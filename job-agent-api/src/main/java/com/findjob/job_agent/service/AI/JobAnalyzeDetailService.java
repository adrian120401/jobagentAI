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
public class JobAnalyzeDetailService {
    private final ChatCompletionsClient client;

    public JobAnalyzeDetailService(ChatCompletionsClient client) {
        this.client = client;
    }

    public String analyzeJobDetail(String userMessage, JobSearched jobInfo, ResumeProfile resumeProfile, String summary) {
        try {
            String systemPrompt = PromptConstants.JOB_ANALYZE_DETAIL_PROMPT;

            ObjectMapper mapper = new ObjectMapper();
            String jobJson = mapper.writeValueAsString(jobInfo);
            String resumeJson = mapper.writeValueAsString(resumeProfile);

            String userPrompt = """
            User message:
            %s

            Job data:
            %s

            Resume data (optional, use if helpful):
            %s

            Conversation summary:
            %s
            """.formatted(userMessage, jobJson, resumeJson, summary);

            BinaryData data = BinaryData.fromObject(userPrompt);

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
            System.out.println("Error analyzing job detail: " + e.getMessage());
            return "<p>Error analyzing job detail.</p>";
        }
    }
}
