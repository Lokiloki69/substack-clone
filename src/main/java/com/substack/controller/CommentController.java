package com.substack.controller;

import com.substack.dto.CommentDto;
import com.substack.model.Comment;
import com.substack.model.Post;
import com.substack.model.User;
import com.substack.service.CommentService;
import com.substack.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final AuthService authService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/posts/{postId}/comments")
    public String createComment(
            @PathVariable Long postId,
            @RequestParam(required = false) Long parentCommentId,
            @Valid @ModelAttribute CommentDto commentDto) {

        commentDto.setPostId(postId);
        commentDto.setParentCommentId(parentCommentId);
        Comment savedComment = commentService.save(commentDto);
        return "redirect:/posts/view/" + postId;
    }

    @PreAuthorize("@commentService.isOwner(#id)")
    @DeleteMapping("/deleteComment/{id}")
    public String deleteComment(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        Long postId = comment.getPost().getId();
        commentService.deleteComment(id);
        return "redirect:/posts/" + postId;
    }

    @PreAuthorize("@commentService.isOwner(#id)")
    @PostMapping("/editComment/{id}")
    public String updateComment(@PathVariable Long id,@ModelAttribute CommentDto commentDto) {
        Comment updatedComment = commentService.updateComment(id, commentDto);
        return "redirect:/posts/" + updatedComment.getPost().getId();
    }

    // AJAX endpoint for getting comments
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<?> getCommentsByPost(@PathVariable Long postId) {
        try {
            var comments = commentService.getCommentsByPost(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("Error fetching comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch comments");
        }
    }
//    @GetMapping("/notifications")
//    public String notifications(Model model) {
//        User currentUser = authService.getCurrentUser();
//        List<Notification> notifications = notificationService.getNotifications(currentUser);
//        model.addAttribute("notifications", notifications);
//        return "notifications";
//    }

}
