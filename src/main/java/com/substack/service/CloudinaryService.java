package com.substack.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) {
        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", "auto"
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            log.info("Cloudinary upload successful. Resource type: {}, URL: {}",
                    uploadResult.get("resource_type"), uploadResult.get("secure_url"));

            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Cloudinary upload error", e);
            throw new RuntimeException("Failed to upload file. Check file type and size.", e);
        }
    }
}