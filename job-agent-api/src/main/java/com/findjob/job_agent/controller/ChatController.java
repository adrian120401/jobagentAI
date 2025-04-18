package com.findjob.job_agent.controller;

import com.findjob.job_agent.model.dto.ChatRequest;
import com.findjob.job_agent.model.dto.ChatResponse;
import com.findjob.job_agent.model.dto.InterviewRequest;
import com.findjob.job_agent.model.dto.InterviewSession;
import com.findjob.job_agent.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/chats")
public class ChatController {
    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    @PostMapping("")
    public ResponseEntity<ChatResponse> test(@RequestBody ChatRequest request) {
        ChatResponse response = service.process(request.getMessage(), request.getJobId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/interview")
    public ResponseEntity<InterviewSession> interview(@RequestBody InterviewRequest request) {
        InterviewSession response = service.getInterview(request.getJobId(), request.getHistory());
        return ResponseEntity.ok(response);
    }
}
