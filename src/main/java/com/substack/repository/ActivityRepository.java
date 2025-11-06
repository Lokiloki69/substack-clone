package com.substack.repository;

import com.substack.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>{
    List<Activity> findByUserIdOrderByCreatedAtDesc(Long userId);
    @Query("SELECT a FROM Activity a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<Activity> findRecentActivities(@Param("userId") Long userId, Pageable pageable);
}
