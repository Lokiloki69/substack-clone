package com.substack.controller;

import com.substack.model.*;
import com.substack.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/{id}")
    public String userProfile(@PathVariable Long id, Model model, HttpSession session) {

        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/";
        }

        Long currentUserId = (Long) session.getAttribute("userId");
        model.addAttribute("user", user);
        model.addAttribute("isCurrentUser", user.getId().equals(currentUserId));

        // ✅ Add counts needed by your HTML
        model.addAttribute("postCount", user.getPosts().size());
        model.addAttribute("subscriberCount", subscriptionService.countSubscribers(id));
        model.addAttribute("subscriptionsCount", subscriptionService.countFollowing(id));
        model.addAttribute("posts", user.getPosts());

        if (currentUserId != null && !user.getId().equals(currentUserId)) {
            boolean isSubscribed = subscriptionService.isSubscribed(currentUserId, id);
            model.addAttribute("isSubscribed", isSubscribed);
        }

        return "user/profile";
    }


    @GetMapping("/settings")
    public String userSettings(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "user/settings";
    }

    @PostMapping("/update")
    public String updateProfile(
            @ModelAttribute User user,
            HttpSession session,
            RedirectAttributes ra) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        User existing = userService.findById(userId);
        existing.setName(user.getName());
        existing.setBio(user.getBio());
        existing.setWebsite(user.getWebsite());
        existing.setTwitterHandle(user.getTwitterHandle());
        existing.setProfileImageUrl(user.getProfileImageUrl());

        userService.saveUser(existing);
        ra.addFlashAttribute("success", "Profile updated!");

        return "redirect:/user/settings";
    }
}
