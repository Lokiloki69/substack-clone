package com.substack.controller;

import com.substack.model.*;
import com.substack.repository.SubscriptionRepository;
import com.substack.repository.UserRepository;
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

    @PostMapping("/join/{authorId}")
    public String subscribe(
            @PathVariable Long authorId,
            @RequestParam SubscriptionType type,
            HttpSession session) {

        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/auth/login";

        subscriptionService.subscribe(email, authorId, type);

        return "redirect:/subscriptions/manage";
    }

    @GetMapping("/manage")
    public String manage(
            @RequestParam(defaultValue = "all") String filter,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/auth/login";

        model.addAttribute("subscriptions",
                subscriptionService.getSubscriptions(email, filter));

        model.addAttribute("filter", filter);

        return "subscriptions/manage";
    }
}
