package com.sinapipro.api.commercial.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DevelopmentRepository extends JpaRepository<Development, UUID> {
    Page<Development> findAll(Pageable pageable);
}
