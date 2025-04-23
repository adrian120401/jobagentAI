package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.findjob.job_agent.config.PromptConstants;
import com.findjob.job_agent.model.enums.UserIntent;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntentDetectionService {
    private final ChatCompletionsClient client;

    public IntentDetectionService(ChatCompletionsClient client) {
        this.client = client;
    }

    public UserIntent detectIntent(String userMessage) {
        try {
            String systemPrompt = PromptConstants.INTENT_DETECTION_PROMPT;

            List<ChatRequestMessage> messages = List.of(
                    new ChatRequestSystemMessage(systemPrompt),
                    new ChatRequestUserMessage(userMessage)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");
            options.setMaxTokens(50);
            options.setTemperature(0.5);
            options.setTopP(0.9);


            ChatCompletions completions = client.complete(options);
            String response = completions.getChoices().getFirst().getMessage().getContent().trim();

            return UserIntent.valueOf(response);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid intent response: " + e.getMessage());
            return UserIntent.GENERAL;
        } catch (Exception e) {
            System.out.println("Error while detecting intent: " + e.getMessage());
            return UserIntent.GENERAL;
        }
    }
}
