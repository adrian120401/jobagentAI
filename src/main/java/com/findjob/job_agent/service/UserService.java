package com.findjob.job_agent.service;

import com.findjob.job_agent.exception.NotFoundException;
import com.findjob.job_agent.model.dto.UserRequestDTO;
import com.findjob.job_agent.model.dto.UserResponseDTO;
import com.findjob.job_agent.model.entity.User;
import com.findjob.job_agent.model.mapper.UserMapper;
import com.findjob.job_agent.repository.UserRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository repository;
    private final CloudinaryService cloudinaryService;

    public UserService(UserRepository repository, CloudinaryService cloudinaryService) {
        this.repository = repository;
        this.cloudinaryService = cloudinaryService;
    }

    public UserResponseDTO register(UserRequestDTO userRequestDTO){
        User user = UserMapper.toEntity(userRequestDTO);
        User savedUser = repository.save(user);
        return UserMapper.fromEntity(savedUser);
    }

    public void uploadCv(MultipartFile cv){
        User user = repository.findById("67f6109fa7c79601e396bc0c").orElseThrow(() -> new NotFoundException("User not found"));
        Map<String, String> result = cloudinaryService.uploadFile(cv);
        user.setCv_path(result.get("url"));
        repository.save(user);
    }

    public String readCV() {
        User user = repository.findById("67f6109fa7c79601e396bc0c").orElseThrow(() -> new NotFoundException("User not found"));
        try {
            URL url = URI.create(user.getCv_path()).toURL();
            
            try (InputStream inputStream = url.openStream();
                 PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
                
                PDFTextStripper textStripper = new PDFTextStripper();

                System.out.println();
                return textStripper.getText(document);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing PDF");
        }
    }
}
