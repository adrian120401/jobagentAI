package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findjob.job_agent.model.ResumeProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneralService {
    private final ChatCompletionsClient client;

    public GeneralService(ChatCompletionsClient client) {
        this.client = client;
    }

    public String askGeneralQuestion(String userMessage, ResumeProfile resume) {
        try {
            String systemPrompt = """
            You are a professional career assistant in the tech industry.

            You must respond clearly and helpfully to user questions, always in English.

            Rules:
            - Use the user's resume information as context if it helps, but don't force it.
            - The question may or may not relate to the user's profile.
            - Respond in simple, clean HTML (<p>, <ul>, <h2>, etc.).
            - Do not output raw JSON, code, or markdown.
            """;

            ObjectMapper mapper = new ObjectMapper();
            String resumeJson = mapper.writeValueAsString(resume);

            String prompt = """
            User question:
            %s

            Resume data (use only if relevant):
            %s
            """.formatted(userMessage, resumeJson);

            BinaryData data = BinaryData.fromObject(prompt);
            List<ChatRequestMessage> messages = List.of(
                    new ChatRequestSystemMessage(systemPrompt),
                    new ChatRequestUserMessage(data)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");

            ChatCompletions completions = client.complete(options);
            return completions.getChoices().getFirst().getMessage().getContent().trim();

        } catch (Exception e) {
            System.out.println("Error in general question: " + e.getMessage());
            return "<p>There was an error processing your question.</p>";
        }
    }

}
