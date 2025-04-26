package com.findjob.job_agent.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterviewResponseDTO {
    private String id;
    private double score;
    private String feedback;
    private JobInformation job;
}
