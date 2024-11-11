package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImageMetaCompleteEvent {

    public String fileKey;
    public String userId;
    public Date publishAt;
    public String eventType = "saveImageMetaComplete";

    // Convert to JSON
    public String toJson() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // ISO 8601 format
        String publishAtFormatted = publishAt != null ? dateFormat.format(publishAt) : null;

        return "{"
            + "\"eventType\":\"" + eventType + "\", "
            + "\"fileKey\":\"" + fileKey + "\", "
            + "\"userId\":\"" + userId + "\", "
            + "\"publishAt\":\"" + publishAtFormatted + "\""
            + "}";
    }
}
