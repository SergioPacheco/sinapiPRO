package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 7.7 — DRE por obra (Demonstrativo de Resultado do Exercício).
 */
@Service
@Transactional(readOnly = true)
public class DreService {

    private final ReceivableRepository receivableRepository;
    private final PayableRepository payableRepository;

    public DreService(ReceivableRepository receivableRepository, PayableRepository payableRepository) {
        this.receivableRepository = receivableRepository;
        this.payableRepository = payableRepository;
    }

    public DreReport generate(UUID projectId, LocalDate startDate, LocalDate endDate) {
        var receivables = receivableRepository.findAll().stream()
                .filter(r -> projectId.equals(r.getProjectId()))
                .filter(r -> !r.getDueDate().isBefore(startDate) && !r.getDueDate().isAfter(endDate))
                .toList();

        var payables = payableRepository.findAll().stream()
                .filter(p -> projectId.equals(p.getProjectId()))
                .filter(p -> !p.getDueDate().isBefore(startDate) && !p.getDueDate().isAfter(endDate))
                .toList();

        // Receitas por categoria
        var revenueByCategory = receivables.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCategory() != null ? r.getCategory() : "Outros",
                        Collectors.reducing(BigDecimal.ZERO, Receivable::getAmount, BigDecimal::add)));

        // Despesas por categoria
        var expenseByCategory = payables.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() != null ? p.getCategory() : "Outros",
                        Collectors.reducing(BigDecimal.ZERO, Payable::getAmount, BigDecimal::add)));

        var totalRevenue = revenueByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalExpenses = expenseByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        var netResult = totalRevenue.subtract(totalExpenses);

        // Realizado (efetivamente pago/recebido)
        var realizedRevenue = receivables.stream()
                .filter(r -> r.getStatus() == PaymentStatus.PAID)
                .map(r -> r.getReceivedAmount() != null ? r.getReceivedAmount() : r.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var realizedExpenses = payables.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(p -> p.getPaidAmount() != null ? p.getPaidAmount() : p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var realizedResult = realizedRevenue.subtract(realizedExpenses);

        return new DreReport(projectId, startDate, endDate,
                toLineItems(revenueByCategory), totalRevenue,
                toLineItems(expenseByCategory), totalExpenses,
                netResult, realizedRevenue, realizedExpenses, realizedResult);
    }

    private List<DreLineItem> toLineItems(Map<String, BigDecimal> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(e -> new DreLineItem(e.getKey(), e.getValue()))
                .toList();
    }

    public record DreReport(UUID projectId, LocalDate startDate, LocalDate endDate,
                            List<DreLineItem> revenues, BigDecimal totalRevenue,
                            List<DreLineItem> expenses, BigDecimal totalExpenses,
                            BigDecimal netResult,
                            BigDecimal realizedRevenue, BigDecimal realizedExpenses, BigDecimal realizedResult) {}

    public record DreLineItem(String category, BigDecimal amount) {}
}
