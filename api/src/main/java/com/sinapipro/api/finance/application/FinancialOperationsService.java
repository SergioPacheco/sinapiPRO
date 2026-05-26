package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.PayableRepository;
import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

// ═══════════════════════════════════════════════════════════
// FECHAMENTO DE PERÍODO (P1)
// ═══════════════════════════════════════════════════════════

@Entity @Table(name = "period_closing", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "project_id", "reference_month"}))
class PeriodClosing extends TenantAwareEntity {
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "reference_month", nullable = false) private LocalDate referenceMonth;
    @Column(nullable = false, length = 20) private String status = "OPEN"; // OPEN, CLOSED
    @Column(name = "closed_by", length = 200) private String closedBy;
    @Column(name = "closed_at") private Instant closedAt;
    @Column(length = 300) private String notes;

    protected PeriodClosing() {}
    public PeriodClosing(UUID projectId, LocalDate referenceMonth) { this.projectId = projectId; this.referenceMonth = referenceMonth; }
    public UUID getProjectId() { return projectId; }
    public LocalDate getReferenceMonth() { return referenceMonth; }
    public String getStatus() { return status; }
    public String getClosedBy() { return closedBy; }
    public Instant getClosedAt() { return closedAt; }
    public boolean isClosed() { return "CLOSED".equals(status); }
    public void close(String by) { this.status = "CLOSED"; this.closedBy = by; this.closedAt = Instant.now(); }
    public void reopen() { this.status = "OPEN"; this.closedBy = null; this.closedAt = null; }
}

// ═══════════════════════════════════════════════════════════
// DISTRIBUIÇÃO DE DIVIDENDOS (P2)
// ═══════════════════════════════════════════════════════════

@Entity @Table(name = "dividend_distribution")
class DividendDistribution extends TenantAwareEntity {
    @Column(name = "reference_month", nullable = false) private LocalDate referenceMonth;
    @Column(name = "total_profit", nullable = false, precision = 18, scale = 2) private BigDecimal totalProfit;
    @Column(name = "partner_name", nullable = false, length = 200) private String partnerName;
    @Column(name = "partner_share_pct", nullable = false, precision = 5, scale = 2) private BigDecimal partnerSharePct;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "paid_date") private LocalDate paidDate;
    @Column(nullable = false, length = 20) private String status = "PENDING";

    protected DividendDistribution() {}
    public DividendDistribution(LocalDate referenceMonth, BigDecimal totalProfit, String partnerName, BigDecimal partnerSharePct) {
        this.referenceMonth = referenceMonth; this.totalProfit = totalProfit;
        this.partnerName = partnerName; this.partnerSharePct = partnerSharePct;
        this.amount = totalProfit.multiply(partnerSharePct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
    public String getPartnerName() { return partnerName; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public void pay(LocalDate date) { this.paidDate = date; this.status = "PAID"; }
}

// ═══════════════════════════════════════════════════════════
// AJUSTE DE DESPESA (P2) — reclassificação contábil
// ═══════════════════════════════════════════════════════════

@Entity @Table(name = "expense_adjustment")
class ExpenseAdjustment extends TenantAwareEntity {
    @Column(name = "payable_id", nullable = false) private UUID payableId;
    @Column(name = "original_category", nullable = false, length = 60) private String originalCategory;
    @Column(name = "new_category", nullable = false, length = 60) private String newCategory;
    @Column(name = "original_cost_center", length = 80) private String originalCostCenter;
    @Column(name = "new_cost_center", length = 80) private String newCostCenter;
    @Column(nullable = false, length = 200) private String reason;
    @Column(name = "adjusted_by", nullable = false, length = 200) private String adjustedBy;
    @Column(name = "adjusted_at", nullable = false) private Instant adjustedAt = Instant.now();

    protected ExpenseAdjustment() {}
    public ExpenseAdjustment(UUID payableId, String originalCategory, String newCategory,
                              String originalCostCenter, String newCostCenter, String reason, String adjustedBy) {
        this.payableId = payableId; this.originalCategory = originalCategory; this.newCategory = newCategory;
        this.originalCostCenter = originalCostCenter; this.newCostCenter = newCostCenter;
        this.reason = reason; this.adjustedBy = adjustedBy;
    }
    public UUID getPayableId() { return payableId; }
    public String getNewCategory() { return newCategory; }
    public String getNewCostCenter() { return newCostCenter; }
    public String getReason() { return reason; }
}

// ═══════════════════════════════════════════════════════════
// AGRUPADOR FINANCEIRO (P2) — consolidação de contas
// ═══════════════════════════════════════════════════════════

@Entity @Table(name = "financial_grouper")
class FinancialGrouper extends TenantAwareEntity {
    @Column(nullable = false, length = 20) private String code;
    @Column(nullable = false, length = 200) private String name;
    @Column(name = "parent_id") private UUID parentId;
    @Column(nullable = false) private boolean active = true;

    protected FinancialGrouper() {}
    public FinancialGrouper(String code, String name, UUID parentId) { this.code = code; this.name = name; this.parentId = parentId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public UUID getParentId() { return parentId; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
}

// ═══════════════════════════════════════════════════════════
// Repositories
// ═══════════════════════════════════════════════════════════

interface PeriodClosingRepository extends JpaRepository<PeriodClosing, UUID> {
    java.util.Optional<PeriodClosing> findByProjectIdAndReferenceMonth(UUID projectId, LocalDate referenceMonth);
    List<PeriodClosing> findByProjectIdOrderByReferenceMonthDesc(UUID projectId);
}

interface DividendDistributionRepository extends JpaRepository<DividendDistribution, UUID> {
    List<DividendDistribution> findByReferenceMonth(LocalDate month);
}

interface ExpenseAdjustmentRepository extends JpaRepository<ExpenseAdjustment, UUID> {
    List<ExpenseAdjustment> findByPayableId(UUID payableId);
}

interface FinancialGrouperRepository extends JpaRepository<FinancialGrouper, UUID> {
    List<FinancialGrouper> findByParentIdIsNullAndActiveTrue();
    List<FinancialGrouper> findByParentIdAndActiveTrue(UUID parentId);
}

// ═══════════════════════════════════════════════════════════
// Service
// ═══════════════════════════════════════════════════════════

@Service @Transactional
public class FinancialOperationsService {

    private final PeriodClosingRepository periodRepo;
    private final DividendDistributionRepository dividendRepo;
    private final ExpenseAdjustmentRepository adjustmentRepo;
    private final FinancialGrouperRepository grouperRepo;
    private final PayableRepository payableRepo;

    public FinancialOperationsService(PeriodClosingRepository periodRepo, DividendDistributionRepository dividendRepo,
                                       ExpenseAdjustmentRepository adjustmentRepo, FinancialGrouperRepository grouperRepo,
                                       PayableRepository payableRepo) {
        this.periodRepo = periodRepo; this.dividendRepo = dividendRepo;
        this.adjustmentRepo = adjustmentRepo; this.grouperRepo = grouperRepo; this.payableRepo = payableRepo;
    }

    // --- Fechamento de Período ---

    /** Verifica se um mês está fechado para lançamentos */
    public boolean isPeriodClosed(UUID projectId, LocalDate date) {
        var month = date.withDayOfMonth(1);
        return periodRepo.findByProjectIdAndReferenceMonth(projectId, month)
                .map(PeriodClosing::isClosed).orElse(false);
    }

    /** Fecha um período (bloqueia lançamentos retroativos) */
    public PeriodClosing closePeriod(UUID projectId, YearMonth month, String closedBy) {
        var refMonth = month.atDay(1);
        var period = periodRepo.findByProjectIdAndReferenceMonth(projectId, refMonth)
                .orElseGet(() -> new PeriodClosing(projectId, refMonth));
        period.close(closedBy);
        return periodRepo.save(period);
    }

    /** Reabre um período (requer permissão especial) */
    public PeriodClosing reopenPeriod(UUID projectId, YearMonth month) {
        var refMonth = month.atDay(1);
        var period = periodRepo.findByProjectIdAndReferenceMonth(projectId, refMonth).orElseThrow();
        period.reopen();
        return periodRepo.save(period);
    }

    public List<PeriodClosing> listPeriods(UUID projectId) {
        return periodRepo.findByProjectIdOrderByReferenceMonthDesc(projectId);
    }

    // --- Distribuição de Dividendos ---

    /** Distribui lucro entre sócios conforme participação */
    public List<DividendDistribution> distributeDividends(LocalDate referenceMonth, BigDecimal totalProfit,
                                                          List<PartnerShare> partners) {
        var distributions = partners.stream()
                .map(p -> new DividendDistribution(referenceMonth, totalProfit, p.name(), p.sharePct()))
                .toList();
        return dividendRepo.saveAll(distributions);
    }

    /** Pagar dividendo */
    public DividendDistribution payDividend(UUID id, LocalDate paidDate) {
        var d = dividendRepo.findById(id).orElseThrow();
        d.pay(paidDate);
        return dividendRepo.save(d);
    }

    // --- Ajuste de Despesa ---

    /** Reclassifica uma despesa (muda categoria e/ou centro de custo) */
    public ExpenseAdjustment adjustExpense(UUID payableId, String newCategory, String newCostCenter,
                                            String reason, String adjustedBy) {
        var payable = payableRepo.findById(payableId).orElseThrow();
        var adjustment = new ExpenseAdjustment(payableId, payable.getCategory(),
                newCategory, null, newCostCenter, reason, adjustedBy);
        return adjustmentRepo.save(adjustment);
    }

    // --- Troca de Centro de Custo em Lote ---

    /** Troca CC de múltiplos payables de uma vez */
    public int batchChangeCostCenter(List<UUID> payableIds, String newCostCenter, String reason, String changedBy) {
        var payables = payableRepo.findAllById(payableIds);
        for (var p : payables) {
            adjustmentRepo.save(new ExpenseAdjustment(p.getId(), p.getCategory(), p.getCategory(),
                    null, newCostCenter, reason, changedBy));
        }
        return payables.size();
    }

    // --- Agrupadores Financeiros ---

    public FinancialGrouper createGrouper(String code, String name, UUID parentId) {
        return grouperRepo.save(new FinancialGrouper(code, name, parentId));
    }

    public List<FinancialGrouper> listGroupers(UUID parentId) {
        return parentId != null ? grouperRepo.findByParentIdAndActiveTrue(parentId) : grouperRepo.findByParentIdIsNullAndActiveTrue();
    }

    // Records
    public record PartnerShare(String name, BigDecimal sharePct) {}
}
