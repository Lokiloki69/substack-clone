// src/main/java/com/substack/service/PostService.java
package com.substack.service;

import com.substack.model.MediaFile;
import com.substack.model.Post;
import com.substack.model.Tag;
import com.substack.model.User;
import com.substack.repository.MediaFileRepository;
import com.substack.repository.PostRepository;
import com.substack.repository.TagRepository;
import com.substack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepo;
    private final MediaFileRepository mediaFileRepository;

    public Post savePost(Post post) {

        List<MediaFile> mediaFiles = new ArrayList<>();
        post.getFiles().forEach(mediaFile -> {
            MediaFile manage = mediaFileRepository.findById(mediaFile.getId()).get();
            manage.setPost(post);
            mediaFiles.add(manage);
        });
        post.setFiles(mediaFiles);
        return postRepo.save(post);
    }

    public Post findById(Long id) {
        return postRepo.findById(id).orElse(null);
    }
}