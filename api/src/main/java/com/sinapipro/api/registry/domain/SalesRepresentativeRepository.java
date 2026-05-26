package com.sinapipro.api.registry.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SalesRepresentativeRepository extends JpaRepository<SalesRepresentative, UUID> {
}
