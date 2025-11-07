package com.substack.controller;

import com.substack.dto.SignupDTO;
import com.substack.model.User;
import com.substack.repository.InterestRepository;
import com.substack.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final InterestRepository interestRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        var user = userService.findByEmail(email);

        if (user.isEmpty()) {
            return "redirect:/auth/login";
        }

        User foundUser = user.get();
        if (!passwordEncoder.matches(password, foundUser.getPassword())) {
            return "redirect:/auth/login";
        }
        authenticateUser(foundUser,session);
        model.addAttribute("user", foundUser);
        session.setAttribute("email",foundUser.getEmail());

        return "redirect:/";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        if (userEmail == null) {
            return "redirect:/auth/login";
        }

        User user = userService.findByEmail(userEmail).get();
        model.addAttribute("user", user);
        model.addAttribute("posts",user.getPosts());
        return "user/profile";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("interests", interestRepository.findAllByOrderByNameAsc());
        model.addAttribute("signupForm", new SignupDTO());
        return "user/signup";
    }

    // === POST: Handle registration ===
    @PostMapping("/register")
    public String register(
            @ModelAttribute("signupForm") SignupDTO form,
            @RequestParam String confirmPassword,
            RedirectAttributes ra) {

        // 1. Password match
        if (!form.getPassword().equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/auth/register";
        }

        // 2. Email already exists
        if (userService.userExists(form.getEmail())) {
            ra.addFlashAttribute("error", "Email already registered");
            return "redirect:/auth/register";
        }

        // 3. Save user + interests
        try {
            userService.saveUserWithInterests(form);
            ra.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("Registration failed", e);
            ra.addFlashAttribute("error", "Something went wrong. Try again.");
            return "redirect:/auth/register";
        }
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

    private void authenticateUser(User user,  HttpSession session) {

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password("")
                .roles("USER")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
}
