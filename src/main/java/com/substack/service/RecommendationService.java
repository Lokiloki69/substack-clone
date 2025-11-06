package com.substack.service;

import com.substack.model.*;
import com.substack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final PostRepository postRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<Post> getRecommendedPosts(Long userId) {
        // Get posts from subscribed authors
        List<Subscription> subscriptions = subscriptionRepository
                .findBySubscriberIdAndActive(userId, true);

        return subscriptions.stream()
                .flatMap(sub -> sub.getAuthor().getPosts().stream())
                .filter(Post::getIsPublished)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Post> getTrendingPosts() {
        // Get most liked/commented posts from last 7 days
        return postRepository.findByIsPublishedTrue()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Publication> getRecommendedPublications() {
        // Get trending publications
        return new ArrayList<>(); // Implement later
    }
}
