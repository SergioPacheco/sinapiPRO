package com.sinapipro.api.registry.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ContactInfoRepository extends JpaRepository<ContactInfo, UUID> {
    List<ContactInfo> findByEntityTypeAndEntityId(String entityType, UUID entityId);
}
