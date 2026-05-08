package com.sinapipro.api.notification.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByRecipientOrderByCreatedAtDesc(String recipient, Pageable pageable);
    Page<Notification> findByRecipientAndReadFalseOrderByCreatedAtDesc(String recipient, Pageable pageable);
    long countByRecipientAndReadFalse(String recipient);
    boolean existsByEntityTypeAndEntityIdAndType(String entityType, UUID entityId, String type);
}
