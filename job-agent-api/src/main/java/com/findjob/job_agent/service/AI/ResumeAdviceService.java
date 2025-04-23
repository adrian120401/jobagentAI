package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findjob.job_agent.model.dto.ResumeProfile;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.config.PromptConstants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeAdviceService {
    private final ChatCompletionsClient client;

    public ResumeAdviceService(ChatCompletionsClient client) {
        this.client = client;
    }

    public String adviseOnResume(String userMessage, ResumeProfile resume, JobSearched job, String summary) {
        try {
            String systemPrompt = PromptConstants.RESUME_ADVICE_PROMPT;

            ObjectMapper mapper = new ObjectMapper();
            String resumeJson = mapper.writeValueAsString(resume);
            String jobJson = (job != null) ? mapper.writeValueAsString(job) : "";

            String prompt = """
            User message:
            %s

            Resume data:
            %s

            Job data:
            %s

            Conversation summary:
            %s
            """.formatted(
                    userMessage,
                    resumeJson,
                    jobJson,
                    summary
            );

            BinaryData data = BinaryData.fromObject(prompt);
            List<ChatRequestMessage> messages = List.of(
                    new ChatRequestSystemMessage(systemPrompt),
                    new ChatRequestUserMessage(data)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");
            options.setTemperature(0.7);
            options.setTopP(0.9);
            options.setFrequencyPenalty(0.5);

            ChatCompletions completions = client.complete(options);
            return completions.getChoices().getFirst().getMessage().getContent().trim();

        } catch (Exception e) {
            System.out.println("Error in resume advice: " + e.getMessage());
            return "<p>Error processing resume advice.</p>";
        }
    }
}
