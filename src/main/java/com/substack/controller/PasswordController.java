package com.substack.controller;

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

@Controller
@RequiredArgsConstructor
@RequestMapping("/password")
public class PasswordController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // you already have mail logic

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
}

