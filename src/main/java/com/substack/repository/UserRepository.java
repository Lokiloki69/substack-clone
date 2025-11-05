package com.substack.repository;

import com.substack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    List<User> findTop10ByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String q, String q1);
}
