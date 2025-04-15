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
public class ResumeAdviceService {
    private final ChatCompletionsClient client;

    public ResumeAdviceService(ChatCompletionsClient client) {
        this.client = client;
    }

    public String adviseOnResume(String userMessage, ResumeProfile resume, JobSearched job) {
        try {
            String systemPrompt = """
            You are an expert career advisor specializing in technical CV/resume improvement.

            Your job is to analyze a candidate’s resume and give clear, structured advice based on the user's message.

            Instructions:
            - Always reply in English.
            - Output must be clean, basic HTML (<p>, <ul>, <li>, <h2>, etc.).
            - If a job is provided, tailor the suggestions to fit that job, but avoid hallucinating or assuming things not present.
            - Job data may contain HTML (e.g. description, benefits, functions). Ignore all HTML formatting and use only the raw textual content.
            - If no job is provided, give general advice based on the resume only.
            - Do not expose raw JSON or code. Only return helpful suggestions in user-friendly HTML.
            """;

            ObjectMapper mapper = new ObjectMapper();
            String resumeJson = mapper.writeValueAsString(resume);
            String jobJson = (job != null) ? mapper.writeValueAsString(job) : "";

            String prompt = """
            User message:
            %s

            Resume data:
            %s

            %s
            """.formatted(
                    userMessage,
                    resumeJson,
                    job != null ? "Job data:\n" + jobJson : ""
            );

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
            System.out.println("Error in resume advice: " + e.getMessage());
            return "<p>Error processing resume advice.</p>";
        }
    }
}
