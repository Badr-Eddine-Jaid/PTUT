package com.ptut.backend.repository;

import com.ptut.backend.entity.DocumentResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentResourceRepository extends JpaRepository<DocumentResource, Long> {
    List<DocumentResource> findAllByOrderByUploadedAtDesc();
}
