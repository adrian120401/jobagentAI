package com.findjob.job_agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchResult {
    private String jobId;
    private float matchScore;
    private String reason;
    private JobInformation job;

    public JobMatchResult(String jobId, float matchScore, String reason) {
        this.jobId = jobId;
        this.matchScore = matchScore;
        this.reason = reason;
    }
}
