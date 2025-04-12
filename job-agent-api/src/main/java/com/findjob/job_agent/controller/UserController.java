package com.findjob.job_agent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.findjob.job_agent.model.dto.UserRequestDTO;
import com.findjob.job_agent.model.dto.UserResponseDTO;
import com.findjob.job_agent.service.UserService;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/me")
    public UserResponseDTO getById(){
        return userService.getUserById();
    }

    @PostMapping("/cv")
    public ResponseEntity<Map<String, String>> uploadCv(@RequestParam("file") MultipartFile file){
        userService.uploadCv(file);

        Map<String, String> response = new HashMap<>();
        response.put("message", "CV uploaded successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cv")
    public ResponseEntity<Map<String, String>> getCvText(){
        userService.readCV();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Analyze CV successfully");
        return ResponseEntity.ok(response);
    }
}
