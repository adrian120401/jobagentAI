package com.findjob.job_agent.service;

import com.findjob.job_agent.model.UserIntent;
import com.findjob.job_agent.model.dto.ChatResponse;
import com.findjob.job_agent.model.JobInformation;
import com.findjob.job_agent.model.JobMatchResult;
import com.findjob.job_agent.model.ResumeProfile;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.service.AI.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final MatchUserService matchUserService;
    private final UserService userService;
    private final JobSearchedService jobSearchedService;
    private final IntentDetectionService intentDetectionService;
    private final JobAnalyzeDetailService jobAnalyzeDetailService;
    private final ResumeAdviceService resumeAdviceService;
    private final GeneralService generalService;

    public ChatService(
            MatchUserService matchUserService,
            UserService userService,
            JobSearchedService jobSearchedService,
            IntentDetectionService intentDetectionService,
            JobAnalyzeDetailService jobAnalyzeDetailService,
            ResumeAdviceService resumeAdviceService,
            GeneralService generalService
    ) {
        this.matchUserService = matchUserService;
        this.userService = userService;
        this.jobSearchedService = jobSearchedService;
        this.intentDetectionService = intentDetectionService;
        this.jobAnalyzeDetailService = jobAnalyzeDetailService;
        this.resumeAdviceService = resumeAdviceService;
        this.generalService = generalService;
    }

    public ChatResponse process(String userMessage, String jobId) {
        UserIntent intent = intentDetectionService.detectIntent(userMessage);

        System.out.println("intent: " + intent.name());
        return switch (intent) {
            case JOB_LISTING -> getJobs();
            case JOB_DETAIL -> getJobDetail(userMessage, jobId);
            case CV_ADVICE -> getResumeAdvice(userMessage, jobId);
            case GENERAL -> getGeneralResponse(userMessage);
        };
    }

    public ChatResponse getJobs() {
        ResumeProfile resumeProfile = userService.getUserById().getResumeProfile();
        List<JobSearched> jobs = jobSearchedService.getAllJobs();
        List<JobSearched> firstFiveJobs = jobs.size() > 5 ? jobs.subList(0, 5) : jobs;

        List<JobMatchResult> matchedResult = matchUserService.matchJobsWithProfile(resumeProfile, firstFiveJobs);
        List<JobMatchResult> result = matchedResult.stream().map(jobMatch -> {
            JobSearched jobSearched = jobSearchedService.getById(jobMatch.getJobId());
            JobInformation jobInformation = new JobInformation();
            jobInformation.setJobId(jobSearched.getIdJob());
            jobInformation.setTitle(jobSearched.getTitle());
            jobInformation.setDescription(jobSearched.getDescription());
            jobInformation.setBenefits(jobSearched.getBenefits());
            jobInformation.setJobUrl(jobSearched.getJobUrl());
            jobInformation.setCompanyName(jobSearched.getCompanyName());
            jobInformation.setCompanyLogo(jobSearched.getCompanyLogo());
            jobMatch.setJob(jobInformation);
            return jobMatch;
        }).toList();
        ChatResponse response = new ChatResponse();
        response.setJobs(result);
        return response;
    }

    public ChatResponse getJobDetail(String userMessage, String jobId) {
        JobSearched jobSearched = jobSearchedService.getById(jobId);
        ResumeProfile resumeProfile = userService.getUserById().getResumeProfile();

        String details = jobAnalyzeDetailService.analyzeJobDetail(userMessage, jobSearched, resumeProfile);
        ChatResponse response = new ChatResponse();
        response.setMessage(details);
        return response;
    }

    public ChatResponse getResumeAdvice(String userMessage, String jobId) {
        JobSearched jobSearched = jobId.isBlank() ? jobSearchedService.getById(jobId) : null;
        ResumeProfile resumeProfile = userService.getUserById().getResumeProfile();

        String advices = resumeAdviceService.adviseOnResume(userMessage, resumeProfile, jobSearched);
        ChatResponse response = new ChatResponse();
        response.setMessage(advices);
        return response;
    }

    public ChatResponse getGeneralResponse(String userMessage) {
        ResumeProfile resumeProfile = userService.getUserById().getResumeProfile();

        String generalResponse = generalService.askGeneralQuestion(userMessage, resumeProfile);
        ChatResponse response = new ChatResponse();
        response.setMessage(generalResponse);
        return response;
    }
}
