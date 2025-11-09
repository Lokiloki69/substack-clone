package com.substack.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subscriptions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"subscriber_id", "author_id"})})
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Who subscribed
    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    // 🔹 Whom they subscribed to
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    // 🔹 Type of subscription
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionType type; // FREE or PAID

    // 🔹 Payment details (only for paid subs)
    private Double amount;            // e.g. 5.99
    private String currency;          // e.g. "USD", "INR"
    private String paymentProvider;   // e.g. "STRIPE", "RAZORPAY"
    private String transactionId;     // Payment gateway transaction reference

    // 🔹 Subscription period
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 🔹 Auto-renew (optional)
    private boolean autoRenew;

    // 🔹 Metadata
    @CreationTimestamp
    private Instant createdAt;

    private boolean active = true;

    // --- Helper methods ---
    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDateTime.now());
    }

    public boolean isPaid() {
        return this.type == SubscriptionType.PAID;
    }
}
