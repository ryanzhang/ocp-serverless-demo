package com.example.demo;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Date;

@ApplicationScoped
public class ImageEventProducer {
    public String fileKey;
    public String userId;

    private static final Logger LOG = Logger.getLogger(ImageEventProducer.class);

    // @Outgoing("save-image-meta-complete-topic") // Kafka topic to publish the saveImageMetaCompleteEvent
    public String publishSaveImageMetaCompleteEvent() {
        SaveImageMetaCompleteEvent saveEvent = new SaveImageMetaCompleteEvent();
        saveEvent.fileKey = fileKey;
        saveEvent.userId = userId;
        saveEvent.publishAt = new Date();
        LOG.info("Publishing saveImageMetaCompleteEvent for fileKey: " + fileKey);
        return saveEvent.toJson();  // Convert event to JSON for Kafka
    }
}
