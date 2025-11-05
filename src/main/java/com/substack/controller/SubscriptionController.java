package com.substack.controller;

import com.substack.model.*;
import com.substack.service.SubscriptionService;
import com.substack.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserService userService;

    @PostMapping("/{authorId}/subscribe")
    public String subscribe(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "FREE") String type,
            HttpSession session,
            RedirectAttributes ra) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        User subscriber = userService.findById(userId);
        User author = userService.findById(authorId);

        if (subscriber != null && author != null) {
            SubscriptionType subType = SubscriptionType.valueOf(type);
            subscriptionService.subscribe(subscriber, author, subType, null);
            ra.addFlashAttribute("success", "Subscribed!");
        }

        return "redirect:/user/" + authorId;
    }

    @PostMapping("/{authorId}/unsubscribe")
    public String unsubscribe(
            @PathVariable Long authorId,
            HttpSession session,
            RedirectAttributes ra) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        subscriptionService.unsubscribe(userId, authorId);
        ra.addFlashAttribute("success", "Unsubscribed!");

        return "redirect:/user/" + authorId;
    }

    @GetMapping("/manage")
    public String manageSubscriptions(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        User user = userService.findById(userId);
        List<Subscription> subscriptions = subscriptionService.getSubscriptions(user);

        model.addAttribute("subscriptions", subscriptions);
        return "subscription/manage";
    }
}

