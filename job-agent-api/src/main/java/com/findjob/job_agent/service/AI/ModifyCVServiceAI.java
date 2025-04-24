package com.findjob.job_agent.service.AI;

import org.springframework.stereotype.Service;
import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findjob.job_agent.model.dto.ResumeProfile;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.config.PromptConstants;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@Service
public class ModifyCVServiceAI {
    private final ChatCompletionsClient client;
    private final ResumeAdviceService resumeAdviceService;

    @Autowired
    public ModifyCVServiceAI(ChatCompletionsClient client, ResumeAdviceService resumeAdviceService) {
        this.client = client;
        this.resumeAdviceService = resumeAdviceService;
    }

    public List<Map<String, String>> getCVModifications(String cvText, String userMessage, ResumeProfile resume,
            JobSearched job, String summary) {
        try {
            String recommendations = resumeAdviceService.adviseOnResume(userMessage, resume, job, summary);

            String prompt = """
                    CV text:
                    %s

                    Improvement recommendations:
                    %s
                    """.formatted(cvText, recommendations);

            BinaryData data = BinaryData.fromObject(prompt);
            List<ChatRequestMessage> messages = List.of(
                    new ChatRequestSystemMessage(PromptConstants.CV_MODIFICATION_PROMPT),
                    new ChatRequestUserMessage(data));

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");
            options.setTemperature(0.5);
            options.setTopP(0.9);
            options.setFrequencyPenalty(0.3);

            ChatCompletions completions = client.complete(options);
            String aiResponse = completions.getChoices().getFirst().getMessage().getContent().trim();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(aiResponse, new TypeReference<List<Map<String, String>>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error getting CV modifications from AI", e);
        }
    }
}
