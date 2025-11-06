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

    public Subscription subscribe(User subscriber, User author, SubscriptionType type, Double amount) {
        // Check if subscription already exists
        Optional<Subscription> existing = subscriptionRepository
                .findBySubscriberIdAndAuthorId(subscriber.getId(), author.getId());

        if (existing.isPresent()) {
            return existing.get();
        }

        Subscription subscription = Subscription.builder()
                .subscriber(subscriber)
                .author(author)
                .type(type)
                .amount(amount)
                .currency("USD")
                .startDate(LocalDateTime.now())
                .endDate(type == SubscriptionType.PAID ? LocalDateTime.now().plusMonths(1) : null)
                .autoRenew(type == SubscriptionType.PAID)
                .active(true)
                .build();

        return subscriptionRepository.save(subscription);
    }

    public void unsubscribe(Long subscriberId, Long authorId) {
        Optional<Subscription> subscription = subscriptionRepository
                .findBySubscriberIdAndAuthorId(subscriberId, authorId);

        if (subscription.isPresent()) {
            Subscription sub = subscription.get();
            sub.setActive(false);
            subscriptionRepository.save(sub);
        }
    }

    public List<Subscription> getSubscribers(User author) {
        return subscriptionRepository.findByAuthorIdAndActive(author.getId(), true);
    }

    public List<Subscription> getSubscriptions(User subscriber) {
        return subscriptionRepository.findBySubscriberIdAndActive(subscriber.getId(), true);
    }

    public List<Subscription> getPaidSubscriptions(User author) {
        return subscriptionRepository.findByAuthorIdAndTypeAndActive(
                author.getId(), SubscriptionType.PAID, true
        );
    }

    public boolean isSubscribed(Long subscriberId, Long authorId) {
        return subscriptionRepository.findBySubscriberIdAndAuthorId(subscriberId, authorId)
                .map(Subscription::isActive)
                .orElse(false);
    }
}
