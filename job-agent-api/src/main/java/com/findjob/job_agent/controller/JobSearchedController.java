package com.findjob.job_agent.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.service.JobSearchedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
public class JobSearchedController {
    private final JobSearchedService service;

    public JobSearchedController(JobSearchedService service) {
        this.service = service;
    }

    @PutMapping("")
    public ResponseEntity<Map<String, String>> updateJobs() throws JsonProcessingException {
        service.updateJobsByAPI();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Jobs already updated");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("")
    public ResponseEntity<List<JobSearched>> getAll(){
        return ResponseEntity.ok(service.getAllJobs());
    }
}
