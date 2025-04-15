package com.findjob.job_agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.findjob.job_agent.exception.NotFoundException;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.repository.JobSearchedRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSearchedService {
    private final JobSearchedRepository repository;
    private final SearchJobsAPIService searchJobsAPIService;

    public JobSearchedService(JobSearchedRepository repository, SearchJobsAPIService searchJobsAPIService) {
        this.repository = repository;
        this.searchJobsAPIService = searchJobsAPIService;
    }

    public void updateJobsByAPI() throws JsonProcessingException {
        List<JobSearched> jobSearched = searchJobsAPIService.getGetonBoardJobs();
        repository.saveAll(jobSearched);
    }

    public List<JobSearched> getAllJobs(){
        return repository.findAll();
    }

    public JobSearched getById(String id){
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Job not found"));
    }

}
