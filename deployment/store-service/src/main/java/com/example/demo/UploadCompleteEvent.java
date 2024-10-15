package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadCompleteEvent {
    private String fileName;
    private String fileUrl;
    private long fileSize;
    private String uploadTime;
    private String eventType;
}
