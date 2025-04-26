package com.findjob.job_agent.model.mapper;

import com.findjob.job_agent.model.dto.JobInformation;
import com.findjob.job_agent.model.entity.JobSearched;

public class JobMapper {
    public static JobInformation toJobInformation(JobSearched jobSearched){
        JobInformation job =  new JobInformation();
        job.setJobId(jobSearched.getIdJob());
        job.setTitle(jobSearched.getTitle());
        job.setDescription(jobSearched.getDescription());
        job.setJobUrl(jobSearched.getJobUrl());
        job.setCompanyName(jobSearched.getCompanyName());
        job.setCompanyLogo(jobSearched.getCompanyLogo());
        return job;
    }
}
