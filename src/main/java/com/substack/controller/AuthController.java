package com.substack.controller;

import com.substack.dto.SignupDTO;
import com.substack.model.PasswordResetToken;
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

//import ch.qos.logback.core.model.Model;
import com.substack.model.*;
import com.substack.repository.*;
import com.substack.service.*;
import lombok.*;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
//    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // you already have mail logic

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
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "No account found for that email.");
            return "forgot-password";
        }

        User user = userOpt.get();

        String token = UUID.randomUUID().toString();

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiryDate(Instant.now().plusSeconds(15 * 60));
        tokenRepository.save(prt);

        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

        emailService.send(email, "Reset your password",
                "Click the link to reset your password:\n" + resetLink);

        model.addAttribute("message", "Password reset link sent to your email.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleReset(
            @RequestParam String token,
            @RequestParam String password,
            Model model
    ) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElse(null);

        if (prt == null || prt.getExpiryDate().isBefore(Instant.now())) {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset-password";
        }

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        tokenRepository.delete(prt);

        model.addAttribute("success", "Password updated. Please login.");
        return "reset-password";
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
