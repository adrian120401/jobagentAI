package com.findjob.job_agent.controller;

import com.findjob.job_agent.model.dto.LoginRequestDTO;
import com.findjob.job_agent.model.dto.LoginResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.findjob.job_agent.model.dto.UserRequestDTO;
import com.findjob.job_agent.model.dto.UserResponseDTO;
import com.findjob.job_agent.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.register(userRequestDTO);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
        return userService.login(loginRequestDTO);
    }

    @GetMapping("/me")
    public UserResponseDTO getMe(){
        return userService.getMe();
    }

    @PostMapping("/cv")
    public ResponseEntity<Map<String, String>> uploadCv(@RequestParam("file") MultipartFile file) throws IOException {
        userService.uploadCv(file);

        Map<String, String> response = new HashMap<>();
        response.put("message", "CV uploaded successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/docx")
    public ResponseEntity<Map<String, String>> uploadDocx(@RequestParam("file") MultipartFile file) throws IOException {
        userService.uploadDocx(file);

        Map<String, String> response = new HashMap<>();
        response.put("message", "DOCX uploaded successfully");
        return ResponseEntity.ok(response);
    }
}
