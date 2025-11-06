package com.substack.service;

import com.substack.model.Activity;
import com.substack.model.User;
import com.substack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;

    public Activity logActivity(User user, String type, String description) {
        Activity activity = Activity.builder()
                .user(user)
                .type(type)
                .description(description)
                .build();

        return activityRepository.save(activity);
    }

    public List<Activity> getUserActivity(Long userId) {
        return activityRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
