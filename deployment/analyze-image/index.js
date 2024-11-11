import { DetectLabelsCommand } from  "@aws-sdk/client-rekognition";
import  { RekognitionClient } from "@aws-sdk/client-rekognition";

import express from 'express';
import { KafkaClient, Consumer, Producer } from 'kafka-node';
import axios from 'axios';
import winston from 'winston';
import client from 'prom-client';
import fs from 'fs';

// Logger setup
const logger = winston.createLogger({
    level: 'info',
    format: winston.format.json(),
    transports: [
        new winston.transports.Console(),
    ],
});

//load config
let config;
try{
    config = JSON.parse(fs.readFileSync(process.cwd() +  '/config.json'))
} catch (error) {
    logger.error({ message: 'Error loading config.yaml ', error: error.message });
    process.exit(1); 
}

// Prometheus metrics
const collectDefaultMetrics = client.collectDefaultMetrics;
collectDefaultMetrics();

const app = express();
const port = config.port || process.env.PORT || 3000;

// AWS Rekognition setup
const bucket = config.bucket; // The bucket name without s3://
const region = config.region; // The AWS region, e.g., 'us-east-1'
const accessKeyId = process.env.AWS_ACCESS_KEY_ID;
const accessKey = process.env.AWS_SECRET_ACCESS_KEY;

// Load AWS credentials
// AWS.config.credentials = new AWS.SharedIniFileCredentials({ profile });
// AWS.config.update({ region });
const rekogClient = new RekognitionClient({
    region: region,
    credentials: {
      accessKeyId: accessKeyId,
      secretAccessKey: accessKey,
    },
  });


// Health Check Endpoint
app.get('/health', (req, res) => {
    res.status(200).json({ status: 'UP' });
});

// Prometheus Metrics Endpoint
app.get('/metrics', async (req, res) => {
    res.set('Content-Type', client.register.contentType);
    res.end(await client.register.metrics());
});

// Kafka Setup
const kafkaClient = new KafkaClient({ kafkaHost: config['kafka-bootstrap-server'] }); 
const consumer = new Consumer(
    kafkaClient,
    [{ topic: config['kafka-incoming-topic'], partition: 0 }],
    { 
        autoCommit: true
    }
);

// Kafka Producer Setup
const producer = new Producer(kafkaClient);

producer.on('ready', () => {
    logger.info('Kafka Producer is ready');
});

producer.on('error', (err) => {
    logger.error({ message: 'Kafka Producer Error', error: err });
});

consumer.on('message', async (message) => {
        let labelsArray;
        let event;
    try {
        logger.info({ message: 'Received event', event });
        event = JSON.parse(message.value);
    }catch (e){
        logger.error({ message: 'Error parser event', error: e.message });
    }
    
    if (event.eventType === 'processThumbsComplete') { // Analyze the image using AWS Rekognition
        try {
            //Check here for example: https://docs.aws.amazon.com/rekognition/latest/dg/images-s3.html
            const params = {
                Image: {
                    S3Object: {
                        Bucket: bucket,
                        Name: event.thumbKey 
                    },
                },
                MaxLabels: 5, // Limit to maximum 5 labels
            };
            const response = await rekogClient.send(new DetectLabelsCommand(params));
            // console.log(response.Labels)
            labelsArray = response.Labels.map(label => label.Name);
            logger.info({ labels: labelsArray})

            } catch (err) {
            console.log("Error in call AWS rekognition api", err);
            }

            // logger.info({ message: 'Labels detected', fileName: event.fileName, labels });
            if (labelsArray != null){

                // Prepare the event to be published
                const analyzeCompleteEvent = {
                    eventType: 'analyzeImageComplete',
                    userId: event.userId,
                    analyzeTime: new Date().toISOString(),
                    fileKey: event.fileKey,
                    thumbKey: event.thumbKey,
                    baseUrl: event.baseUrl,
                    labels: labelsArray // Convert labels array to a string separated by commas
                };
    
                // Publish analyze image complete event
                producer.send([{ topic: config['kafka-outgoing-topic'], messages: JSON.stringify(analyzeCompleteEvent) }], (err, data) => {
                    if (err) {
                        logger.error({ message: 'Error publishing analyze image complete event', error: err.message });
                    } else {
                        logger.info({ message: 'Analyze image complete event published', data });
                        logger.info(JSON.stringify(analyzeCompleteEvent));
                    }
                });
            }
        }
});

consumer.on('error', (err) => {
    logger.error({ message: 'Kafka Consumer Error', error: err });
});


// Start Express Server
app.listen(port, () => {
    logger.info(`Server running on http://localhost:${port}`);
});
