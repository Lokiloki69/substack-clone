package com.substack.service;

import com.substack.model.Comment;
import com.substack.model.Post;
import com.substack.model.User;
import com.substack.repository.CommentRepository;
import com.substack.repository.PostRepository;
import com.substack.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    public Comment addComment(Comment givenComment) {
            Post post = postRepository.findById(givenComment.getPost().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Post not found with ID: " + givenComment.getPost().getId()));

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

    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    public long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
