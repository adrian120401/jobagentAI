package com.findjob.job_agent.service;

import com.findjob.job_agent.exception.NotFoundException;
import com.findjob.job_agent.exception.UnauthorizedException;
import com.findjob.job_agent.model.dto.LoginRequestDTO;
import com.findjob.job_agent.model.dto.LoginResponseDTO;
import com.findjob.job_agent.model.dto.ResumeProfile;
import com.findjob.job_agent.model.dto.UserRequestDTO;
import com.findjob.job_agent.model.dto.UserResponseDTO;
import com.findjob.job_agent.model.entity.User;
import com.findjob.job_agent.model.mapper.UserMapper;
import com.findjob.job_agent.repository.UserRepository;
import com.findjob.job_agent.security.JwtService;
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
    private final FileService fileService;
    private final CloudinaryService cloudinaryService;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            FileService fileService,
            CloudinaryService cloudinaryService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.fileService = fileService;
        this.cloudinaryService = cloudinaryService;
    }

    public UserResponseDTO register(UserRequestDTO userRequestDTO) {
        User user = UserMapper.toEntity(userRequestDTO);
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = repository.save(user);
        return UserMapper.fromEntity(savedUser);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = repository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

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

    public User getFirstUser() {
        return repository.findAll().getFirst();
    }

    public void uploadCv(MultipartFile cv) throws IOException {
        User user = getAuthUser();
        String cvPath = uploadFile(cv.getBytes(), cv.getOriginalFilename());
        ResumeProfile resumeProfile = fileService.readCV(cv.getBytes());
        user.setCv_path(cvPath);
        user.setResumeProfile(resumeProfile);
        repository.save(user);
    }

    public void uploadDocx(MultipartFile docx) throws IOException {
        User user = getAuthUser();
        String docxPath = uploadFile(docx.getBytes(), docx.getOriginalFilename());
        user.setDocx_path(docxPath);
        repository.save(user);
    }


    public String uploadFile(byte[] file, String originalFilename) throws IOException {
        Map<String, String> result = cloudinaryService.uploadFile(file, originalFilename);
        return result.get("url");
    }

    public byte[] getDocx() throws IOException {
        User user = getAuthUser();
        String docxPath = user.getDocx_path();
        if (docxPath == null) {
            throw new NotFoundException("Docx not found");
        }
        return cloudinaryService.downloadFile(docxPath);
    }
}
