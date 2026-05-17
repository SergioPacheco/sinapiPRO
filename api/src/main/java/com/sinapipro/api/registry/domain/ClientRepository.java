package com.sinapipro.api.registry.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Page<Client> findByActiveTrue(Pageable pageable);
    Page<Client> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
}
