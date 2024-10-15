package com.example.demo;

import com.example.demo.UploadCompleteEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, UploadCompleteEvent> kafkaTemplate;

    @Value("${kafka.topic.upload-complete}")
    private String uploadCompleteTopic;

    public KafkaProducerService(KafkaTemplate<String, UploadCompleteEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUploadCompleteEvent(UploadCompleteEvent event) {
        kafkaTemplate.send(uploadCompleteTopic, event);
        System.out.println("Uploaded event sent to Kafka topic: " + uploadCompleteTopic);
    }
}
