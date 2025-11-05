package com.substack.controller;

import com.substack.model.User;
import com.substack.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {

        var user = userService.findByEmail(email);

        if (user.isEmpty()) {
            ra.addFlashAttribute("error", "User not found");
            return "redirect:/auth/login";
        }

        User foundUser = user.get();
        if (!passwordEncoder.matches(password, foundUser.getPassword())) {
            ra.addFlashAttribute("error", "Invalid password");
            return "redirect:/auth/login";
        }

        session.setAttribute("userId", foundUser.getId());
        session.setAttribute("userName", foundUser.getName());
        ra.addFlashAttribute("success", "Logged in successfully");

        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes ra) {

        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/auth/register";
        }

        if (userService.userExists(email)) {
            ra.addFlashAttribute("error", "Email already registered");
            return "redirect:/auth/register";
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        userService.saveUser(user);
        ra.addFlashAttribute("success", "Registration successful! Please login.");

        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "Logged out successfully");
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam String email,
            RedirectAttributes ra) {

        // TODO: Implement password reset logic
        // Send email with reset link

        ra.addFlashAttribute("success", "Check your email for password reset link");
        return "redirect:/auth/login";
    }
}
