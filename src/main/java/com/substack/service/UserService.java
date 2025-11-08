package com.substack.service;

import com.substack.dto.SignupDTO;
import com.substack.model.Interest;
import com.substack.model.User;
import com.substack.repository.InterestRepository;
import com.substack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> searchUserList(String q) {
        return userRepository.findTop10ByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(q, q);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean userExists(String email) {
        return userRepository.findByEmailIgnoreCase(email).isPresent();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public  List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUserWithInterests(SignupDTO form) {
        User user = User.builder()
                .name(form.getName())
                .username(form.getUsername())
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .build();

        if (form.getInterestIds() != null && !form.getInterestIds().isEmpty()) {
            Set<Interest> interests = new HashSet<>(interestRepository.findAllById(form.getInterestIds()));
            user.setInterests(interests);
        }

        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}

