package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    @Autowired
    private JobScheduler jobScheduler;

    @GetMapping("/start")
    public String startJob() {
        if(!jobScheduler.isJobRunning()){
            jobScheduler.startJob();
        }
        return jobScheduler.getJobInfo();
    }
}
