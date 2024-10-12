package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SlackService {

    @Value("${slack_token:}")
    private String slackToken;

    @Value("${slack.channel.id}")
    private String channelId;

   @Autowired
    private Environment environment;

    public void sendMessage(String message) {
        message = message +  " from " + environment.getProperty("HOSTNAME");
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://slack.com/api/chat.postMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + slackToken);
        headers.set("Content-Type", "application/json");
        
        String jsonBody = String.format("{\"channel\":\"%s\", \"text\":\"%s\"}", channelId, message);

        // Create the HttpEntity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        System.out.println("Sending to Slack: " + message);

    }
}
