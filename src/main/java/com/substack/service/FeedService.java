package com.substack.service;

import com.substack.model.Interest;
import com.substack.model.Post;
import com.substack.model.Subscription;
import com.substack.model.User;
import com.substack.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;

    public List<Post> getPosts(String feedType, Optional<User> userOpt) {
        if (userOpt.isEmpty()) {
            return postRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
        }

        User user = userOpt.get();

        return switch (feedType) {

            case "following" -> getFollowingPosts(user);

            case "foryou" -> getPersonalizedPosts(user);

            default -> postRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
        };
    }

    public List<Post> getFollowingPosts(User user) {
        List<Long> followedAuthors = user.getSubscriptions().stream()
                .filter(Subscription::isActive)
                .map(sub -> sub.getAuthor().getId())
                .toList();

        if (followedAuthors.isEmpty()) {
            return List.of();
        }

        return postRepository.findByAuthorIdsAndIsPublishedTrueOrderByCreatedAtDesc(followedAuthors);
    }

    public List<Post> getPersonalizedPosts(User user) {
        Set<Long> interestIds = user.getInterests().stream()
                .map(Interest::getId)
                .collect(Collectors.toSet());

        if (interestIds.isEmpty()) {
            return postRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
        }

        return postRepository.findPersonalizedPosts(interestIds);
    }
}
