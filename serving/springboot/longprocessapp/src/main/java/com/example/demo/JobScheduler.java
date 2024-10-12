package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class JobScheduler {
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Value("${keepalive_url:}")
    private String keepalive_url;

    @Value("${complete.duration}")
    private long completeDuration; // in seconds

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private SlackService slackService; 



    private LocalDateTime startTime;
    private boolean jobRunning = false;

    public void startJob() {
        startTime = LocalDateTime.now();
        jobRunning = true;

        // Simulate sending a Slack message
        sendSlackMessage(applicationName + " Job is started at " + startTime.format(dtFormatter) );

        new Thread(() -> {
            try {
                for (int i = 0; i <= completeDuration; i++) {
                    TimeUnit.SECONDS.sleep(1);
                    
                    if (!jobRunning) break; // Exit if job is stopped
                }
                completeJob();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        //keepalive
        new Thread(() -> {
            try {
                if (keepalive_url!="" && jobRunning)
                    for (int i = 0; i <= completeDuration; i++){
                        TimeUnit.SECONDS.sleep(1);
                        if (!jobRunning) break;
                        if(i%20==0)
                            sendServerlessIngressTraffic(keepalive_url);
                    }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void completeJob() {
        jobRunning = false;
        String totalProcessTime= String.format("Total Process Time: %d:%02d", completeDuration / 60, completeDuration % 60);
        sendSlackMessage(applicationName + " Job is completed at " + LocalDateTime.now().format(dtFormatter) 
            + " " + totalProcessTime);
        // System.exit(0); // Exit the application
    }
    public boolean isJobRunning() {
        return jobRunning;
    }
    public String getJobInfo() {
        if (jobRunning) {
            // Calculate elapsed time using Duration
            Duration elapsed = Duration.between(startTime, LocalDateTime.now());
            long minutes = elapsed.toMinutes();
            long seconds = elapsed.getSeconds() % 60; // Get remaining seconds after minutes
            
            return String.format("Start time: %s, Elapsed time: %d:%02d, Complete duration: %d:%02d",
                    startTime.format(dtFormatter), minutes, seconds, completeDuration / 60, completeDuration % 60);
        }
        return "Job is not running.";
    }

    private void sendSlackMessage(String message) {
        // Implement the code to send a message to Slack here
        slackService.sendMessage(message );

    }

    private int sendServerlessIngressTraffic(String keepalive_url) {

        RestTemplate restTemplate = new RestTemplate();

        try {
            URL url = new URL(keepalive_url);
            // Send GET request to the keepalive URL
            ResponseEntity<String> response = restTemplate.getForEntity(url.toURI(), String.class);
            System.out.println("Send http requeset to " + keepalive_url);

            // Return the HTTP status code
            return response.getStatusCode().value();

        } catch (RestClientException e) {
            // Print stacktrace for any errors
            // You can also return a specific error code if needed, e.g. 500 for server error
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }catch (MalformedURLException e){
            return HttpStatus.BAD_REQUEST.value(); 
        }catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }
}
