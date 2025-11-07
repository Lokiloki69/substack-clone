package com.substack.config;

import com.substack.model.User;
import com.substack.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModel {

    private final UserService userService;

    @ModelAttribute("currentUser")
    public User addCurrentUser(HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) return null;
        return userService.findByEmail(email).orElse(null);
    }
}
