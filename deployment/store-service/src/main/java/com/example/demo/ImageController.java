package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.demo.KafkaProducerService;
import com.example.demo.UploadCompleteEvent;


@RestController
public class ImageController {
    private final S3Client s3Client;
    private final String bucketName = "ryan-workshop";
    private final String uploadFolder = "upload";
    private final String thumbsFolder = "thumb";

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Autowired
    public ImageController(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uploadFolder + "/" + file.getOriginalFilename())
                    .build();

            // Upload the file to S3
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            URL theUrl = s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(uploadFolder + "/" + file.getOriginalFilename())
                .build());

            // Create and send Kafka event
            String fileName = file.getOriginalFilename();
            String fileUrl = theUrl.toString();
            long fileSize = file.getSize();
            String uploadTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            UploadCompleteEvent uploadEvent = new UploadCompleteEvent(fileName, fileUrl, fileSize, uploadTime, "uploadComplete");

            kafkaProducerService.sendUploadCompleteEvent(uploadEvent);
            // String publicUrl = theUrl.toString();
            // sendUploadCompleteEvent(publicUrl);
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/fetch_thumbs")
    public ResponseEntity<List<Map<String, String>>> fetchThumbnails() {
        // Fetch the list of objects from the S3 bucket with the given prefix
        List<S3Object> thumbnails = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(thumbsFolder + "/")
                .build()).contents();

        // Filtering out folders and mapping each object to a JSON object containing 'name' and 'url'
        List<Map<String, String>> thumbnailDetails = thumbnails.stream()
                .filter(thumbnail -> !thumbnail.key().endsWith("/")) // Exclude directories
                .map(thumbnail -> {
                    Map<String, String> thumbDetail = new HashMap<>();
                    String fileName = thumbnail.key().substring(thumbsFolder.length() + 1); // Extract the file name
                    String publicUrl = s3Client.utilities().getUrl(GetUrlRequest.builder()
                            .bucket(bucketName)
                            .key(thumbnail.key())
                            .build()).toExternalForm(); // Generate the public URL
                    
                    thumbDetail.put("name", fileName); // Set the name
                    thumbDetail.put("url", publicUrl); // Set the public URL
                    return thumbDetail;
                })
                .collect(Collectors.toList());

        // Return the response with the formatted list of thumbnails
        return ResponseEntity.ok(thumbnailDetails);
    }
}
