package com.sinapipro.api.aftersales.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceTicketTaskRepository extends JpaRepository<ServiceTicketTask, UUID> {
    List<ServiceTicketTask> findByTicketIdOrderBySortOrder(UUID ticketId);
}
