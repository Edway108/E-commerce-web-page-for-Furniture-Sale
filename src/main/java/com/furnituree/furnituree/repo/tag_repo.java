package com.furnituree.furnituree.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.furnituree.furnituree.model.Tag;

public interface tag_repo extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameIgnoreCase(String name);

    List<Tag> findByStatusIgnoreCaseOrderByNameAsc(String status);

    boolean existsByNameIgnoreCase(String name);
}
