package com.findjob.job_agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobInformation {
    private String jobId;
    private String title;
    private String description;
    private String benefits;
    private String jobUrl;
    private String companyName;
    private String companyLogo;
}
