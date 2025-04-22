package com.findjob.job_agent.model.dto;
import com.findjob.job_agent.model.entity.Interview;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSession {
    private String question;
    private String answer;
    private String previousFeedback;
    private int step;
    private Interview interview;

    public InterviewSession(String question,int step, String answer){
        this.answer = answer;
        this.question = question;
        this.step = step;
    }

    public InterviewSession(String question, String previousFeedback, int step) {
        this.question = question;
        this.previousFeedback = previousFeedback;
        this.step = step;
    }

    public InterviewSession(String question,int step, String answer, Interview interview){
        this.answer = answer;
        this.question = question;
        this.step = step;
        this.interview = interview;
    }

    public InterviewSession(String question, String previousFeedback, int step, Interview interview) {
        this.question = question;
        this.previousFeedback = previousFeedback;
        this.step = step;
        this.interview = interview;
    }

    @Override
    public String toString() {
        return "InterviewSession{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", previousFeedback='" + previousFeedback + '\'' +
                ", step=" + step +
                '}';
    }
}
