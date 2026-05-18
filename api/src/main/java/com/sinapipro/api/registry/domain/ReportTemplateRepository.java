package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, UUID> {}
