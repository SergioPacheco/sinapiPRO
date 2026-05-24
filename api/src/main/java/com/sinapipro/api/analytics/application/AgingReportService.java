package com.sinapipro.api.analytics.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AgingReportService {

    private final PayableInstallmentRepository payableInstallmentRepository;
    private final ReceivableInstallmentRepository receivableInstallmentRepository;

    public AgingReportService(PayableInstallmentRepository payableInstallmentRepository,
                               ReceivableInstallmentRepository receivableInstallmentRepository) {
        this.payableInstallmentRepository = payableInstallmentRepository;
        this.receivableInstallmentRepository = receivableInstallmentRepository;
    }

    /**
     * Aging report de contas a receber: inadimplência por faixa de atraso.
     */
    public AgingReport receivableAging() {
        var today = LocalDate.now();
        var overdue = receivableInstallmentRepository.findByStatusAndDueDateBefore(InstallmentStatus.OPEN, today);

        var current = BigDecimal.ZERO;
        var days30 = BigDecimal.ZERO;
        var days60 = BigDecimal.ZERO;
        var days90 = BigDecimal.ZERO;
        var days120plus = BigDecimal.ZERO;

        for (var i : overdue) {
            var daysLate = ChronoUnit.DAYS.between(i.getDueDate(), today);
            var amount = i.getAmount();
            if (daysLate <= 30) days30 = days30.add(amount);
            else if (daysLate <= 60) days60 = days60.add(amount);
            else if (daysLate <= 90) days90 = days90.add(amount);
            else days120plus = days120plus.add(amount);
        }

        var total = days30.add(days60).add(days90).add(days120plus);
        return new AgingReport("RECEIVABLE", total, days30, days60, days90, days120plus, overdue.size());
    }

    /**
     * Aging report de contas a pagar.
     */
    public AgingReport payableAging() {
        var today = LocalDate.now();
        var overdue = payableInstallmentRepository.findByStatusAndDueDateBefore(InstallmentStatus.OPEN, today);

        var days30 = BigDecimal.ZERO;
        var days60 = BigDecimal.ZERO;
        var days90 = BigDecimal.ZERO;
        var days120plus = BigDecimal.ZERO;

        for (var i : overdue) {
            var daysLate = ChronoUnit.DAYS.between(i.getDueDate(), today);
            var amount = i.getAmount();
            if (daysLate <= 30) days30 = days30.add(amount);
            else if (daysLate <= 60) days60 = days60.add(amount);
            else if (daysLate <= 90) days90 = days90.add(amount);
            else days120plus = days120plus.add(amount);
        }

        var total = days30.add(days60).add(days90).add(days120plus);
        return new AgingReport("PAYABLE", total, days30, days60, days90, days120plus, overdue.size());
    }

    public record AgingReport(String type, BigDecimal total, BigDecimal days1to30,
                               BigDecimal days31to60, BigDecimal days61to90,
                               BigDecimal days120plus, int count) {}
}
