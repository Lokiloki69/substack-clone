package com.substack.service;

import com.substack.model.User;
import com.substack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public List<User> searchUserList(String q) {
        return userRepository.findTop10ByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(q, q);
    }
}
