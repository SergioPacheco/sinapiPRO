package com.sinapipro.api.aftersales.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceTicketAttachmentRepository extends JpaRepository<ServiceTicketAttachment, UUID> {
    List<ServiceTicketAttachment> findByTicketId(UUID ticketId);
}
