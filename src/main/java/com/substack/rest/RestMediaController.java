package com.substack.rest;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.substack.model.MediaFile;
import com.substack.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class RestMediaController {

    private final MediaFileRepository mediaFileRepository;

    @Value("${cloudinary.cloud_name}")
    private String cloudName;
    @Value("${cloudinary.api_key}")
    private String apiKey;
    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    private Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Cloudinary cloud = cloudinary();
        Map<String, Object> uploadResult = cloud.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        MediaFile media = MediaFile.builder()
                .fileName(file.getOriginalFilename())
                .fileUrl((String) uploadResult.get("secure_url"))
                .fileType(file.getContentType())
                .build();

        mediaFileRepository.save(media);

        return Map.of("url", media.getFileUrl(), "id", media.getId());
    }
}