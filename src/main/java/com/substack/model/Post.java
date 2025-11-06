package com.substack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String subTitle;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isPublished;

    @ManyToMany
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @ManyToOne
    private User author;

    @ManyToMany
    @JoinTable(
            name = "post_author",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "co_author_id")
    )
    private List<User> coAuthor;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaFile> files;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    private String audience = "everyone";
    private String comments = "everyone";
    private LocalDateTime scheduledAt;
    private boolean sendEmail = true;

}