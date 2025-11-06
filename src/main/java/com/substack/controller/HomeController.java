package com.substack.controller;

import com.substack.model.Post;
import com.substack.model.Publication;
import com.substack.model.Subscription;
import com.substack.model.User;
import com.substack.repository.PostRepository;
import com.substack.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PostRepository postRepository;
    private final PublicationRepository publicationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Get trending/recommended posts
        List<Post> trendingPosts = postRepository.findByIsPublishedTrue();

        // Get publications
        List<Publication> publications = publicationRepository.findByActive(true);

        // Get user if logged in
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            model.addAttribute("currentUser", user);

            // Get user's subscriptions
            List<Subscription> subscriptions = subscriptionRepository
                    .findBySubscriberIdAndActive(userId, true);
            model.addAttribute("userSubscriptions", subscriptions);
        }

        model.addAttribute("posts", trendingPosts);
        model.addAttribute("publications", publications);
        return "index";
    }

    @GetMapping("/explore")
    public String explore(Model model) {
        List<Publication> publications = publicationRepository.findByActive(true);
        model.addAttribute("publications", publications);
        return "explore";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        // Simple search - implement full-text search later
        List<Post> posts = postRepository.findByIsPublishedTrue();
        List<User> users = userRepository.findAll();

        model.addAttribute("posts", posts);
        model.addAttribute("users", users);
        model.addAttribute("query", q);
        return "search";
    }
}
