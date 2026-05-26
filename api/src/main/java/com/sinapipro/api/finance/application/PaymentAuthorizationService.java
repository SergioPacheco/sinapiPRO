package com.sinapipro.api.finance.application;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// ═══════════════════════════════════════════════════════════
// Entities
// ═══════════════════════════════════════════════════════════

/** Alçada de aprovação: define quem pode aprovar até qual valor */
@Entity @Table(name = "payment_authority_level")
class PaymentAuthorityLevel extends TenantAwareEntity {
    @Column(name = "approver_name", nullable = false, length = 200) private String approverName;
    @Column(name = "approver_email", nullable = false, length = 200) private String approverEmail;
    @Column(name = "max_amount", nullable = false, precision = 18, scale = 2) private BigDecimal maxAmount;
    @Column(nullable = false) private int priority; // 1 = lowest authority, higher = more authority
    @Column(name = "project_id") private UUID projectId; // null = global
    @Column(nullable = false) private boolean active = true;

    protected PaymentAuthorityLevel() {}
    public PaymentAuthorityLevel(String approverName, String approverEmail, BigDecimal maxAmount, int priority, UUID projectId) {
        this.approverName = approverName; this.approverEmail = approverEmail;
        this.maxAmount = maxAmount; this.priority = priority; this.projectId = projectId;
    }
    public String getApproverName() { return approverName; }
    public String getApproverEmail() { return approverEmail; }
    public BigDecimal getMaxAmount() { return maxAmount; }
    public int getPriority() { return priority; }
    public UUID getProjectId() { return projectId; }
    public boolean isActive() { return active; }
}

/** Solicitação de autorização de pagamento */
@Entity @Table(name = "payment_authorization")
class PaymentAuthorization extends TenantAwareEntity {
    @Column(name = "payable_id") private UUID payableId;
    @Column(name = "project_id") private UUID projectId;
    @Column(nullable = false, length = 300) private String description;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "supplier_name", length = 200) private String supplierName;
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "requested_by", nullable = false, length = 200) private String requestedBy;
    @Column(name = "requested_at", nullable = false) private Instant requestedAt = Instant.now();
    @Column(nullable = false, length = 20) private String status = "PENDING"; // PENDING, APPROVED, REJECTED, CANCELLED
    @Column(name = "current_level", nullable = false) private int currentLevel = 1;
    @Column(length = 500) private String notes;

    protected PaymentAuthorization() {}
    public PaymentAuthorization(UUID payableId, UUID projectId, String description, BigDecimal amount,
                                 String supplierName, LocalDate dueDate, String requestedBy) {
        this.payableId = payableId; this.projectId = projectId; this.description = description;
        this.amount = amount; this.supplierName = supplierName; this.dueDate = dueDate; this.requestedBy = requestedBy;
    }
    public UUID getPayableId() { return payableId; }
    public UUID getProjectId() { return projectId; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public String getSupplierName() { return supplierName; }
    public LocalDate getDueDate() { return dueDate; }
    public String getRequestedBy() { return requestedBy; }
    public Instant getRequestedAt() { return requestedAt; }
    public String getStatus() { return status; }
    public int getCurrentLevel() { return currentLevel; }
    public String getNotes() { return notes; }

    public void approve() { this.status = "APPROVED"; }
    public void reject(String reason) { this.status = "REJECTED"; this.notes = reason; }
    public void escalate() { this.currentLevel++; }
    public void cancel() { this.status = "CANCELLED"; }
}

/** Registro de cada aprovação/rejeição no workflow */
@Entity @Table(name = "payment_authorization_history")
class PaymentAuthorizationHistory extends TenantAwareEntity {
    @Column(name = "authorization_id", nullable = false) private UUID authorizationId;
    @Column(nullable = false, length = 20) private String action; // APPROVED, REJECTED, ESCALATED
    @Column(name = "acted_by", nullable = false, length = 200) private String actedBy;
    @Column(name = "acted_at", nullable = false) private Instant actedAt = Instant.now();
    @Column(nullable = false) private int level;
    @Column(length = 500) private String comments;

    protected PaymentAuthorizationHistory() {}
    public PaymentAuthorizationHistory(UUID authorizationId, String action, String actedBy, int level, String comments) {
        this.authorizationId = authorizationId; this.action = action; this.actedBy = actedBy;
        this.level = level; this.comments = comments;
    }
    public UUID getAuthorizationId() { return authorizationId; }
    public String getAction() { return action; }
    public String getActedBy() { return actedBy; }
    public Instant getActedAt() { return actedAt; }
    public int getLevel() { return level; }
    public String getComments() { return comments; }
}

// ═══════════════════════════════════════════════════════════
// Repositories
// ═══════════════════════════════════════════════════════════

interface PaymentAuthorityLevelRepository extends JpaRepository<PaymentAuthorityLevel, UUID> {
    List<PaymentAuthorityLevel> findByActiveTrueOrderByPriority();
    List<PaymentAuthorityLevel> findByProjectIdAndActiveTrueOrderByPriority(UUID projectId);
}

interface PaymentAuthorizationRepository extends JpaRepository<PaymentAuthorization, UUID> {
    List<PaymentAuthorization> findByStatus(String status);
    List<PaymentAuthorization> findByProjectIdAndStatus(UUID projectId, String status);
}

interface PaymentAuthorizationHistoryRepository extends JpaRepository<PaymentAuthorizationHistory, UUID> {
    List<PaymentAuthorizationHistory> findByAuthorizationIdOrderByActedAt(UUID authorizationId);
}

// ═══════════════════════════════════════════════════════════
// Service
// ═══════════════════════════════════════════════════════════

@Service @Transactional
public class PaymentAuthorizationService {

    private final PaymentAuthorizationRepository authRepo;
    private final PaymentAuthorizationHistoryRepository historyRepo;
    private final PaymentAuthorityLevelRepository levelRepo;

    public PaymentAuthorizationService(PaymentAuthorizationRepository authRepo,
                                        PaymentAuthorizationHistoryRepository historyRepo,
                                        PaymentAuthorityLevelRepository levelRepo) {
        this.authRepo = authRepo; this.historyRepo = historyRepo; this.levelRepo = levelRepo;
    }

    /** Solicitar autorização de pagamento */
    public PaymentAuthorization request(UUID payableId, UUID projectId, String description,
                                         BigDecimal amount, String supplierName, LocalDate dueDate, String requestedBy) {
        return authRepo.save(new PaymentAuthorization(payableId, projectId, description, amount, supplierName, dueDate, requestedBy));
    }

    /** Aprovar — verifica se o aprovador tem alçada suficiente */
    public PaymentAuthorization approve(UUID authorizationId, String approverEmail, String comments) {
        var auth = authRepo.findById(authorizationId).orElseThrow();
        var levels = auth.getProjectId() != null
                ? levelRepo.findByProjectIdAndActiveTrueOrderByPriority(auth.getProjectId())
                : levelRepo.findByActiveTrueOrderByPriority();

        var approverLevel = levels.stream()
                .filter(l -> l.getApproverEmail().equalsIgnoreCase(approverEmail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Approver not configured: " + approverEmail));

        if (auth.getAmount().compareTo(approverLevel.getMaxAmount()) > 0) {
            // Valor acima da alçada — escalar para próximo nível
            auth.escalate();
            historyRepo.save(new PaymentAuthorizationHistory(authorizationId, "ESCALATED", approverEmail, auth.getCurrentLevel() - 1, "Amount exceeds authority. Escalated."));
            return authRepo.save(auth);
        }

        auth.approve();
        historyRepo.save(new PaymentAuthorizationHistory(authorizationId, "APPROVED", approverEmail, auth.getCurrentLevel(), comments));
        return authRepo.save(auth);
    }

    /** Rejeitar */
    public PaymentAuthorization reject(UUID authorizationId, String approverEmail, String reason) {
        var auth = authRepo.findById(authorizationId).orElseThrow();
        auth.reject(reason);
        historyRepo.save(new PaymentAuthorizationHistory(authorizationId, "REJECTED", approverEmail, auth.getCurrentLevel(), reason));
        return authRepo.save(auth);
    }

    /** Listar pendentes */
    public List<PaymentAuthorization> pending(UUID projectId) {
        return projectId != null
                ? authRepo.findByProjectIdAndStatus(projectId, "PENDING")
                : authRepo.findByStatus("PENDING");
    }

    /** Histórico de uma autorização */
    public List<PaymentAuthorizationHistory> history(UUID authorizationId) {
        return historyRepo.findByAuthorizationIdOrderByActedAt(authorizationId);
    }

    /** Configurar alçada */
    public PaymentAuthorityLevel createLevel(String name, String email, BigDecimal maxAmount, int priority, UUID projectId) {
        return levelRepo.save(new PaymentAuthorityLevel(name, email, maxAmount, priority, projectId));
    }

    public List<PaymentAuthorityLevel> listLevels() {
        return levelRepo.findByActiveTrueOrderByPriority();
    }
}
