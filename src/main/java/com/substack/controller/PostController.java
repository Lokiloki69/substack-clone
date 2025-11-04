package com.substack.controller;

import com.substack.model.Post;
import com.substack.model.Users;
import com.substack.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // Show create post form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/create";
    }

    // Save or update post
    @PostMapping("/save")
    public String savePost(@RequestParam("title") String title,
                           @RequestParam("content") String content,
                           @RequestParam(value = "subtitle", required = false) String subtitle,
                           @RequestParam(value = "isPublished", defaultValue = "true") boolean isPublished) {

        Post post = Post.builder()
                .title(title)
                .content(content)
                .isPublished(isPublished)
//                .author()
                .build();

        postService.save(post);

        return "redirect:/posts/" + post.getId();
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "edit-post";
    }

    // Update post
    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Long id,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value = "isPublished", defaultValue = "true") boolean isPublished) {

        Post post = postService.findById(id);
        post.setTitle(title);
        post.setContent(content);
        post.setPublished(isPublished);

        postService.update(post);

        return "redirect:/posts/" + id;
    }

    // View single post
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "post/view";
    }
}
