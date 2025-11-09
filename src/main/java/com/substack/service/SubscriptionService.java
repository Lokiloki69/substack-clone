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

        User subscriber = userRepository.findByEmailIgnoreCase(subscriberEmail)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Subscription sub = subscriptionRepository
                .findBySubscriberIdAndAuthorId(subscriber.getId(), authorId)
                .orElse(null);

        if (sub == null) {
            sub = Subscription.builder()
                    .subscriber(subscriber)
                    .author(author)
                    .type(type)
                    .active(true)
                    .startDate(LocalDateTime.now())
                    .build();
        } else {
            sub.setType(type);
            sub.setActive(true);
        }

        subscriptionRepository.save(sub);
    }

    public void unsubscribe(Long subscriberId, Long authorId) {
        Subscription sub = subscriptionRepository
                .findBySubscriberIdAndAuthorId(subscriberId, authorId)
                .orElse(null);

        if (sub != null) {
            sub.setActive(false);
            subscriptionRepository.save(sub);
        }
    }

    public boolean isSubscribed(Long subscriberId, Long authorId) {
        return subscriptionRepository
                .existsBySubscriberIdAndAuthorIdAndActive(subscriberId, authorId, true);
    }

    public List<Subscription> getSubscriptions(String email, String filter) {
        User subscriber = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow();

        Long id = subscriber.getId();

        return switch (filter) {
            case "paid" -> subscriptionRepository.findPaidSubscriptions(id);
            case "free" -> subscriptionRepository.findFreeSubscriptions(id);
            default -> subscriptionRepository.findBySubscriberIdAndActive(id, true);
        };
    }

    public long countSubscribers(Long authorId) {
        return subscriptionRepository.countByAuthorIdAndActive(authorId, true);
    }

    public long countFollowing(Long subscriberId) {
        return subscriptionRepository.countBySubscriberIdAndActive(subscriberId, true);
    }
}
