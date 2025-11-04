package com.substack.service.auth;

import com.reddit.clone.entity.User;
import com.reddit.clone.repository.UserRepository;
import com.reddit.clone.service.cachecleaner.CacheCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CacheCleaner cacheCleaner;

    public AuthService(UserRepository userRepository, CacheCleaner cacheCleaner) {
        this.userRepository = userRepository;
        this.cacheCleaner = cacheCleaner;
    }

    public  User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            log.info("User logged in: {}", username); // INFO level log

            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        }

        log.info("No authenticated user found");
        cacheCleaner.clearAllCaches();
        return null;
    }
}