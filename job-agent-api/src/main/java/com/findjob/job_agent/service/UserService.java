package com.findjob.job_agent.service;

import com.findjob.job_agent.exception.NotFoundException;
import com.findjob.job_agent.exception.UnauthorizedException;
import com.findjob.job_agent.model.ResumeProfile;
import com.findjob.job_agent.model.dto.LoginRequestDTO;
import com.findjob.job_agent.model.dto.LoginResponseDTO;
import com.findjob.job_agent.model.dto.UserRequestDTO;
import com.findjob.job_agent.model.dto.UserResponseDTO;
import com.findjob.job_agent.model.entity.User;
import com.findjob.job_agent.model.mapper.UserMapper;
import com.findjob.job_agent.repository.UserRepository;
import com.findjob.job_agent.security.JwtService;
import com.findjob.job_agent.service.AI.CVAnalyzeService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final CloudinaryService cloudinaryService;
    private final CVAnalyzeService cvAnalyzeService;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            CloudinaryService cloudinaryService,
            CVAnalyzeService cvAnalyzeService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.cloudinaryService = cloudinaryService;
        this.cvAnalyzeService = cvAnalyzeService;
    }

    public UserResponseDTO register(UserRequestDTO userRequestDTO) {
        User user = UserMapper.toEntity(userRequestDTO);
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = repository.save(user);
        return UserMapper.fromEntity(savedUser);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = repository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new LoginResponseDTO(token, UserMapper.fromEntity(user));
    }

    public User getAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return repository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserResponseDTO getMe() {
        return UserMapper.fromEntity(getAuthUser());
    }

    public void uploadCv(MultipartFile cv) throws IOException {
        User user = getAuthUser();
        Map<String, String> result = cloudinaryService.uploadFile(cv);
        ResumeProfile resumeProfile = readCV(cv.getBytes());
        user.setCv_path(result.get("url"));
        user.setResumeProfile(resumeProfile);
        repository.save(user);
    }

    public ResumeProfile readCV(byte[] file) {
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper textStripper = new PDFTextStripper();

            String rawText = textStripper.getText(document);
            document.close();

            String sanitizedText = sanitizeText(rawText);

            String response = cvAnalyzeService.analyzeCV(sanitizedText);

            if (response == null) {
                throw new Exception("Error analyzing cv");
            }

            return ResumeProfile.parseResumeResponse(response);

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
