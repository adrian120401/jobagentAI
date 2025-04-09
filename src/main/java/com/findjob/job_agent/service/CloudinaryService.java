package com.findjob.job_agent.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.findjob.job_agent.config.CloudinaryConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    private final CloudinaryConfig config;

    public CloudinaryService(CloudinaryConfig config) {
        this.config = config;
    }

    public Map<String, String> uploadFile(MultipartFile file) {
        Cloudinary cloudinary = config.getCloudinary();
        try {
            String originalFilename = file.getOriginalFilename();

            Map<String, Object> options = new HashMap<>();
            options.put("folder", "jobagent");
            options.put("use_filename", true);
            options.put("unique_filename", false);
            options.put("public_id", getPublicIdFromFilename(originalFilename));

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            Map<String, String> result = new HashMap<>();
            result.put("url", (String) uploadResult.get("secure_url"));
            result.put("public_id", (String) uploadResult.get("public_id"));
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    private String getPublicIdFromFilename(String filename) {
        if (filename == null) return null;
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(0, lastDotIndex) : filename;
    }
}
