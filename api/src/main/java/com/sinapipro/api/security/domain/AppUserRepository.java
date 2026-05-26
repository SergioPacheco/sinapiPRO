package com.sinapipro.api.security.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByExternalId(String externalId);
    List<AppUser> findByActiveTrue();
    boolean existsByEmail(String email);
}
