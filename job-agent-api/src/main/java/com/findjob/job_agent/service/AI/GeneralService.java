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
public class GeneralService {
    private final ChatCompletionsClient client;

    public GeneralService(ChatCompletionsClient client) {
        this.client = client;
    }

    public String askGeneralQuestion(String userMessage, ResumeProfile resume, JobSearched jobSearched, String summary) {
        try {
            String systemPrompt = PromptConstants.GENERAL_QUESTION_PROMPT;

            ObjectMapper mapper = new ObjectMapper();
            String resumeJson = mapper.writeValueAsString(resume);
            String jobJson = mapper.writeValueAsString(jobSearched);
            String prompt = """
            User question:
            %s

            Resume data (use only if relevant):
            %s

            Job data (use only if relevant):
            %s

            Conversation summary:
            %s
            """.formatted(userMessage, resumeJson, jobJson, summary);

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
            System.out.println("Error in general question: " + e.getMessage());
            return "<p>There was an error processing your question.</p>";
        }
    }

}
