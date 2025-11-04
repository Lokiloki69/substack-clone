// src/main/java/com/substack/service/PostService.java
package com.substack.service;

import com.substack.model.Post;
import com.substack.model.Tag;
import com.substack.repository.PostRepository;
import com.substack.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepo;
    private final TagRepository tagRepo;

    public Post savePost(Post post, String tagsCsv) {
        // Parse tags
        if (tagsCsv != null && !tagsCsv.isBlank()) {
            List<Tag> tags = Arrays.stream(tagsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(name -> tagRepo.findByName(name).orElseGet(() -> tagRepo.save(Tag.builder().name(name).build())))
                    .collect(Collectors.toList());
            post.setTags(tags);
        }

        return postRepo.save(post);
    }

    public Post findById(Long id) {
        return postRepo.findById(id).orElse(null);
    }
}