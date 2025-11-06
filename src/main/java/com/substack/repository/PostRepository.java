package com.substack.repository;

import com.substack.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorIdAndIsPublishedTrue(Long authorId);
    List<Post> findByPublicationIdAndIsPublishedTrue(Long publicationId);
    List<Post> findByIsPublishedTrue();
    List<Post> findByScheduledAtNotNull();
}
