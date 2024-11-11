package com.example.demo;

import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Date;

@ApplicationScoped
public class ImageEventConsumer {

    @Inject
    ImageEventProducer imageEventProducer;  // Inject the producer
    private static final Logger logger = Logger.getLogger(ImageEventConsumer.class);

    @Incoming("analyze-image-topic") // Name of the Kafka topic where analyzeImageComplete events are published
    @Outgoing("save-image-meta-complete-topic") // Kafka topic to publish the saveImageMetaCompleteEvent
    @Blocking // Marks the method as blocking since MongoDB persistence is IO-bound
    public String onAnalyzeImageCompleteEvent(AnalyzeImageEvent event) {
        if ("analyzeImageComplete".equals(event.eventType)) {
            logger.info("Received event:" + event);
            try {
                Image existImage =Image.findByFileKey(event.fileKey);
                // logger.info("Check if image exist :" + existImage);
                if(existImage == null){
                    Image image = new Image();
                    image.fileKey = event.fileKey;
                    image.thumbKey = event.thumbKey;
                    image.url = event.baseUrl + event.fileKey;
                    image.thumbUrl = event.baseUrl + event.thumbKey;
                    image.labels = event.labels;
                    image.uploadedAt = new Date();
                    image.updatedAt = new Date();
                    image.userObjectId = User.findByUserId(event.userId).id;

                    // Persist the image in MongoDB
                    image.persist();
                    // Log success
                    logger.info("Successfully saved image metadata for fileKey: " +  event.fileKey);

                    // Publish the saveImageMetaCompleteEvent to Kafka
                    imageEventProducer.userId = event.userId;
                    imageEventProducer.fileKey = event.fileKey;
                    // imageEventProducer.publishSaveImageMetaCompleteEvent();
                    SaveImageMetaCompleteEvent saveEvent = new SaveImageMetaCompleteEvent();
                    saveEvent.fileKey = event.fileKey;
                    saveEvent.userId = event.userId;
                    saveEvent.publishAt = new Date();
                    return saveEvent.toJson();
                }else{
                logger.info("Image already existed, Ignore persist"); 
                }

            } catch (Exception e) {
                logger.error("Failed to save image metadata:" + e.getMessage());
            }
        }
            return "";
    }

}
