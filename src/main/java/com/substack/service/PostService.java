package com.substack.service;

import com.substack.exceptions.ResourceNotFoundException;
import com.substack.model.Post;
import com.substack.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post createPost(Post post) {
        post.setPublished(false);
        return postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}

