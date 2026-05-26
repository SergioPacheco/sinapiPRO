package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 7.4 — Rateio de custos entre obras.
 * Distribui um payable entre múltiplos projetos conforme percentuais configurados.
 */
@Service
@Transactional
public class CostApportionmentService {

    private final PayableRepository payableRepository;
    private final ProjectRepository projectRepository;

    public CostApportionmentService(PayableRepository payableRepository, ProjectRepository projectRepository) {
        this.payableRepository = payableRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Rateia um custo entre projetos. Cria um payable para cada projeto com o valor proporcional.
     * @param description descrição do custo
     * @param totalAmount valor total a ratear
     * @param dueDate vencimento
     * @param category categoria financeira
     * @param supplierId fornecedor (opcional)
     * @param distribution mapa projectId → percentual (soma deve ser 100)
     */
    public List<Payable> apportion(String description, BigDecimal totalAmount, LocalDate dueDate,
                                   String category, UUID supplierId, Map<UUID, BigDecimal> distribution) {
        var totalPct = distribution.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPct.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException("Distribution percentages must sum to 100, got: " + totalPct);
        }

        var entries = distribution.entrySet().stream().toList();
        BigDecimal allocated = BigDecimal.ZERO;
        var payables = new java.util.ArrayList<Payable>();

        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            BigDecimal amount;
            if (i == entries.size() - 1) {
                amount = totalAmount.subtract(allocated); // last gets remainder
            } else {
                amount = totalAmount.multiply(entry.getValue())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                allocated = allocated.add(amount);
            }

            var project = projectRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + entry.getKey()));

            var payable = new Payable(project.getId(), supplierId,
                    description + " (rateio " + entry.getValue() + "% - " + project.getName() + ")",
                    amount, dueDate, category);
            payable.setProjectId(entry.getKey());
            payables.add(payable);
        }

        return payableRepository.saveAll(payables);
    }

    /**
     * Rateia usando os percentuais configurados nos projetos (apportionment_rate).
     */
    public List<Payable> apportionByProjectRates(String description, BigDecimal totalAmount,
                                                  LocalDate dueDate, String category, UUID supplierId,
                                                  List<UUID> projectIds) {
        var projects = projectRepository.findAllById(projectIds);
        var totalRate = projects.stream()
                .map(Project::getApportionmentRate)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRate.signum() == 0) {
            throw new IllegalArgumentException("No apportionment rates configured for the given projects");
        }

        Map<UUID, BigDecimal> distribution = new java.util.LinkedHashMap<>();
        for (var project : projects) {
            var rate = project.getApportionmentRate();
            if (rate != null && rate.signum() > 0) {
                var pct = rate.multiply(new BigDecimal("100")).divide(totalRate, 2, RoundingMode.HALF_UP);
                distribution.put(project.getId(), pct);
            }
        }

        // Normalize to exactly 100
        var sum = distribution.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.compareTo(new BigDecimal("100")) != 0 && !distribution.isEmpty()) {
            var lastKey = distribution.keySet().stream().reduce((a, b) -> b).orElseThrow();
            distribution.put(lastKey, distribution.get(lastKey).add(new BigDecimal("100").subtract(sum)));
        }

        return apportion(description, totalAmount, dueDate, category, supplierId, distribution);
    }
}
