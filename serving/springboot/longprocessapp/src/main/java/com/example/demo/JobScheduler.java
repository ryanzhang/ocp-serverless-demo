package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
public class JobScheduler {
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

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
        sendSlackMessage(applicationName + " is started at " + startTime.format(dtFormatter));

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
    }

    public void completeJob() {
        jobRunning = false;
        String totalProcessTime= String.format("Totoal Process Time: %d:%02d", completeDuration / 60, completeDuration % 60);
        sendSlackMessage(applicationName + " is completed at " + LocalDateTime.now().format(dtFormatter) + " " + totalProcessTime);
        System.exit(0); // Exit the application
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
        slackService.sendMessage(message);

    }
}
