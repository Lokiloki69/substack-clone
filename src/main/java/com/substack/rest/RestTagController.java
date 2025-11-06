package com.substack.rest;

import com.substack.model.Tag;
import com.substack.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class RestTagController {

    private final TagRepository tagRepo;

    @GetMapping("/search")
    public List<Tag> search(@RequestParam String q) {
        return tagRepo.findTop10ByNameContainingIgnoreCase(q);
    }

    @PostMapping
    public Tag create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        Tag tag = Tag.builder().name(name).build();
        return tagRepo.save(tag);
    }
}