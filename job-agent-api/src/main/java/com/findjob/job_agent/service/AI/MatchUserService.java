package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findjob.job_agent.model.JobMatchResult;
import com.findjob.job_agent.model.ResumeProfile;
import com.findjob.job_agent.model.entity.JobSearched;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MatchUserService {
    private final ChatCompletionsClient client;

    public MatchUserService(ChatCompletionsClient client) {
        this.client = client;
    }

    public List<JobMatchResult> matchJobsWithProfile(ResumeProfile profile, List<JobSearched> jobs) {
        try {
            String systemMessage = """
                    You are a specialized AI assistant that evaluates job matches in the tech industry.

                    Your task is to analyze a candidate's profile and compare it against a list of tech job offers.
                    The goal is to determine which jobs are potentially suitable based on the candidate’s skills, experience, and background.

                    Instructions:
                    1. Only consider a job relevant if it shares substantial alignment with the candidate’s skills (e.g., programming languages, tools, frameworks, roles).
                    2. Use industry standards to evaluate the relevance of skills (e.g., JavaScript aligns with front-end roles, Python with data-related jobs, etc.).
                    3. For each job that meets the threshold (matchScore > 0.5), return:
                       - `jobId`: the job's ID
                       - `matchScore`: a number between 0.0 and 1.0 indicating how well it matches
                       - `reason`: a short explanation of the match (e.g., “Strong match on backend skills: Java, Spring Boot”)
                    4. Don't repeat jobs, based on job's ID

                    Requirements:
                    - Only include jobs with matchScore > 0.5.
                    - Return only a minified JSON array in one line.
                    - If no matches are found, return an empty array `[]`.
                    - Be precise and avoid hallucinating skills not present in the data.
                    """;

            ObjectMapper mapper = new ObjectMapper();
            String profileJson = mapper.writeValueAsString(profile);
            String jobsJson = mapper.writeValueAsString(jobs);

            String userPrompt = """
                    Candidate profile:
                    %s

                    Jobs:
                    %s
                    """.formatted(profileJson, jobsJson);

            BinaryData data =BinaryData.fromObject(userPrompt);

            List<ChatRequestMessage> messages = List.of(
                    new ChatRequestSystemMessage(systemMessage),
                    new ChatRequestUserMessage(data)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");

            ChatCompletions completions = client.complete(options);
            String responseContent = completions.getChoices().getFirst().getMessage().getContent();

            return mapper.readValue(responseContent, new TypeReference<>() {
            });
        } catch (Exception e) {
            System.out.println("Error matching jobs: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
