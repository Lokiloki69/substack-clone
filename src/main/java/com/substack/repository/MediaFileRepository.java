package com.substack.repository;

import com.substack.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> { }
