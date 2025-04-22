package com.findjob.job_agent.service;

import com.findjob.job_agent.model.JobMatchResult;
import com.findjob.job_agent.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.List;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendJobMatchesEmail(User user, List<JobMatchResult> matches) {
        String html = buildEmailHtml(matches);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("New job opportunities for you");
            helper.setText(html, true);
            mailSender.send(message);
            System.out.println("Email send");
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage(), e);
        }
    }

    private String buildEmailHtml(List<JobMatchResult> matches) {
        StringBuilder cards = new StringBuilder();
        for (JobMatchResult match : matches) {
            String matchColor = match.getMatchScore() >= 0.8 ? "#22c55e" : "#f59e42";
            int matchPercentage = Math.round(match.getMatchScore() * 100);
            cards.append("<div style=\"border:1px solid #e0e0e0; border-radius:8px; padding:16px; margin-bottom:16px; font-family:sans-serif;\">")
                .append("<div style=\"display:flex; align-items:center;\">")
                .append("<img src=\"").append(match.getJob().getCompanyLogo()).append("\" alt=\"").append(match.getJob().getCompanyName()).append("\" style=\"width:60px; height:60px; object-fit:contain; border-radius:8px; background:#fff; margin-right:16px;\">")
                .append("<div>")
                .append("<div style=\"font-size:18px; font-weight:bold; color:#2d3748;\">").append(match.getJob().getTitle()).append("</div>")
                .append("<div style=\"font-size:14px; color:#718096;\">").append(match.getJob().getCompanyName()).append("</div>")
                .append("<div style=\"font-size:13px; color:#4a5568; margin-top:8px;\">").append(match.getReason()).append("</div>")
                .append("<div style=\"margin-top:8px;\"><span style=\"display:inline-block; background:").append(matchColor).append("; color:#fff; border-radius:4px; padding:2px 8px; font-size:12px;\">")
                .append(matchPercentage).append("% match</span></div>")
                .append("</div></div>")
                .append("<div style=\"margin-top:12px;\"><a href=\"").append(match.getJob().getJobUrl()).append("\" style=\"color:#3182ce; text-decoration:none; font-weight:bold;\">View Job</a></div>")
                .append("</div>");
        }
        return "<body style=\"background:#f7fafc; padding:24px;\">" +
                "<h2 style=\"font-family:sans-serif; color:#2d3748;\">New job opportunities for you!</h2>" +
                "<p style=\"font-family:sans-serif; color:#4a5568;\">These jobs match your profile:</p>" +
                cards +
                "<p style=\"font-family:sans-serif; color:#4a5568; margin-top:32px;\">Best of luck in your job search!</p>" +
                "</body>";
    }
} 