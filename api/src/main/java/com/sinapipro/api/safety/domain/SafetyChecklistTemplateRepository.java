package com.sinapipro.api.safety.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SafetyChecklistTemplateRepository extends JpaRepository<SafetyChecklistTemplate, UUID> {
    List<SafetyChecklistTemplate> findByActiveTrue();
    List<SafetyChecklistTemplate> findByCategory(String category);
}
