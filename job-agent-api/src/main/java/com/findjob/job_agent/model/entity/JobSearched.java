package com.findjob.job_agent.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSearched {
    private String id;

    @Indexed(unique = true)
    private String idJob;
    private String title;
    private String description;
    private String functions;
    private String benefits;
    private String desirable;
    private List<Tag> tags;
    private String companyName;
    private String companyLogo;
    private String jobUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag {
        private String name;
        private String keywords;
    }
}
