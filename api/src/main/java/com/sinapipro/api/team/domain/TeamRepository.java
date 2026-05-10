package com.sinapipro.api.team.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByProjectId(UUID projectId);
    List<Team> findByActiveTrue();
}
