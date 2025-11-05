package com.substack.repository;

import com.substack.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>{
    List<Activity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Activity> findByUserIdOrderByCreatedAtDescLimit10(Long userId);
}
