package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findjob.job_agent.model.dto.InterviewSession;
import com.findjob.job_agent.model.entity.JobSearched;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewService {
    private final ChatCompletionsClient client;

    public InterviewService(ChatCompletionsClient client) {
        this.client = client;
    }

    public InterviewSession nextInterviewStep(JobSearched job, List<InterviewSession> history) {
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
                    - Reply in JSON format: {\"question\":\"...\", \"previousFeedback\":\"...\"} (the previousFeedback field can be empty if this is the first step, previousFeedback is the field for feedback).
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
