package com.substack.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "interest_tag_mapping")
@IdClass(InterestTagMappingId.class)
@Getter @Setter
public class InterestTagMapping {

    @Id
    @ManyToOne
    @JoinColumn(name = "interest_id")
    private Interest interest;

    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Column(name = "relevance_score", nullable = false)
    private Double relevanceScore = 0.0;
}

