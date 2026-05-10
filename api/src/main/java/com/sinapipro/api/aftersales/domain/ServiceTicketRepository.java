package com.sinapipro.api.aftersales.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, UUID> {
    Page<ServiceTicket> findAll(Pageable pageable);
    Page<ServiceTicket> findByStatus(String status, Pageable pageable);
    List<ServiceTicket> findByUnitId(UUID unitId);
    long countByStatus(String status);
}
