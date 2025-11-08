package com.substack.controller;

import com.cloudinary.Uploader;
import com.substack.model.*;
import com.substack.service.*;
import com.substack.service.auth.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final AuthService authService;
    private final CloudinaryService cloudinaryService;

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

    @PostMapping("/user/updateProfile")
    public String updateProfile(@RequestParam(value = "profileImage", required = false) MultipartFile file,
                                @RequestParam(value = "bio", required = false) String bio) {
        User user = authService.getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        // ✅ Update bio
        if (bio != null) {
            user.setBio(bio.trim());
        }

        // ✅ Update profile image if uploaded
        if (file != null && !file.isEmpty()) {
            try {
                // Example: Use Cloudinary or local storage
                String imageUrl = cloudinaryService.uploadFile(file);
                user.setProfileImageUrl(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        userService.save(user); // persist updates
        return "redirect:/posts/profile"; // reload updated profile
    }

}
