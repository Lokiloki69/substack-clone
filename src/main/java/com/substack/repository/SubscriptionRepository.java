package com.substack.repository;

import com.substack.model.Subscription;
import com.substack.model.SubscriptionType;
import com.substack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("""
        select s.subscriber
        from Subscription s
        where s.author.id = :authorId
          and s.active = true
          and (s.endDate is null or s.endDate > CURRENT_TIMESTAMP)
    """)
    List<User> findSubscribersByAuthorId(Long authorId);

    Optional<Subscription> findBySubscriberIdAndAuthorId(Long subscriberId, Long authorId);
    List<Subscription> findByAuthorIdAndActive(Long authorId, boolean active);
    List<Subscription> findBySubscriberIdAndActive(Long subscriberId, boolean active);
    List<Subscription> findByAuthorIdAndTypeAndActive(Long authorId, SubscriptionType type, boolean active);
}
