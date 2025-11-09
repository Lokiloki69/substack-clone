package com.substack.service;

import com.substack.model.Post;
import com.substack.model.Publication;
import com.substack.model.Subscription;
import com.substack.model.User;
import com.substack.repository.PostRepository;
import com.substack.repository.PublicationRepository;
import com.substack.repository.SubscriptionRepository;
import com.substack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExploreService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PublicationRepository publicationRepository;

    public List<Post> getTrendingPosts() {
        return postRepository.findByIsPublishedTrue()
                .stream()
                .sorted(Comparator.comparingInt((Post p) -> p.getLikes().size()).reversed())
                .limit(10)
                .toList();
    }

    public List<User> getTopAuthors() {
        Map<Long, Long> subscriberCount = subscriptionRepository.findAll()
                .stream()
                .filter(Subscription::isActive)
                .collect(Collectors.groupingBy(
                        s -> s.getAuthor().getId(),
                        Collectors.counting()
                ));

        return userRepository.findAll()
                .stream()
                .sorted((a, b) -> {
                    long aCount = subscriberCount.getOrDefault(a.getId(), 0L);
                    long bCount = subscriberCount.getOrDefault(b.getId(), 0L);
                    return Long.compare(bCount, aCount);
                })
                .limit(10)
                .toList();
    }

    public List<Publication> getTopPublications() {
        return publicationRepository.findByActive(true)
                .stream()
                .limit(10)
                .toList();
    }
}
