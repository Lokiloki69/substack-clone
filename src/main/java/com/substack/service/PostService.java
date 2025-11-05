// src/main/java/com/substack/service/PostService.java
package com.substack.service;

import com.substack.model.Post;
import com.substack.model.Tag;
import com.substack.repository.*;
import com.substack.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public Post savePost(Post post, String tags) {
        Post saved = postRepository.save(post);

        if (tags != null && !tags.isBlank()) {
            String[] tagNames = tags.split(",");
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                Optional<Tag> existingTag = tagRepository.findByName(tagName);
                Tag tag;
                if (existingTag.isPresent()) {
                    tag = existingTag.get();
                } else {
                    tag = Tag.builder().name(tagName).build();
                    tagRepository.save(tag);
                }
                saved.getTags().add(tag);
            }
            postRepository.save(saved);
        }

        return saved;
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getPublishedPosts() {
        return postRepository.findByIsPublishedTrue();
    }

    public List<Post> getAuthorPosts(Long authorId) {
        return postRepository.findByAuthorIdAndIsPublishedTrue(authorId);
    }

    public List<Post> getPublicationPosts(Long publicationId) {
        return postRepository.findByPublicationIdAndIsPublishedTrue(publicationId);
    }

    public List<Post> getScheduledPosts() {
        return postRepository.findByScheduledAtNotNull()
                .stream()
                .filter(p -> p.getScheduledAt().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public void publishScheduledPosts() {
        List<Post> scheduledPosts = getScheduledPosts();
        scheduledPosts.forEach(post -> {
            post.setIsPublished(true);
            post.setScheduledAt(null);
            postRepository.save(post);
        });
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public List<Post> searchPosts(String query) {
        // Implement full-text search later
        return postRepository.findByIsPublishedTrue();
    }
}