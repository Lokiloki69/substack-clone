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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PostRepository postRepository;
    private final PublicationRepository publicationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final UserService userService;

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "trending") String feedType,
            Model model,
            HttpSession session) {

//        Long userId = (Long) session.getAttribute("userId");
        Optional<User> currentUser = userService.findByEmail((String) session.getAttribute("email"));
        ;

        if (currentUser.isPresent()) {
//            currentUser = userRepository.findById(userId).orElse(null);
            model.addAttribute("currentUser", currentUser.get());
        }

        List<Post> posts = postService.getPosts(feedType, currentUser);

        List<Publication> publications = publicationRepository.findByActive(true);

        model.addAttribute("posts", posts);
        model.addAttribute("publications", publications);
        model.addAttribute("selectedFeed", feedType);

        return "index";
    }

    @GetMapping("/explore")
    public String explore(Model model) {
        List<User> creators = userRepository.findAll(); // Filter later if needed
        model.addAttribute("creators", creators);
        return "user/explore";
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
