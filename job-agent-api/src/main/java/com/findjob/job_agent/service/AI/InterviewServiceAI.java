package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findjob.job_agent.model.dto.InterviewSession;
import com.findjob.job_agent.model.entity.Interview;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.service.InterviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewServiceAI {
    private final ChatCompletionsClient client;
    private final InterviewService interviewService;

    public InterviewServiceAI(ChatCompletionsClient client, InterviewService interviewService) {
        this.client = client;
        this.interviewService = interviewService;
    }

    public InterviewSession nextInterviewStep(JobSearched job, List<InterviewSession> history, String userId) {
        try {
            String systemPrompt = """
                    You are an expert technical interviewer for IT jobs.

                    Your task is to simulate a technical interview based on the job context and the interview history.

                    Instructions:
                    - Always reply in English.
                    - If this is the first step (step 0), the 'answer' field in the history is not an answer to an interview question, but may contain preferences, guidance, or context for the interview (for example: 'Yes, let's start, base it on a semi-senior role'). Use this information to tailor the first interview question, but do not generate feedback in step 0, regardless of the answer content.
                    - Only generate a relevant first question for the position in step 0.
                    - If there is a previous answer (step > 0), first generate brief, constructive feedback about that answer, then a new question.
                    - Feedback must be clear, specific, and help the candidate improve.
                    - The question must be related to the job and should not be repeated.
                    - Additionally, return a general interview summary object with two fields: 'score' (a number between 0.0 and 1.0 representing the overall interview performance so far) and 'feedback' (a concise summary of the candidate's performance so far, in English).
                    - Reply in JSON format: {\"question\":\"...\", \"previousFeedback\":\"...\", \"interview\": {\"score\": 0.0, \"feedback\": \"...\"}} (the previousFeedback field can be empty if this is the first step, interview is the general summary object).
                    - Do not include any explanations or text outside the JSON.
                    """;

            ObjectMapper mapper = new ObjectMapper();
            String jobJson = mapper.writeValueAsString(job);
            StringBuilder historyBuilder = new StringBuilder();

            if (history != null && !history.isEmpty()) {
                for (InterviewSession session : history) {
                    historyBuilder.append("Question: ").append(session.getQuestion()).append("\n");
                    if (session.getAnswer() != null && !session.getAnswer().isBlank()) {
                        historyBuilder.append("Answer: ").append(session.getAnswer()).append("\n");
                    }
                    if (session.getPreviousFeedback() != null && !session.getPreviousFeedback().isBlank()) {
                        historyBuilder.append("Feedback: ").append(session.getPreviousFeedback()).append("\n");
                    }
                }
            }

            String prompt = """
                    Job context:
                    %s

                    Interview history:
                    %s
                    """.formatted(jobJson, historyBuilder.toString());

            BinaryData data = BinaryData.fromObject(prompt);
            List<ChatRequestMessage> messages = List.of(
                    new ChatRequestSystemMessage(systemPrompt),
                    new ChatRequestUserMessage(data));

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");

            ChatCompletions completions = client.complete(options);
            String content = completions.getChoices().get(0).getMessage().getContent().trim();

            InterviewSession result = mapper.readValue(content, InterviewSession.class);
            int nextStep = (history == null) ? 0 : history.size();
            result.setStep(nextStep);

            Interview interview = null;
            if (history != null && !history.isEmpty()) {
                InterviewSession lastSession = history.get(history.size() - 1);
                if (lastSession.getInterview() == null || lastSession.getInterview().getId() == null) {
                    interview = new Interview(userId, job.getId(), result.getInterview().getScore(), result.getInterview().getFeedback());
                    interview = interviewService.create(interview);
                } else {
                    interview = lastSession.getInterview();
                    interview.setScore(result.getInterview().getScore());
                    interview.setFeedback(result.getInterview().getFeedback());
                }
            } else {
                interview = new Interview(userId, job.getId(), result.getInterview().getScore(), result.getInterview().getFeedback());
                interview = interviewService.create(interview);
            }
            result.setInterview(interview);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            InterviewSession errorSession = new InterviewSession();
            errorSession.setQuestion("Error generating the next interview question.");
            errorSession.setPreviousFeedback("");
            errorSession.setStep((history == null) ? 0 : history.size());
            return errorSession;
        }
    }
}
