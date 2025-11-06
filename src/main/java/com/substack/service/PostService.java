// src/main/java/com/substack/service/PostService.java
package com.substack.service;

import com.substack.model.MediaFile;
import com.substack.model.Post;
import com.substack.model.Tag;
import com.substack.model.User;
import com.substack.repository.MediaFileRepository;
import com.substack.repository.PostRepository;
import com.substack.repository.TagRepository;
import com.substack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    private final PostRepository postRepo;
    private final TagRepository tagRepo;

    public Post savePost(Post post) {

        List<MediaFile> mediaFiles = new ArrayList<>();
        post.getFiles().forEach(mediaFile -> {
            MediaFile manage = mediaFileRepository.findById(mediaFile.getId()).get();
            manage.setPost(post);
            mediaFiles.add(manage);
        });
        post.setFiles(mediaFiles);
        return postRepo.save(post);
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