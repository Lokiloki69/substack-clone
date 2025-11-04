package com.substack.service;

import com.substack.model.Post;
import com.substack.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public Post update(Post post) {
        return postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        postRepository.deleteById(id);
    }
}
