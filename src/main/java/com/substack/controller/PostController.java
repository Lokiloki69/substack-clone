// src/main/java/com/substack/controller/PostController.java
package com.substack.controller;

import com.substack.model.Interest;
import com.substack.model.Post;
import com.substack.model.User;
import com.substack.service.LikeService;
import com.substack.service.PostService;
import com.substack.service.UserService;
import com.substack.service.auth.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private  final LikeService likeService;
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/new")
    public String newPost(Model model) {
        Post post = Post.builder()
                .title("")
                .subTitle("")
                .content("")
                .isPublished(true)
                .audience("everyone")
                .comments("everyone")
                .sendEmail(true)
                .author(authService.getCurrentUser())
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
            HttpSession session,
            RedirectAttributes ra) {

        var user = userService.findByEmail((String) session.getAttribute("email"));

        if (user.isEmpty()) {
            return "redirect:/auth/login";
        }

        User foundUser = user.get();
        Set<Interest> interests = foundUser.getInterests();

        post.setAuthor(foundUser);

        post.setAudience(audience != null ? audience : "everyone");
        post.setComments(comments != null ? comments : "everyone");
        post.setSendEmail(sendEmail);

        if (scheduledAt != null && !scheduledAt.isBlank()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            post.setScheduledAt(LocalDateTime.parse(scheduledAt, fmt));
        } else {
            post.setScheduledAt(null);
        }

        postService.savePost(post,foundUser);

//        ra.addFlashAttribute("success", "Post saved!");
//        return "redirect:/posts/" + post.getId();
        return "redirect:/";
    }



    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable Long id,Model model){
        Post post = postService.findById(id);
        User user = authService.getCurrentUser();
        boolean hasLiked = user != null && likeService.hasUserLikedPost(user.getId(), id);
        model.addAttribute("post", post);
        model.addAttribute("user", user);
        model.addAttribute("likeCount",post.getLikes().size());
        model.addAttribute("hasLiked", hasLiked);
        model.addAttribute("comments", post.getComments());
        model.addAttribute("postId", id);
        return "post/view";
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id, Model model) {
        User user = authService.getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Post post = postService.findById(id);
        model.addAttribute("post", post);
        if (post != null && user != null) {
            likeService.likePost(user, post);
        }
        return "redirect:/posts/view/" + id;
    }

}