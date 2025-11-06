package com.substack.repository;

import com.substack.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long>{
    Optional<Publication> findBySlug(String slug);
    List<Publication> findByOwnerId(Long ownerId);
    List<Publication> findByActive(boolean active);
}