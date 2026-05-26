package com.sinapipro.api.aftersales.application;

import com.sinapipro.api.aftersales.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Sprint 14 — Ordem de Serviço / Atendimento.
 */
@Service
@Transactional
public class ServiceTicketService {

    private final ServiceTicketRepository ticketRepo;
    private final ServiceTicketTaskRepository taskRepo;
    private final ServiceTicketAttachmentRepository attachmentRepo;

    public ServiceTicketService(ServiceTicketRepository ticketRepo, ServiceTicketTaskRepository taskRepo,
                                 ServiceTicketAttachmentRepository attachmentRepo) {
        this.ticketRepo = ticketRepo; this.taskRepo = taskRepo; this.attachmentRepo = attachmentRepo;
    }

    /** 14.1 — Criar OS */
    public ServiceTicket create(UUID unitId, String clientName, String category,
                                 String description, String priority, LocalDate dueDate) {
        return ticketRepo.save(new ServiceTicket(unitId, clientName, category, description, priority, dueDate));
    }

    /** 14.1 — Atribuir */
    public ServiceTicket assign(UUID ticketId, String assignedTo) {
        var ticket = findOrThrow(ticketId);
        ticket.assign(assignedTo);
        return ticketRepo.save(ticket);
    }

    /** 14.1 — Resolver */
    public ServiceTicket resolve(UUID ticketId, String resolution) {
        var ticket = findOrThrow(ticketId);
        ticket.resolve(resolution);
        return ticketRepo.save(ticket);
    }

    /** 14.1 — Encerrar */
    public ServiceTicket close(UUID ticketId) {
        var ticket = findOrThrow(ticketId);
        ticket.close();
        return ticketRepo.save(ticket);
    }

    /** 14.3 — SLA: tickets atrasados */
    public List<ServiceTicket> overdueTickets() {
        return ticketRepo.findByStatusInAndDueDateBefore(
                List.of("OPEN", "IN_PROGRESS"), LocalDate.now());
    }

    /** 14.4 — Estatísticas de atendimento */
    public AttendanceStats stats() {
        var all = ticketRepo.findAll();
        var total = all.size();
        var open = all.stream().filter(t -> "OPEN".equals(t.getStatus())).count();
        var inProgress = all.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count();
        var resolved = all.stream().filter(t -> "RESOLVED".equals(t.getStatus()) || "CLOSED".equals(t.getStatus())).count();
        var overdue = all.stream().filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now())
                && !"CLOSED".equals(t.getStatus()) && !"RESOLVED".equals(t.getStatus())).count();

        // Tempo médio de resolução
        var avgResolutionHours = all.stream()
                .filter(t -> t.getResolvedAt() != null)
                .mapToLong(t -> Duration.between(t.getOpenedAt(), t.getResolvedAt()).toHours())
                .average().orElse(0);

        return new AttendanceStats(total, open, inProgress, resolved, overdue, avgResolutionHours);
    }

    /** 14.7 — Histórico por unidade/cliente */
    public Page<ServiceTicket> findByUnit(UUID unitId, Pageable pageable) {
        return ticketRepo.findByUnitId(unitId, pageable);
    }

    public Page<ServiceTicket> findAll(Pageable pageable) {
        return ticketRepo.findAll(pageable);
    }

    private ServiceTicket findOrThrow(UUID id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Service ticket not found: " + id));
    }

    // ═══════════════════════════════════════════════════════════
    // 14.2 — Tarefas/checklist
    // ═══════════════════════════════════════════════════════════

    public ServiceTicketTask addTask(UUID ticketId, String description, int sortOrder) {
        return taskRepo.save(new ServiceTicketTask(ticketId, description, sortOrder));
    }

    public ServiceTicketTask completeTask(UUID taskId, String completedBy) {
        var task = taskRepo.findById(taskId).orElseThrow(() -> new DomainNotFoundException("Task not found: " + taskId));
        task.complete(completedBy);
        return taskRepo.save(task);
    }

    public List<ServiceTicketTask> listTasks(UUID ticketId) {
        return taskRepo.findByTicketIdOrderBySortOrder(ticketId);
    }

    // ═══════════════════════════════════════════════════════════
    // 14.5 — Notificação por email (event-based)
    // ═══════════════════════════════════════════════════════════

    public NotificationResult notifyClient(UUID ticketId, String eventType) {
        var ticket = findOrThrow(ticketId);
        // In production: publish event to notification service
        // NotificationEvent.of(ticket.getClientName(), eventType, ticket.getDescription())
        return new NotificationResult(ticketId, ticket.getClientName(), eventType, "QUEUED");
    }

    // ═══════════════════════════════════════════════════════════
    // 14.6 — Anexos
    // ═══════════════════════════════════════════════════════════

    public ServiceTicketAttachment addAttachment(UUID ticketId, String fileName, String filePath,
                                                  String contentType, Long fileSize, String uploadedBy) {
        return attachmentRepo.save(new ServiceTicketAttachment(ticketId, fileName, filePath, contentType, fileSize, uploadedBy));
    }

    public List<ServiceTicketAttachment> listAttachments(UUID ticketId) {
        return attachmentRepo.findByTicketId(ticketId);
    }

    public record NotificationResult(UUID ticketId, String clientName, String eventType, String status) {}

    public record AttendanceStats(long total, long open, long inProgress, long resolved, long overdue, double avgResolutionHours) {}
}
