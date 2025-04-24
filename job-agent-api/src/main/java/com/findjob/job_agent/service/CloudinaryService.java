package com.findjob.job_agent.service;

import com.cloudinary.Cloudinary;
import com.findjob.job_agent.config.CloudinaryConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    private final CloudinaryConfig config;

    public CloudinaryService(CloudinaryConfig config) {
        this.config = config;
    }

    public Map<String, String> uploadFile(byte[] file, String originalFilename) {
        Cloudinary cloudinary = config.getCloudinary();

        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", "jobagent");
            options.put("use_filename", true);
            options.put("unique_filename", false);
            options.put("public_id", originalFilename);
            options.put("resource_type", "auto");
            options.put("overwrite", true);

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file, options);

            Map<String, String> result = new HashMap<>();
            result.put("url", (String) uploadResult.get("secure_url"));
            result.put("public_id", (String) uploadResult.get("public_id"));
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }


    public byte[] downloadFile(String fileUrl) {
        try {
            URL url = URI.create(fileUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (InputStream in = connection.getInputStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from Cloudinary", e);
        }
    }
}
