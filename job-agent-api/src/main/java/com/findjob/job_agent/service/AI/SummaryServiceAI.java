package com.findjob.job_agent.service.AI;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.core.util.BinaryData;
import com.findjob.job_agent.config.PromptConstants;
import com.findjob.job_agent.model.entity.Conversation;
import com.findjob.job_agent.model.entity.Message;

@Service
public class SummaryServiceAI {
    private final ChatCompletionsClient client;

    public SummaryServiceAI(ChatCompletionsClient client) {
        this.client = client;
    }

    public String summarizeConversation(Conversation conversation) {
        String systemMessage = PromptConstants.SUMMARY_PROMPT;

        List<Message> messages = conversation.getMessages();
        String previousSummary = conversation.getSummary() != null ? conversation.getSummary() : "";

        int totalMessages = messages.size();
        int fromIndex = Math.max(0, totalMessages - 5);
        List<Message> recentMessages = messages.subList(fromIndex, totalMessages);

        StringBuilder messagesText = new StringBuilder();
        for (Message msg : recentMessages) {
            messagesText.append(msg.getSender().name())
                    .append(": ")
                    .append(msg.getContent())
                    .append("\n");
        }

        String userPrompt = String.format(
                "Previous summary (if any):\n%s\n\nRecent conversation messages:\n%s",
                previousSummary,
                messagesText.toString());

        BinaryData data = BinaryData.fromObject(userPrompt);
        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage(systemMessage));
        chatMessages.add(new ChatRequestUserMessage(data));

        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
        chatCompletionsOptions.setModel("gpt-4o");
        chatCompletionsOptions.setTemperature(0.7);
        chatCompletionsOptions.setTopP(0.9);
        chatCompletionsOptions.setFrequencyPenalty(0.5);

        ChatCompletions completions = client.complete(chatCompletionsOptions);
        return completions.getChoices().getFirst().getMessage().getContent().trim();
    }
}
