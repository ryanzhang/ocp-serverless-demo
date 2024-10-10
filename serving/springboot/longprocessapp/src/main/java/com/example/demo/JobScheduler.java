package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
public class JobScheduler {
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Value("${complete.duration}")
    private long completeDuration; // in seconds

    private LocalDateTime startTime;
    private boolean jobRunning = false;

    public void startJob() {
        startTime = LocalDateTime.now();
        jobRunning = true;
        sendSlackMessage("Job is started at " + startTime.format(dtFormatter));

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
        sendSlackMessage("Job is completed at " + LocalDateTime.now().format(dtFormatter));
        System.exit(0); // Exit the application
    }

    public String getJobInfo() {
        if (jobRunning) {
            long elapsedSeconds = TimeUnit.SECONDS.convert(LocalDateTime.now().minusSeconds(startTime.toEpochSecond() - LocalDateTime.now().toEpochSecond()), TimeUnit.SECONDS);
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;
            return String.format("Start time: %s, Elapsed time: %d:%02d, Complete duration: %d:%02d",
                    startTime.format(dtFormatter), minutes, seconds, completeDuration / 60, completeDuration % 60);
        }
        return "Job is not running.";
    }

    private void sendSlackMessage(String message) {
        // Implement the code to send a message to Slack here
        System.out.println("Sending to Slack: " + message);
    }
}
