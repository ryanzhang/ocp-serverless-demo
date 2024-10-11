package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class GracefulShutdown {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private SlackService slackService; 
    
    @PostConstruct
    public void init(){
        String message = applicationName + " is starting at " + getCurrentTime();
        slackService.sendMessage(message);
    }
    public GracefulShutdown() {
        // Add a shutdown hook for normal exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            String message = applicationName + " is exiting at " + getCurrentTime();
            slackService.sendMessage(message);
        }));
    }

    @jakarta.annotation.PreDestroy
    public void onDestroy() {
        String message = applicationName + " is terminating by the Platform at " + getCurrentTime();
        slackService.sendMessage(message);
        // Perform any additional cleanup tasks here
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
