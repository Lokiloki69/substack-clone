package com.substack.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String username;  // for public URL slug (e.g., substack.com/@nivedita)
    private String bio;       // short user bio

    // 🔹 Auth info
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // READER, WRITER, ADMIN

    // 🔹 Profile details
    private String profileImageUrl; // Cloudinary URL
    private String bannerImageUrl;  // optional cover photo

    // 🔹 Social or contact links (optional)
    private String twitterHandle;
    private String website;

    // 🔹 Activity timestamps
    @CreationTimestamp
    @Column(name = "cerated_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", updatable = false)
    private Instant updatedAt;

    // 🔹 Relationships
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<User> subscriptions;
}
