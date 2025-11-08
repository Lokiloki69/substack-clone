package com.substack.controller;

import com.substack.model.*;
import com.substack.repository.*;
import com.substack.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final PostRepository postRepository;

    @PostMapping("/{postId}")
    public String addComment(
            @PathVariable Long postId,
            @RequestParam(required = false) Long parentCommentId,
            @RequestParam String name) {

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return "redirect:/";
        }

        Comment newComment = Comment.builder()
                .text(name)
                .post(postRepository.findById(postId).get())
//                .parentComment(commentService.)
                .build();

        commentService.addComment(newComment);

        return "redirect:/posts/view/"+ postId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, RedirectAttributes ra) {
        commentService.deleteComment(commentId);
        ra.addFlashAttribute("success", "Comment deleted!");
        return "redirect:/";
    }
}
