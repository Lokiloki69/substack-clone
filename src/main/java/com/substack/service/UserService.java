package com.substack.service;

import com.substack.model.User;
import com.substack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
}

