package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 7.8 — Aging report (inadimplência por faixa de atraso).
 */
@Service
@Transactional(readOnly = true)
public class FinanceAgingService {

    private final PayableRepository payableRepository;
    private final ReceivableRepository receivableRepository;

    public FinanceAgingService(PayableRepository payableRepository, ReceivableRepository receivableRepository) {
        this.payableRepository = payableRepository;
        this.receivableRepository = receivableRepository;
    }

    public AgingReport payablesAging(UUID projectId) {
        var today = LocalDate.now();
        var overdue = payableRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .filter(p -> projectId == null || projectId.equals(p.getProjectId()))
                .filter(p -> p.getDueDate().isBefore(today))
                .toList();

        return buildReport("PAYABLE", overdue.stream()
                .map(p -> new OverdueItem(p.getId(), p.getDescription(), p.getAmount(),
                        p.getDueDate(), (int) ChronoUnit.DAYS.between(p.getDueDate(), today), p.getSupplierId(), p.getProjectId()))
                .toList());
    }

    public AgingReport receivablesAging(UUID projectId) {
        var today = LocalDate.now();
        var overdue = receivableRepository.findAll().stream()
                .filter(r -> r.getStatus() == PaymentStatus.PENDING)
                .filter(r -> projectId == null || projectId.equals(r.getProjectId()))
                .filter(r -> r.getDueDate().isBefore(today))
                .toList();

        return buildReport("RECEIVABLE", overdue.stream()
                .map(r -> new OverdueItem(r.getId(), r.getDescription(), r.getAmount(),
                        r.getDueDate(), (int) ChronoUnit.DAYS.between(r.getDueDate(), today), null, r.getProjectId()))
                .toList());
    }

    private AgingReport buildReport(String type, List<OverdueItem> items) {
        var bucket1to30 = items.stream().filter(i -> i.daysOverdue() <= 30).toList();
        var bucket31to60 = items.stream().filter(i -> i.daysOverdue() > 30 && i.daysOverdue() <= 60).toList();
        var bucket61to90 = items.stream().filter(i -> i.daysOverdue() > 60 && i.daysOverdue() <= 90).toList();
        var bucket90plus = items.stream().filter(i -> i.daysOverdue() > 90).toList();

        return new AgingReport(type,
                new AgingBucket("1-30 dias", sumAmounts(bucket1to30), bucket1to30.size()),
                new AgingBucket("31-60 dias", sumAmounts(bucket31to60), bucket31to60.size()),
                new AgingBucket("61-90 dias", sumAmounts(bucket61to90), bucket61to90.size()),
                new AgingBucket("90+ dias", sumAmounts(bucket90plus), bucket90plus.size()),
                sumAmounts(items), items.size(), items);
    }

    private BigDecimal sumAmounts(List<OverdueItem> items) {
        return items.stream().map(OverdueItem::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public record AgingReport(String type, AgingBucket bucket1to30, AgingBucket bucket31to60,
                              AgingBucket bucket61to90, AgingBucket bucket90plus,
                              BigDecimal totalOverdue, int totalCount, List<OverdueItem> items) {}

    public record AgingBucket(String label, BigDecimal amount, int count) {}

    public record OverdueItem(UUID id, String description, BigDecimal amount, LocalDate dueDate,
                              int daysOverdue, UUID supplierId, UUID projectId) {}
}
