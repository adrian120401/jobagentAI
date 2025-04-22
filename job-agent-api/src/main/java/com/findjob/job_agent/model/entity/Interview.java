package com.findjob.job_agent.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interview {
    private String id;
    private String userId;
    private String jobId;
    private double score;
    private String feedback;

    public Interview(String userId, String jobId, double score, String feedback) {
        this.userId = userId;
        this.jobId = jobId;
        this.score = score;
        this.feedback = feedback;
    }

    public Interview(double score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }
}
