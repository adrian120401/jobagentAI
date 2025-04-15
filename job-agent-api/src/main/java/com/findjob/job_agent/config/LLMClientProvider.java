package com.findjob.job_agent.config;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMClientProvider {

    @Value("${github.token}")
    private String key;

    @Bean
    public ChatCompletionsClient openAIClient(){
        String endpoint = "https://models.inference.ai.azure.com";

       return new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
    }
}
