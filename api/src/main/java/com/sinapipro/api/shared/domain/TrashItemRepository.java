package com.sinapipro.api.shared.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TrashItemRepository extends JpaRepository<TrashItem, UUID> {
    List<TrashItem> findByEntityTypeOrderByDeletedAtDesc(String entityType);
    List<TrashItem> findAllByOrderByDeletedAtDesc();
    List<TrashItem> findByExpiresAtBefore(Instant now);
}
