package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.findjob.job_agent.model.dto.JobMatchResult;
import com.findjob.job_agent.config.PromptConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findjob.job_agent.model.dto.ResumeProfile;
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
            String systemMessage = PromptConstants.JOB_MATCH_PROMPT;

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
