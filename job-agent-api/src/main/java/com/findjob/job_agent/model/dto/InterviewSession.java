package com.findjob.job_agent.model.dto;
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
