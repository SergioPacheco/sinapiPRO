package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.measurement.domain.Measurement;
import com.sinapipro.api.measurement.domain.MeasurementRepository;
import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sprint 15 — Relatórios Financeiros e Gerenciais.
 */
@Service
@Transactional(readOnly = true)
public class ManagerialReportsService {

    private final PayableRepository payableRepo;
    private final ReceivableRepository receivableRepo;
    private final ProjectRepository projectRepo;
    private final MeasurementRepository measurementRepo;

    public ManagerialReportsService(PayableRepository payableRepo, ReceivableRepository receivableRepo,
                                     ProjectRepository projectRepo, MeasurementRepository measurementRepo) {
        this.payableRepo = payableRepo; this.receivableRepo = receivableRepo;
        this.projectRepo = projectRepo; this.measurementRepo = measurementRepo;
    }

    /** 15.3 — Posição financeira do cliente (saldo devedor, inadimplência) */
    public ClientFinancialPosition clientPosition(UUID clientId) {
        var receivables = receivableRepo.findAll().stream()
                .filter(r -> clientId.equals(r.getProjectId())) // simplified: project linked to client
                .toList();
        var totalAmount = receivables.stream().map(Receivable::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalReceived = receivables.stream()
                .filter(r -> r.getStatus() == PaymentStatus.PAID)
                .map(r -> r.getReceivedAmount() != null ? r.getReceivedAmount() : r.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var overdue = receivables.stream()
                .filter(r -> r.getStatus() == PaymentStatus.PENDING && r.getDueDate().isBefore(java.time.LocalDate.now()))
                .map(Receivable::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ClientFinancialPosition(clientId, totalAmount, totalReceived, totalAmount.subtract(totalReceived), overdue);
    }

    /** 15.4 — Curva ABC de fornecedores (volume de compras) */
    public List<AbcItem> supplierAbcCurve(UUID projectId) {
        var payables = payableRepo.findAll().stream()
                .filter(p -> projectId == null || projectId.equals(p.getProjectId()))
                .filter(p -> p.getSupplierId() != null)
                .collect(Collectors.groupingBy(Payable::getSupplierId,
                        Collectors.reducing(BigDecimal.ZERO, Payable::getAmount, BigDecimal::add)));

        var total = payables.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.signum() == 0) return List.of();

        var sorted = payables.entrySet().stream()
                .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue().reversed())
                .toList();

        var result = new ArrayList<AbcItem>();
        BigDecimal cumulative = BigDecimal.ZERO;
        for (var entry : sorted) {
            cumulative = cumulative.add(entry.getValue());
            var pct = entry.getValue().multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
            var cumPct = cumulative.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
            var classification = cumPct.compareTo(BigDecimal.valueOf(80)) <= 0 ? "A"
                    : cumPct.compareTo(BigDecimal.valueOf(95)) <= 0 ? "B" : "C";
            result.add(new AbcItem(entry.getKey().toString(), entry.getValue(), pct, cumPct, classification));
        }
        return result;
    }

    /** 15.5 — Curva ABC de insumos (por categoria de despesa) */
    public List<AbcItem> expenseAbcCurve(UUID projectId) {
        var payables = payableRepo.findAll().stream()
                .filter(p -> projectId == null || projectId.equals(p.getProjectId()))
                .filter(p -> p.getCategory() != null)
                .collect(Collectors.groupingBy(Payable::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Payable::getAmount, BigDecimal::add)));

        var total = payables.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.signum() == 0) return List.of();

        var sorted = payables.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .toList();

        var result = new ArrayList<AbcItem>();
        BigDecimal cumulative = BigDecimal.ZERO;
        for (var entry : sorted) {
            cumulative = cumulative.add(entry.getValue());
            var pct = entry.getValue().multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
            var cumPct = cumulative.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
            var classification = cumPct.compareTo(BigDecimal.valueOf(80)) <= 0 ? "A"
                    : cumPct.compareTo(BigDecimal.valueOf(95)) <= 0 ? "B" : "C";
            result.add(new AbcItem(entry.getKey(), entry.getValue(), pct, cumPct, classification));
        }
        return result;
    }

    /** 15.6 — Relatório de medições consolidado por contrato */
    public List<MeasurementSummary> measurementsSummary(UUID budgetId) {
        return measurementRepo.findByBudgetIdOrderByNumberDesc(budgetId).stream()
                .map(m -> new MeasurementSummary(m.getId(), m.getNumber(), m.getStatus().name(),
                        m.getGrossAmount(), m.getCreatedAt()))
                .toList();
    }

    /** 15.7 — Relatório gerencial resumo (1 página por obra) */
    public ProjectDashboard projectDashboard(UUID projectId) {
        var project = projectRepo.findById(projectId).orElseThrow();
        var payables = payableRepo.findAll().stream().filter(p -> projectId.equals(p.getProjectId())).toList();
        var receivables = receivableRepo.findAll().stream().filter(r -> projectId.equals(r.getProjectId())).toList();

        var totalExpenses = payables.stream().map(Payable::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalPaid = payables.stream().filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(p -> p.getPaidAmount() != null ? p.getPaidAmount() : p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalRevenue = receivables.stream().map(Receivable::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalReceived = receivables.stream().filter(r -> r.getStatus() == PaymentStatus.PAID)
                .map(r -> r.getReceivedAmount() != null ? r.getReceivedAmount() : r.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProjectDashboard(projectId, project.getName(), project.getStatus().name(),
                project.getTotalBudget(), totalExpenses, totalPaid, totalRevenue, totalReceived,
                totalReceived.subtract(totalPaid));
    }

    /** 15.8 — Apropriação de custos por obra (rateio visualizado) */
    public List<ProjectCostAllocation> costAllocationByProject() {
        var grouped = payableRepo.findAll().stream()
                .filter(p -> p.getProjectId() != null)
                .collect(Collectors.groupingBy(Payable::getProjectId,
                        Collectors.reducing(BigDecimal.ZERO, Payable::getAmount, BigDecimal::add)));

        var total = grouped.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        var projectNames = projectRepo.findAllById(grouped.keySet()).stream()
                .collect(Collectors.toMap(Project::getId, Project::getName));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue().reversed())
                .map(e -> new ProjectCostAllocation(e.getKey(), projectNames.getOrDefault(e.getKey(), ""),
                        e.getValue(), total.signum() > 0 ? e.getValue().multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO))
                .toList();
    }

    // Records
    public record ClientFinancialPosition(UUID clientId, BigDecimal totalAmount, BigDecimal totalReceived,
                                           BigDecimal balance, BigDecimal overdueAmount) {}
    public record AbcItem(String identifier, BigDecimal amount, BigDecimal percentage, BigDecimal cumulativePercentage, String classification) {}
    public record MeasurementSummary(UUID id, int number, String status, BigDecimal totalAmount, java.time.Instant createdAt) {}
    public record ProjectDashboard(UUID projectId, String name, String status, BigDecimal budget,
                                    BigDecimal totalExpenses, BigDecimal totalPaid, BigDecimal totalRevenue,
                                    BigDecimal totalReceived, BigDecimal netCashFlow) {}
    public record ProjectCostAllocation(UUID projectId, String projectName, BigDecimal totalCost, BigDecimal percentage) {}
}
