package com.substack.repository;

import com.substack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findTop10ByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String q, String q1);
    User save(User user);
}
