package com.findjob.job_agent.model.entity;

import java.time.LocalDateTime;

import com.findjob.job_agent.model.enums.Sender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String id;
    private Sender sender;
    private String content;
    private String createdAt;

    public Message(String content, Sender sender) {
        this.content = content;
        this.sender = sender;
        this.createdAt = LocalDateTime.now().toString();
    }
}