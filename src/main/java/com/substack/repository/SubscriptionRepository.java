package com.substack.repository;

import com.substack.model.Subscription;
import com.substack.model.SubscriptionType;
import com.substack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findBySubscriberIdAndAuthorId(Long subscriberId, Long authorId);

    List<Subscription> findBySubscriberIdAndActive(Long subscriberId, boolean active);

    @Query("""
        SELECT s FROM Subscription s
        WHERE s.subscriber.id = :subscriberId
          AND s.type = 'PAID'
          AND s.active = true
    """)
    List<Subscription> findPaidSubscriptions(Long subscriberId);

    @Query("""
        SELECT s FROM Subscription s
        WHERE s.subscriber.id = :subscriberId
          AND s.type = 'FREE'
          AND s.active = true
    """)
    List<Subscription> findFreeSubscriptions(Long subscriberId);

    List<Subscription> findByAuthorIdAndActive(Long authorId, boolean active);

    @Query("""
        SELECT s.subscriber
        FROM Subscription s
        WHERE s.author.id = :authorId
          AND s.active = true
    """)
    List<User> findSubscribersByAuthorId(Long authorId);

    boolean existsBySubscriberIdAndAuthorIdAndActive(Long subscriberId, Long authorId, boolean active);

    long countByAuthorIdAndActive(Long authorId, boolean active);

    long countBySubscriberIdAndActive(Long subscriberId, boolean active);
}
