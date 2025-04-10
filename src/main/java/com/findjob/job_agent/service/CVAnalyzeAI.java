package com.findjob.job_agent.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CVAnalyzeAI {
    @Value("${github.token}")
    private String key;

    public String analyzeCV(String cvText) {
        try {
            String endpoint = "https://models.inference.ai.azure.com";
            String model = "gpt-4o";

            ChatCompletionsClient client = new ChatCompletionsClientBuilder()
                    .credential(new AzureKeyCredential(key))
                    .endpoint(endpoint)
                    .buildClient();

            String systemMessage = "You are an AI agent that analyzes plain-text resumes and extracts relevant information. Always respond in a compact, valid, minified JSON string in one line. Ensure all special characters within JSON string values (like newlines, quotes, backslashes) are properly escaped (e.g., \\n, \\\", \\\\). If a field is missing or cannot be determined, use an empty string or default value (e.g., [] for lists).";

            String userPrompt = String.format("""
                    Below is the content of a resume in plain text. Extract and organize the information using the following JSON format:

                    {
                      "name": "",
                      "email": "",
                      "phone": "",
                      "location": "",
                      "linkedin": "",
                      "portfolio": "",
                      "title": "",
                      "summary": "",
                      "skills": [],
                      "languages": [],
                      "experience": [
                        {
                          "position": "",
                          "company": "",
                          "start_date": "",
                          "end_date": "",
                          "description": ""
                        }
                      ],
                      "education": [
                        {
                          "degree": "",
                          "institution": "",
                          "start_date": "",
                          "end_date": ""
                        }
                      ]
                    }

                    Resume content:
                    \"\"\"
                    %s
                    \"\"\"
                    """, cvText);


            BinaryData data = BinaryData.fromObject(userPrompt);
            List<ChatRequestMessage> chatMessages = Arrays.asList(
                    new ChatRequestSystemMessage(systemMessage),
                    new ChatRequestUserMessage(data)
            );

            ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
            chatCompletionsOptions.setModel(model);

            ChatCompletions completions = client.complete(chatCompletionsOptions);

            String message =  completions.getChoices().getFirst().getMessage().getContent();
            return extractJson(message);
        } catch (Exception e) {
            System.out.println("Error details: " + e.getMessage());
        }
        return null;
    }

    public String extractJson(String response) {
        int startIndex = response.indexOf("{");
        int endIndex = response.lastIndexOf("}");

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return response.substring(startIndex, endIndex + 1);
        }

        return null;
    }
}

