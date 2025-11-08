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
        select s from Subscription s
        where s.subscriber.id = :subscriberId
          and s.type = 'PAID'
          and s.active = true
    """)
    List<Subscription> findPaidSubscriptions(Long subscriberId);

    @Query("""
        select s from Subscription s
        where s.subscriber.id = :subscriberId
          and s.type = 'FREE'
          and s.active = true
    """)
    List<Subscription> findFreeSubscriptions(Long subscriberId);

    List<Subscription> findByAuthorIdAndActive(Long id, boolean b);

    List<User> findSubscribersByAuthorId(Long id);

    boolean existsBySubscriberIdAndAuthorIdAndActive(Long currentUserId, Long authorId, boolean b);
    List<Subscription> findBySubscriberAndActiveTrue(User subscriber);

    long countByAuthorIdAndActive(Long authorId, boolean active);
    long countBySubscriberIdAndActive(Long subscriberId, boolean active);

}

