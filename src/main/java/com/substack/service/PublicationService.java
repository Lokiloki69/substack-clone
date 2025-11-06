package com.substack.service;

import com.substack.model.*;
import com.substack.repository.PublicationRepository;
import com.substack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;

    public Publication createPublication(Publication publication) {
        return publicationRepository.save(publication);
    }

    public Publication updatePublication(Publication publication) {
        return publicationRepository.save(publication);
    }

    public Optional<Publication> findBySlug(String slug) {
        return publicationRepository.findBySlug(slug);
    }

    public Optional<Publication> findById(Long id) {
        return publicationRepository.findById(id);
    }

    public List<Publication> getByOwnerId(Long ownerId) {
        return publicationRepository.findByOwnerId(ownerId);
    }

    public List<Publication> getAllActive() {
        return publicationRepository.findByActive(true);
    }

    public void addMember(Long publicationId, Long userId) {
        Optional<Publication> pub = publicationRepository.findById(publicationId);
        Optional<User> user = userRepository.findById(userId);

        if (pub.isPresent() && user.isPresent()) {
            Publication publication = pub.get();
            if (!publication.getMembers().contains(user.get())) {
                publication.getMembers().add(user.get());
                publicationRepository.save(publication);
            }
        }
    }

    public void removeMember(Long publicationId, Long userId) {
        Optional<Publication> pub = publicationRepository.findById(publicationId);
        Optional<User> user = userRepository.findById(userId);

        if (pub.isPresent() && user.isPresent()) {
            Publication publication = pub.get();
            publication.getMembers().remove(user.get());
            publicationRepository.save(publication);
        }
    }
}

