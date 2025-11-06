package com.substack.service;

import com.substack.model.Like;
import com.substack.model.Post;
import com.substack.model.*;
import com.substack.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public Like likePost(User user, Post post) {
        Optional<Like> existing = likeRepository.findByUserIdAndPostId(user.getId(), post.getId());

        if (existing.isPresent()) {
            return existing.get();
        }

        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();

        return likeRepository.save(like);
    }

    public void unlikePost(Long userId, Long postId) {
        Optional<Like> like = likeRepository.findByUserIdAndPostId(userId, postId);
        like.ifPresent(likeRepository::delete);
    }

    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isLiked(Long userId, Long postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }
}

