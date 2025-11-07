// src/main/java/com/substack/service/PostService.java
package com.substack.service;

import com.substack.model.MediaFile;
import com.substack.model.Post;
import com.substack.model.Tag;
import com.substack.model.User;
import com.substack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final MediaFileRepository mediaFileRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PostRepository postRepo;
    private final EmailService emailService;

    public Post savePost(Post post) {

        if(post.getFiles() != null) {
            List<MediaFile> mediaFiles = new ArrayList<>();
            post.getFiles().forEach(mediaFile -> {
                MediaFile manage = mediaFileRepository.findById(mediaFile.getId()).get();
                manage.setPost(post);
                mediaFiles.add(manage);
            });
            post.setFiles(mediaFiles);
        }

        Post saved = postRepository.save(post);
        // 1) Email creator immediately
        sendEmailToCreator(saved);

        // 2) If published now → mail subscribers
        if (saved.getIsPublished() && saved.getScheduledAt() == null) {
            sendEmailToSubscribers(saved);
        }
        return saved;
    }

    private void sendEmailToCreator(Post post) {
        User creator = post.getAuthor();
        if (creator == null) return;

        emailService.send(
                creator.getEmail(),
                "Your post has been created",
                "Your post '" + post.getTitle() + "' was created successfully."
        );
    }

    private void sendEmailToSubscribers(Post post) {
        User creator = post.getAuthor();
        if (creator == null) return;

        List<User> subscribers = subscriptionRepository.findSubscribersByAuthorId(creator.getId());

        subscribers.forEach(sub -> {
            emailService.send(
                    sub.getEmail(),
                    "New post from " + creator.getName(),
                    "A new post was published: " + post.getTitle() +
                            "\n\nClick here to read: https://yourapp.com/posts/" + post.getId()
            );
        });
    }

    public void publishScheduledPosts() {
        List<Post> scheduled = getScheduledPosts();

        scheduled.forEach(p -> {
            p.setIsPublished(true);
            p.setScheduledAt(null);
            postRepository.save(p);

            sendEmailToSubscribers(p);
        });
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

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public List<Post> searchPosts(String query) {
        // Implement full-text search later
        return postRepository.findByIsPublishedTrue();
    }
}