package com.findjob.job_agent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.findjob.job_agent.model.entity.Interview;
import com.findjob.job_agent.repository.InterviewRepository;

@Service
public class InterviewService {
    private final InterviewRepository interviewRepository;

    public InterviewService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    public Interview create(Interview interview) {
        return interviewRepository.save(interview);
    }

    public List<Interview> findByUserId(String userId) {
        return interviewRepository.findByUserId(userId);
    }
}
