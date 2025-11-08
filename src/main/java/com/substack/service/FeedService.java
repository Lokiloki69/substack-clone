// src/main/java/com/substack/service/FeedService.java
package com.substack.service;

import com.substack.model.*;
import com.substack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final InterestTagMappingRepository mappingRepository;

    public List<Post> getFollowingPosts(User user) {
//        List<Long> followingUserId = user.getSubscriptions().stream().map(m->m.getId()).toList();

        return postRepository.findFollowingPost(user.getSubscriptions());
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