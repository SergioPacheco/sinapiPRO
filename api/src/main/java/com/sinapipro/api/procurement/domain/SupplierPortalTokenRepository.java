package com.sinapipro.api.procurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SupplierPortalTokenRepository extends JpaRepository<SupplierPortalToken, UUID> {
    Optional<SupplierPortalToken> findByToken(String token);
}
