package com.findjob.job_agent.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("conversations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    private String id;
    private String userId;
    private List<Message> messages;
    private String summary;

    public Conversation(String userId) {
        this.userId = userId;
        this.messages = new ArrayList<>();
        this.summary = "";
    }
}
