package com.findjob.job_agent.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class ResumeProfile {
    public String name;
    public String email;
    public String phone;
    public String location;
    public String linkedin;
    public String portfolio;
    public String title;
    public String summary;
    public Integer totalExperience;
    public List<String> skills;
    public List<String> languages;
    public List<Experience> experience;
    public List<Education> education;

    public static class Experience {
        public String position;
        public String company;
        public String start_date;
        public String end_date;
        public String description;
    }

    public static class Education {
        public String degree;
        public String institution;
        public String start_date;
        public String end_date;
    }

    public static ResumeProfile parseResumeResponse(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, ResumeProfile.class);
    }
}
