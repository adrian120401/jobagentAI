package com.findjob.job_agent.service.AI;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.*;
import com.findjob.job_agent.model.UserIntent;
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
            String systemPrompt = """
                    You are an AI assistant designed to classify the user's intent based on their message.

                    The valid intents are:
                    - JOB_LISTING: The user is asking for job recommendations, job matches, or wants to find a list of jobs.
                    - JOB_DETAIL: The user is asking about a specific job position, job responsibilities, or a detailed job description.
                    - CV_ADVICE: The user is asking for advice or feedback on their resume, CV, or how to improve their chances.
                    - GENERAL: Anything else that does not fit the above.

                    Instructions:
                    - Analyze the user message carefully.
                    - Respond **only** with one of the following exact values: JOB_LISTING, JOB_DETAIL, CV_ADVICE, GENERAL.
                    - Do **not** include any explanation or additional text.
                    """;

            List<ChatRequestMessage> messages = List.of(
                    new ChatRequestSystemMessage(systemPrompt),
                    new ChatRequestUserMessage(userMessage)
            );

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");


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
