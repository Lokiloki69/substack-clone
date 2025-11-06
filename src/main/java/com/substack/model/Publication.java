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
@Table(name = "publications")
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "The Daily Newsletter"

    @Column(unique = true, nullable = false)
    private String slug; // e.g., "daily-newsletter" for URL

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image")
    private String coverImageUrl; // Cloudinary URL

    @Column(name = "logo")
    private String logoUrl; // Cloudinary URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "publication_members",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members; // Co-authors/collaborators

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @Column(name = "is_paid")
    private boolean isPaid = false;

    @Column(name = "base_price")
    private Double basePrice; // USD per month for paid publications

    @Column(columnDefinition = "TEXT")
    private String customCSS; // for styling

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    private boolean active = true;
}
