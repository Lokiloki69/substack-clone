package com.substack.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;         // STRIPE, RAZORPAY
    private String transactionId;    // Gateway transaction reference
    private Double amount;
    private String currency;
    private String status;           // SUCCESS, FAILED, PENDING

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
}
