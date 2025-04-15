package com.findjob.job_agent.repository;

import com.findjob.job_agent.model.entity.JobSearched;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobSearchedRepository extends MongoRepository<JobSearched, String> {
}
