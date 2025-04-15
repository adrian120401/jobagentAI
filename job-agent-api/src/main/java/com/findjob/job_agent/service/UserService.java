package com.findjob.job_agent.service;

import com.findjob.job_agent.exception.NotFoundException;
import com.findjob.job_agent.model.ResumeProfile;
import com.findjob.job_agent.model.dto.UserRequestDTO;
import com.findjob.job_agent.model.dto.UserResponseDTO;
import com.findjob.job_agent.model.entity.User;
import com.findjob.job_agent.model.mapper.UserMapper;
import com.findjob.job_agent.repository.UserRepository;
import com.findjob.job_agent.service.AI.CVAnalyzeService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository repository;
    private final CloudinaryService cloudinaryService;
    private final CVAnalyzeService cvAnalyzeService;

    public UserService(UserRepository repository, CloudinaryService cloudinaryService, CVAnalyzeService cvAnalyzeService) {
        this.repository = repository;
        this.cloudinaryService = cloudinaryService;
        this.cvAnalyzeService = cvAnalyzeService;
    }

    public UserResponseDTO register(UserRequestDTO userRequestDTO){
        User user = UserMapper.toEntity(userRequestDTO);
        User savedUser = repository.save(user);
        return UserMapper.fromEntity(savedUser);
    }

    public UserResponseDTO getUserById() {
        User user = repository.findById("67f6109fa7c79601e396bc0c").orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.fromEntity(user);
    }

    public void uploadCv(MultipartFile cv){
        User user = repository.findById("67f6109fa7c79601e396bc0c").orElseThrow(() -> new NotFoundException("User not found"));
        Map<String, String> result = cloudinaryService.uploadFile(cv);
        user.setCv_path(result.get("url"));
        repository.save(user);
    }

    public void readCV() {
        User user = repository.findById("67f6109fa7c79601e396bc0c").orElseThrow(() -> new NotFoundException("User not found"));
        try {
            URL url = URI.create(user.getCv_path()).toURL();
            
            try (InputStream inputStream = url.openStream();
                 PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
                
                PDFTextStripper textStripper = new PDFTextStripper();

                String rawText = textStripper.getText(document);
                document.close();

                String sanitizedText = sanitizeText(rawText);

                String response = cvAnalyzeService.analyzeCV(sanitizedText);

                if(response == null){
                    throw new Exception("Error analyzing cv");
                }

                System.out.println(response);
                ResumeProfile resumeProfile = ResumeProfile.parseResumeResponse(response);

                user.setResumeProfile(resumeProfile);
                repository.save(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing PDF");
        }
    }
    public String sanitizeText(String rawText) {
        return rawText
                .replaceAll("\\r?\\n", "\\\\n")
                .replaceAll("\\s{2,}", " ")
                .replaceAll("•", "-")
                .replaceAll("–", "-")
                .trim();
    }
}
