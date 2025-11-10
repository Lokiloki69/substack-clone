package com.substack.repository;

import com.substack.model.Comment;
import com.substack.model.Post;
import com.substack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findByUser(User user);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post")
    Long countByPost(@Param("post") Post post);

    @Query("SELECT c FROM Comment c WHERE c.post = :post ORDER BY c.createdDate ASC")
    List<Comment> findByPostOrderByCreatedDateAsc(@Param("post") Post post);

    List<Comment> findByParentCommentId(Long parentCommentId);

    Optional<Comment> findById(Long id);
}
