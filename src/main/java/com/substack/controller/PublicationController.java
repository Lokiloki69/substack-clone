package com.substack.controller;

import com.substack.model.Post;
import com.substack.model.Publication;
import com.substack.model.*;
import com.substack.repository.*;
import com.substack.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {
    private final PublicationService publicationService;
    private final UserService userService;
    private final PostRepository postRepository;

    @GetMapping("/{slug}")
    public String viewPublication(@PathVariable String slug, Model model) {
        PublicationService service = null;
        var publication = publicationService.findBySlug(slug);

        if (publication.isEmpty()) {
            return "redirect:/";
        }

        Publication pub = publication.get();
        List<Post> posts = postRepository.findByPublicationIdAndIsPublishedTrue(pub.getId());

        model.addAttribute("publication", pub);
        model.addAttribute("posts", posts);
        return "publication/view";
    }

    @GetMapping("/new")
    public String newPublication(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }
        return "publication/create";
    }

    @PostMapping("/save")
    public String savePublication(
            @ModelAttribute Publication publication,
            HttpSession session,
            RedirectAttributes ra) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        User user = userService.findById(userId);
        publication.setOwner(user);
        publication.setSlug(publication.getName().toLowerCase().replaceAll(" ", "-"));

        Publication saved = publicationService.createPublication(publication);
        ra.addFlashAttribute("success", "Publication created!");

        return "redirect:/publications/" + saved.getSlug();
    }

    @GetMapping("/settings/{id}")
    public String publicationSettings(@PathVariable Long id, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        var publication = publicationService.findById(id);
        if (publication.isEmpty() || !publication.get().getOwner().getId().equals(userId)) {
            return "redirect:/";
        }

        model.addAttribute("publication", publication.get());
        return "publication/settings";
    }

    @PostMapping("/{id}/add-member")
    public String addMember(
            @PathVariable Long id,
            @RequestParam String email,
            HttpSession session,
            RedirectAttributes ra) {

        Long userId = (Long) session.getAttribute("userId");
        var publication = publicationService.findById(id);

        if (publication.isEmpty() || !publication.get().getOwner().getId().equals(userId)) {
            return "redirect:/";
        }

        var memberUser = userService.findByEmail(email);
        if (memberUser.isPresent()) {
            publicationService.addMember(id, memberUser.get().getId());
            ra.addFlashAttribute("success", "Member added!");
        } else {
            ra.addFlashAttribute("error", "User not found");
        }

        return "redirect:/publications/settings/" + id;
    }
}
