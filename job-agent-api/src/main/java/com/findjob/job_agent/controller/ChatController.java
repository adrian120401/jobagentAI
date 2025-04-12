package com.findjob.job_agent.controller;

import com.findjob.job_agent.service.CVAnalyzeAI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chats")
public class ChatController {
    private final CVAnalyzeAI ai;

    public ChatController(CVAnalyzeAI ai) {
        this.ai = ai;
    }

/*    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test(){
        String message = ai.test();
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }*/
}
