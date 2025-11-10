package com.substack.service;
import com.substack.dto.CommentDto;
import com.substack.model.Comment;
import com.substack.model.Post;
import com.substack.model.User;
import com.substack.repository.CommentRepository;
import com.substack.repository.PostRepository;
import com.substack.repository.UserRepository;
import com.substack.service.auth.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    // We can Remove Only One comment , Let's do that later
    public Comment save(CommentDto givenComment) {
        Post post = postRepository.findById(givenComment.getPostId())
                .orElseThrow(() -> new RuntimeException(
                        "Post not found with ID: " + givenComment.getPostId()));

        User user = authService.getCurrentUser();

        Comment comment = new Comment();
        comment.setText(givenComment.getText());
        comment.setPost(post);
        comment.setUser(user);

        Optional<Comment> parentComment = Optional.empty();
        if (givenComment.getParentCommentId() != null) {
            parentComment = commentRepository.findById(givenComment.getParentCommentId());
        }

        if (parentComment.isPresent()) {
            comment.setParentComment(parentComment.get());
        } else {
            comment.setParentComment(null);
        }

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created on post: {}", post.getTitle());

        if (parentComment.isPresent()) {
            List<Comment> replies = parentComment.get().getReplies();
            if (replies == null) replies = new ArrayList<>();
            replies.add(savedComment);
            parentComment.get().setReplies(replies);
        }

        return savedComment;
    }

    public List<Comment> getCommentsByParentCommentId(Long id){
        return commentRepository.findByParentCommentId(id);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(
                        "Post not found with ID: " + postId));

        return new ArrayList<>(commentRepository.findByPostOrderByCreatedDateAsc(post));
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + username));

        return commentRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Long getCommentCountByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException(
                        "Post not found with ID: " + postId));

        return commentRepository.countByPost(post);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with ID: " + id);
        }
        commentRepository.deleteById(id);
        log.info("Comment deleted with ID: {}", id);
    }


    public Comment getCommentById(Long id){
        return commentRepository.findById(id).orElseThrow(()->
                new RuntimeException("Comment not found!"));
    }

    public Comment updateComment(Long id, CommentDto commentDto) {
        Comment comment = commentRepository.findById(id).
                orElseThrow(()-> new RuntimeException("Comment not found"));
        comment.setText(commentDto.getText());
        return commentRepository.save(comment);
    }

    public boolean isOwner(Long commentId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) return false;
        Comment comment = getCommentById(commentId);
        return comment.getUser().getId().equals(currentUser.getId());
    }
}
