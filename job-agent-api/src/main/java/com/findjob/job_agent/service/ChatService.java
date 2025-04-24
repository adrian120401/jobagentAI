package com.findjob.job_agent.service;

import com.findjob.job_agent.model.enums.Sender;
import com.findjob.job_agent.model.enums.UserIntent;
import com.findjob.job_agent.model.dto.ChatResponse;
import com.findjob.job_agent.model.dto.InterviewSession;
import com.findjob.job_agent.model.dto.JobMatchResult;
import com.findjob.job_agent.model.dto.ResumeProfile;
import com.findjob.job_agent.model.dto.JobInformation;
import com.findjob.job_agent.model.entity.Conversation;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.model.entity.Message;
import com.findjob.job_agent.model.entity.User;
import com.findjob.job_agent.service.AI.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final InterviewServiceAI interviewService;
    private final ConversationService conversationService;
    private final SummaryServiceAI summaryService;
    private final FileService fileService;

    public ChatService(
            MatchUserService matchUserService,
            UserService userService,
            JobSearchedService jobSearchedService,
            IntentDetectionService intentDetectionService,
            JobAnalyzeDetailService jobAnalyzeDetailService,
            ResumeAdviceService resumeAdviceService,
            GeneralService generalService,
            InterviewServiceAI interviewService,
            ConversationService conversationService,
            SummaryServiceAI summaryService,
            FileService fileService) {
        this.matchUserService = matchUserService;
        this.userService = userService;
        this.jobSearchedService = jobSearchedService;
        this.intentDetectionService = intentDetectionService;
        this.jobAnalyzeDetailService = jobAnalyzeDetailService;
        this.resumeAdviceService = resumeAdviceService;
        this.generalService = generalService;
        this.interviewService = interviewService;
        this.conversationService = conversationService;
        this.summaryService = summaryService;
        this.fileService = fileService;
    }

    public ChatResponse process(String userMessage, String jobId) throws IOException {
        if (jobId != null) {
            userMessage = userMessage.concat(" jobId: " + jobId);
        }
        UserIntent intent = intentDetectionService.detectIntent(userMessage);

        Conversation conversation = conversationService.getByUserId(userService.getAuthUser().getId());
        if (intent != UserIntent.JOB_LISTING) {
            conversation.getMessages().add(new Message(userMessage, Sender.USER));
        }

        System.out.println("Intent: " + intent);
        ChatResponse response = switch (intent) {
            case JOB_LISTING -> getJobs();
            case JOB_DETAIL -> getJobDetail(userMessage, jobId, conversation.getSummary());
            case RESUME_ADVICE -> getResumeAdvice(userMessage, jobId, conversation.getSummary());
            case MODIFY_RESUME -> getCVModification(userMessage, jobId, conversation.getSummary());
            case GENERAL -> getGeneralResponse(userMessage, jobId, conversation.getSummary());
        };

        if (intent != UserIntent.JOB_LISTING) {
            conversation.getMessages().add(new Message(response.getMessage(), Sender.AGENT));

            if (shouldSummarize(conversation)) {
                String summary = summaryService.summarizeConversation(conversation);
                conversation.setSummary(summary);
            }

            conversationService.create(conversation);
        }
        return response;
    }

    public ChatResponse getJobs() {
        ResumeProfile resumeProfile = userService.getAuthUser().getResumeProfile();
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

    public ChatResponse getJobDetail(String userMessage, String jobId, String summary) {
        JobSearched jobSearched = jobSearchedService.getById(jobId);
        ResumeProfile resumeProfile = userService.getAuthUser().getResumeProfile();

        String details = jobAnalyzeDetailService.analyzeJobDetail(userMessage, jobSearched, resumeProfile, summary);
        ChatResponse response = new ChatResponse();
        response.setMessage(details);
        return response;
    }

    public ChatResponse getResumeAdvice(String userMessage, String jobId, String summary) {
        JobSearched jobSearched = jobId != null && !jobId.isBlank() ? jobSearchedService.getById(jobId) : null;
        ResumeProfile resumeProfile = userService.getAuthUser().getResumeProfile();

        String advices = resumeAdviceService.adviseOnResume(userMessage, resumeProfile, jobSearched, summary);
        ChatResponse response = new ChatResponse();
        response.setMessage(advices);
        return response;
    }

    public ChatResponse getCVModification(String userMessage, String jobId, String summary) throws IOException {
        JobSearched jobSearched = jobId != null && !jobId.isBlank() ? jobSearchedService.getById(jobId) : null;
        ResumeProfile resumeProfile = userService.getAuthUser().getResumeProfile();
        byte[] docx = userService.getDocx();
        byte[] newCV = fileService.modifyCV(docx, userMessage, resumeProfile, jobSearched, summary);
        String newCvPath = userService.uploadFile(newCV, "newCV.docx");
        ChatResponse response = new ChatResponse();
        response.setMessage(newCvPath);
        return response;
    }

    public ChatResponse getGeneralResponse(String userMessage, String jobId, String summary) {
        ResumeProfile resumeProfile = userService.getAuthUser().getResumeProfile();
        JobSearched jobSearched = jobId != null && !jobId.isBlank() ? jobSearchedService.getById(jobId) : null;

        String generalResponse = generalService.askGeneralQuestion(userMessage, resumeProfile, jobSearched, summary);
        ChatResponse response = new ChatResponse();
        response.setMessage(generalResponse);
        return response;
    }

    public InterviewSession getInterview(String jobId, List<InterviewSession> history) {
        User user = userService.getAuthUser();
        JobSearched jobSearched = jobSearchedService.getById(jobId);
        return interviewService.nextInterviewStep(jobSearched, history, user.getId());
    }

    private boolean shouldSummarize(Conversation conversation) {
        return conversation.getMessages().size() % 5 == 0;
    }
}
