package com.substack.repository;

import com.substack.model.Post;
import com.substack.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorIdAndIsPublishedTrue(Long authorId);
    List<Post> findByPublicationIdAndIsPublishedTrue(Long publicationId);
    List<Post> findByIsPublishedTrue();
    List<Post> findByScheduledAtNotNull();
    List<Post> findByIsPublishedTrueOrderByCreatedAtDesc();


    @Query("""
        SELECT DISTINCT p FROM Post p
        JOIN p.tags t
        JOIN InterestTagMapping m ON t = m.tag
        WHERE m.interest.id IN :interestIds
          AND p.isPublished = true
        ORDER BY m.relevanceScore DESC, p.createdAt DESC
        """)
    List<Post> findPersonalizedPosts(@Param("interestIds") Set<Long> interestIds);

    @Query("""
    SELECT p FROM Post p 
    WHERE p.author IN :followedAuthors 
      AND p.isPublished = true 
    ORDER BY p.createdAt DESC
    """)
    List<Post> findFollowingPost(@Param("followedAuthors") List<User> followedAuthors);

    @Query("""
    SELECT p FROM Post p
    WHERE p.author.id IN :authorIds
      AND p.isPublished = true
    ORDER BY p.createdAt DESC
""")
    List<Post> findByAuthorIdsAndIsPublishedTrueOrderByCreatedAtDesc(List<Long> followedAuthorIds);
}
