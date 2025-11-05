// src/main/java/com/substack/controller/PostController.java
package com.substack.controller;

import com.substack.model.Post;
import com.substack.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/new")
    public String newPost(Model model) {
        Post post = Post.builder()
                .title("")
                .subTitle("")
                .content("")
                .isPublished(false)
                .audience("everyone")
                .comments("everyone")
                .sendEmail(true)
                .build();
        model.addAttribute("post", post);
        return "post/create";
    }

    @PostMapping("/save")
    public String savePost(
            @ModelAttribute Post post,
            @RequestParam(required = false) String scheduledAt,
            @RequestParam(required = false) String audience,
            @RequestParam(required = false) String comments,
            @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "true") boolean sendEmail,
            RedirectAttributes ra) {

        post.setAudience(audience != null ? audience : "everyone");
        post.setComments(comments != null ? comments : "everyone");
        post.setSendEmail(sendEmail);

        if (scheduledAt != null && !scheduledAt.isBlank()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            post.setScheduledAt(LocalDateTime.parse(scheduledAt, fmt));
        } else {
            post.setScheduledAt(null);
        }

        postService.savePost(post);

//        ra.addFlashAttribute("success", "Post saved!");
//        return "redirect:/posts/" + post.getId();
        return "post/create";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        if (post == null) return "redirect:/";
        model.addAttribute("post", post);
        return "post/view";
    }

    @GetMapping("/edit/{id}")
    public String editPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        if (post == null) return "redirect:/";
        model.addAttribute("post", post);
        return "editor";
    }
}