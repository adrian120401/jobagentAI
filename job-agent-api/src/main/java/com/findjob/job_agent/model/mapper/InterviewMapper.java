package com.findjob.job_agent.model.mapper;

import com.findjob.job_agent.model.dto.InterviewResponseDTO;
import com.findjob.job_agent.model.dto.JobInformation;
import com.findjob.job_agent.model.entity.Interview;
import com.findjob.job_agent.model.entity.JobSearched;
import lombok.AllArgsConstructor;
import lombok.Data;


public class InterviewMapper {
    public static InterviewResponseDTO toInterviewResponseDTO(Interview interview, JobSearched jobSearched) {
        JobInformation job = JobMapper.toJobInformation(jobSearched);
        return new InterviewResponseDTO(interview.getId(), interview.getScore(), interview.getFeedback(), job);
    }
}
