package com.substack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private Users author;

    @ManyToMany
    @JoinTable(
            name = "post_author",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "co_author_id")
    )
    private List<Users> coAuthor;

    private String audience = "everyone";
    private String comments = "everyone";
    private LocalDateTime scheduledAt;
    private boolean sendEmail = true;

}