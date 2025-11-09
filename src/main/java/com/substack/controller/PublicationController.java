package com.substack.controller;

import com.substack.model.Post;
import com.substack.model.Publication;
import com.substack.model.User;
import com.substack.service.PostService;
import com.substack.service.PublicationService;
import com.substack.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;
    private final PostService postService;
    private final UserService userService;

    // ✅ View publication
    @GetMapping("/{slug}")
    public String viewPublication(@PathVariable String slug, Model model) {

        var pubOpt = publicationService.findBySlug(slug);
        if (pubOpt.isEmpty()) return "redirect:/";

        Publication pub = pubOpt.get();
        List<Post> posts = postService.getPublicationPosts(pub.getId());

        model.addAttribute("publication", pub);
        model.addAttribute("posts", posts);

        return "publication/view";
    }

    // ✅ Create publication page
    @GetMapping("/new")
    public String newPublication(HttpSession session) {

        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/auth/login";

        return "publication/create";
    }

    // ✅ Save publication
    @PostMapping("/save")
    public String savePublication(@ModelAttribute Publication publication,
                                  HttpSession session,
                                  RedirectAttributes ra) {

        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/auth/login";

        User owner = userService.findByEmail(email).orElseThrow();

        publication.setOwner(owner);
        publication.setSlug(publication.getName().toLowerCase().replace(" ", "-"));

        Publication saved = publicationService.createPublication(publication);

        ra.addFlashAttribute("success", "Publication created");
        return "redirect:/publications/" + saved.getSlug();
    }

    // ✅ Settings page
    @GetMapping("/settings/{id}")
    public String settings(@PathVariable Long id,
                           Model model,
                           HttpSession session) {

        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/auth/login";

        User owner = userService.findByEmail(email).orElseThrow();

        var pubOpt = publicationService.findById(id);
        if (pubOpt.isEmpty()) return "redirect:/";

        Publication pub = pubOpt.get();

        // ✅ Only publication owner can see settings
        if (!pub.getOwner().getId().equals(owner.getId())) {
            return "redirect:/";
        }

        model.addAttribute("publication", pub);
        model.addAttribute("members", pub.getMembers());

        return "publication/settings";
    }

    // ✅ Add member to publication
    @PostMapping("/{id}/add-member")
    public String addMember(@PathVariable Long id,
                            @RequestParam String email,
                            HttpSession session,
                            RedirectAttributes ra) {

        String ownerEmail = (String) session.getAttribute("email");
        if (ownerEmail == null) return "redirect:/auth/login";

        User owner = userService.findByEmail(ownerEmail).orElseThrow();

        var pubOpt = publicationService.findById(id);
        if (pubOpt.isEmpty()) return "redirect:/";

        Publication pub = pubOpt.get();

        if (!pub.getOwner().getId().equals(owner.getId())) {
            return "redirect:/";
        }

        var userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            ra.addFlashAttribute("error", "User not found");
        } else {
            publicationService.addMember(id, userOpt.get().getId());
            ra.addFlashAttribute("success", "Member added!");
        }

        return "redirect:/publications/settings/" + id;
    }

    // ✅ Remove member
    @PostMapping("/{id}/remove-member/{userId}")
    public String removeMember(@PathVariable Long id,
                               @PathVariable Long userId,
                               HttpSession session) {

        String email = (String) session.getAttribute("email");
        if (email == null) return "redirect:/auth/login";

        User owner = userService.findByEmail(email).orElseThrow();

        var pubOpt = publicationService.findById(id);
        if (pubOpt.isEmpty()) return "redirect:/";

        Publication pub = pubOpt.get();

        if (!pub.getOwner().getId().equals(owner.getId())) return "redirect:/";

        publicationService.removeMember(id, userId);

        return "redirect:/publications/settings/" + id;
    }
}
