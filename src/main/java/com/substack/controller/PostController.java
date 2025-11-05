// src/main/java/com/substack/controller/PostController.java
package com.substack.controller;

import com.substack.model.*;
import com.substack.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final PublicationService publicationService;
    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, HttpSession session) {
        Post post = postService.findById(id);
        if (post == null) {
            return "redirect:/";
        }

        List<Comment> comments = commentService.getCommentsByPost(id);
        long likeCount = likeService.getLikeCount(id);

        Long userId = (Long) session.getAttribute("userId");
        boolean liked = false;
        if (userId != null) {
            liked = likeService.isLiked(userId, id);
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("liked", liked);
        return "post/view";
    }

    @GetMapping("/new")
    public String newPost(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        User user = userService.findById(userId);
        List<Publication> publications = publicationService.getByOwnerId(userId);

        Post post = Post.builder()
                .title("")
                .subTitle("")
                .content("")
                .isPublished(false)
                .audience("everyone")
                .comments("everyone")
                .sendEmail(true)
                .author(user)
                .build();

        model.addAttribute("post", post);
        model.addAttribute("publications", publications);
        return "post/create";
    }

    @PostMapping("/save")
    public String savePost(
            @ModelAttribute Post post,
            @RequestParam(required = false) Long publicationId,
            @RequestParam(required = false) String scheduledAt,
            @RequestParam(required = false) String audience,
            @RequestParam(required = false) String comments,
            @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "false") boolean isPublished,
            HttpSession session,
            RedirectAttributes ra) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        User user = userService.findById(userId);
        post.setAuthor(user);
        post.setAudience(audience != null ? audience : "everyone");
        post.setComments(comments != null ? comments : "everyone");
        post.setIsPublished(isPublished);

        if (publicationId != null) {
            var publication = publicationService.findById(publicationId);
            publication.ifPresent(post::setPublication);
        }

        if (scheduledAt != null && !scheduledAt.isBlank()) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                post.setScheduledAt(LocalDateTime.parse(scheduledAt, fmt));
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Invalid date format");
                return "redirect:/posts/new";
            }
        }

        Post saved = postService.savePost(post, tags);
        ra.addFlashAttribute("success", "Post saved!");

        return "redirect:/posts/" + saved.getId();
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        Post post = postService.findById(id);
        User user = userService.findById(userId);

        if (post != null && user != null) {
            likeService.likePost(user, post);
        }

        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/unlike")
    public String unlikePost(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        likeService.unlikePost(userId, id);
        return "redirect:/posts/" + id;
    }
}