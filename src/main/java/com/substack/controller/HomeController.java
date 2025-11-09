package com.substack.controller;

import com.substack.model.Post;
import com.substack.model.Publication;
import com.substack.model.User;
import com.substack.repository.UserRepository;
import com.substack.service.*;
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

    private final FeedService feedService;
    private final UserService userService;
    private final PublicationService publicationService;
    private final ExploreService exploreService;

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "following") String feedType,
            Model model,
            HttpSession session
    ) {
        String email = (String) session.getAttribute("email");
        Optional<User> currentUser = email == null
                ? Optional.empty()
                : userService.findByEmail(email);

        List<Post> posts = feedService.getPosts(feedType, currentUser);

        List<Publication> publications = publicationService.findActivePublications();

        model.addAttribute("posts", posts);
        model.addAttribute("publications", publications);
        model.addAttribute("currentUser", currentUser.orElse(null));
        model.addAttribute("selectedFeed", feedType);

        return "index";
    }

    @GetMapping("/explore")
    public String explore(Model model) {
        model.addAttribute("trending", exploreService.getTrendingPosts());
        model.addAttribute("topAuthors", exploreService.getTopAuthors());
        model.addAttribute("publications", exploreService.getTopPublications());
        return "user/explore";
    }


    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        model.addAttribute("query", q);
        return "search"; // Will refine later
    }
}
