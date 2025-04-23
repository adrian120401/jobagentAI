package com.findjob.job_agent.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import com.findjob.job_agent.model.dto.JobInformation;
import com.findjob.job_agent.model.dto.JobMatchResult;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.model.entity.User;
import com.findjob.job_agent.service.UserService;
import com.findjob.job_agent.service.AI.MatchUserService;
import com.findjob.job_agent.service.JobSearchedService;
import com.findjob.job_agent.service.EmailService;
import org.springframework.stereotype.Component;

@Component
public class JobSuggestionScheduler {
    private UserService userService;
    private MatchUserService matchUserService;
    private JobSearchedService jobSearchedService;
    private EmailService emailService;

    public JobSuggestionScheduler(UserService userService, MatchUserService matchUserService, JobSearchedService jobSearchedService, EmailService emailService) {
        this.userService = userService;
        this.matchUserService = matchUserService;
        this.jobSearchedService = jobSearchedService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void suggestJobs(){
        User user = userService.getFirstUser();
        List<JobSearched> jobs = jobSearchedService.getAllJobs();
        List<JobSearched> firstFiveJobs = jobs.size() > 5 ? jobs.subList(0, 5) : jobs;
        List<JobMatchResult> jobMatchResults = matchUserService.matchJobsWithProfile(user.getResumeProfile(), firstFiveJobs);

        List<JobMatchResult> result = jobMatchResults.stream().map(jobMatch -> {
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

        emailService.sendJobMatchesEmail(user, result);
    }
}
