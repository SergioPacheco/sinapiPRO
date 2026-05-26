package com.sinapipro.api.registry.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectSettingsRepository extends JpaRepository<ProjectSettings, UUID> {
    List<ProjectSettings> findByProjectId(UUID projectId);
    Optional<ProjectSettings> findByProjectIdAndKey(UUID projectId, String key);
}
