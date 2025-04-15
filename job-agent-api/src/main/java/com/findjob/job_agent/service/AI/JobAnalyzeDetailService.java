package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findjob.job_agent.model.ResumeProfile;
import com.findjob.job_agent.model.entity.JobSearched;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobAnalyzeDetailService {
    private final ChatCompletionsClient client;

    public JobAnalyzeDetailService(ChatCompletionsClient client) {
        this.client = client;
    }

    public String analyzeJobDetail(String userMessage, JobSearched jobInfo, ResumeProfile resumeProfile) {
        try {
            String systemPrompt = """
            You are an AI assistant that provides job detail insights to the user, based on a specific question or prompt.

            Instructions:
            - Use the job information provided below to answer the user's message.
            - If resume information is provided, use it to personalize the response (e.g., comparing requirements with user's profile).
            - Job fields such as description or functions may contain HTML tags or formatting. Ignore all HTML content and focus only on the textual meaning.
            - Respond in clean and valid HTML (no JavaScript or CSS). Only include the relevant content, like <p>, <ul>, <li>, <b>, <strong>, <h2>, etc.
            - Do not generate links unless there's a `jobUrl` provided.
            - If a value is missing or not relevant to the user's question, omit it silently.
            - Make the output readable and user-friendly.

            Always respond in English.
            """;

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
            """.formatted(userMessage, jobJson, resumeJson);

            BinaryData data = BinaryData.fromObject(userPrompt);

            List<ChatRequestMessage> messages = List.of(
                    new ChatRequestSystemMessage(systemPrompt),
                    new ChatRequestUserMessage(data)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");

            ChatCompletions completions = client.complete(options);
            return completions.getChoices().getFirst().getMessage().getContent().trim();

        } catch (Exception e) {
            System.out.println("Error analyzing job detail: " + e.getMessage());
            return "<p>Error analyzing job detail.</p>";
        }
    }
}
