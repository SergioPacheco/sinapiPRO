package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.ContactDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

public interface ClientContactRepository extends JpaRepository<ClientContact, UUID> {
    Page<ClientContact> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);

    @Modifying
    @Query("UPDATE ClientContact c SET c.primary = false WHERE c.clientId = :clientId AND c.department = :dept AND c.id != :excludeId")
    void clearPrimaryForDepartment(UUID clientId, ContactDepartment dept, UUID excludeId);
}
