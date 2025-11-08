package com.substack.service;

import com.substack.model.*;
import com.substack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


    @Service
    @RequiredArgsConstructor
    public class SubscriptionService {

        private final SubscriptionRepository subscriptionRepository;
        private final UserRepository userRepository;

        public void subscribe(String subscriberEmail, Long authorId, SubscriptionType type) {

            User subscriber = userRepository.findByEmail(subscriberEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("Author not found"));

            Subscription subscription = subscriptionRepository
                    .findBySubscriberIdAndAuthorId(subscriber.getId(), authorId)
                    .orElse(null);

            if (subscription == null) {
                subscription = Subscription.builder()
                        .subscriber(subscriber)
                        .author(author)
                        .type(type)
                        .active(true)
                        .startDate(LocalDateTime.now())
                        .build();
            } else {
                subscription.setType(type);
                subscription.setActive(true);
            }

            subscriptionRepository.save(subscription);
        }

        public List<Subscription> getSubscriptions(String email, String filter) {

            User subscriber = userRepository.findByEmail(email)
                    .orElseThrow();

            Long userId = subscriber.getId();

            return switch (filter) {
                case "paid" -> subscriptionRepository.findPaidSubscriptions(userId);
                case "free" -> subscriptionRepository.findFreeSubscriptions(userId);
                default -> subscriptionRepository.findBySubscriberIdAndActive(userId, true);
            };
        }


        public boolean isSubscribed(Long currentUserId, Long authorId) {
            return subscriptionRepository.existsBySubscriberIdAndAuthorIdAndActive(currentUserId, authorId, true);
        }

        public long countSubscribers(Long authorId) {
            return subscriptionRepository.countByAuthorIdAndActive(authorId, true);
        }

        public long countFollowing(Long userId) {
            return subscriptionRepository.countBySubscriberIdAndActive(userId, true);
        }


    }

