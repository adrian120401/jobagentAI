package com.findjob.job_agent.model.entity;

import com.findjob.job_agent.model.ResumeProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
    private String cv_path;
    private ResumeProfile resumeProfile;

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }
}
