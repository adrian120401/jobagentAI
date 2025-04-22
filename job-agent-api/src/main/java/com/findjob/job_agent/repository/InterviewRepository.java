package com.findjob.job_agent.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.findjob.job_agent.model.entity.Interview;

public interface InterviewRepository extends MongoRepository<Interview, String> {
    List<Interview> findByUserId(String userId);
}
