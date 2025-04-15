package com.findjob.job_agent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.findjob.job_agent.model.entity.JobSearched;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchJobsAPIService {
    private final String baseUrl = "https://www.getonbrd.com/api/v0";

    public List<JobSearched> getGetonBoardJobs() throws JsonProcessingException {
        String apiUrl = baseUrl + "/search/jobs?query=programming&per_page=10&page=1&expand=[\"company\",\"tags\"]&remote=true";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForEntity(apiUrl, String.class).getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> root = objectMapper.readValue(response, new TypeReference<>() {
        });

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) root.get("data");

        List<JobSearched> jobs = new ArrayList<>();
        for (Map<String, Object> job : dataList) {
            JobSearched newJob = new JobSearched();
            String id = (String) job.get("id");
            Map<String, Object> attributes = (Map<String, Object>) job.get("attributes");
            String title = (String) attributes.get("title");
            String description = (String) attributes.get("description");
            String desirable = (String) attributes.get("desirable");
            String functions = (String) attributes.get("functions");
            String benefits = (String) attributes.get("benefits");

            Map<String, Object> companyRel = (Map<String, Object>) attributes.get("company");

            Map<String, Object> companyData = (Map<String, Object>) companyRel.get("data");
            Map<String, Object> companyAttributes = (Map<String, Object>) companyData.get("attributes");
            String companyName = (String) companyAttributes.get("name");
            String logo = (String) companyAttributes.get("logo");

            Map<String, Object> tagRel = (Map<String, Object>) attributes.get("tags");

            List<Map<String, Object>> tagData = (List<Map<String, Object>>) tagRel.get("data");

            List<JobSearched.Tag> tags = new ArrayList<>();

            tagData.forEach(tag -> {
                Map<String, Object> attributesTag = (Map<String, Object>) tag.get("attributes");
                String tagName = (String) attributesTag.get("name");
                String tagKeywords = (String) attributesTag.get("keywords");
                tags.add(new JobSearched.Tag(tagName,tagKeywords));
            });

            Map<String, Object> links = (Map<String, Object>) job.get("links");
            String link = (String) links.get("public_url");

            newJob.setIdJob(id);
            newJob.setTitle(title);
            newJob.setDescription(description);
            newJob.setCompanyName(companyName);
            newJob.setCompanyLogo(logo);
            newJob.setJobUrl(link);
            newJob.setDesirable(desirable);
            newJob.setFunctions(functions);
            newJob.setBenefits(benefits);
            newJob.setTags(tags);

            jobs.add(newJob);
        }

        return jobs;
    }

}
