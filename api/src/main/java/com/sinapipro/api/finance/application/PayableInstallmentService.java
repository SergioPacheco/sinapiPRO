package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PayableInstallmentService {

    private final PayableInstallmentRepository installmentRepository;
    private final PayableRepository payableRepository;

    public PayableInstallmentService(PayableInstallmentRepository installmentRepository,
                                     PayableRepository payableRepository) {
        this.installmentRepository = installmentRepository;
        this.payableRepository = payableRepository;
    }

    /**
     * Gera N parcelas iguais para um payable, distribuindo centavos na última.
     */
    public List<PayableInstallment> generateInstallments(UUID payableId, int numberOfInstallments,
                                                          LocalDate firstDueDate, int intervalDays) {
        var payable = payableRepository.findById(payableId)
                .orElseThrow(() -> new IllegalArgumentException("Payable not found: " + payableId));

        var existing = installmentRepository.findByPayableIdOrderByInstallmentNumber(payableId);
        if (!existing.isEmpty()) {
            throw new IllegalStateException("Installments already exist for payable: " + payableId);
        }

        var totalAmount = payable.getAmount();
        var installmentAmount = totalAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.FLOOR);
        var remainder = totalAmount.subtract(installmentAmount.multiply(BigDecimal.valueOf(numberOfInstallments)));

        var installments = new ArrayList<PayableInstallment>();
        for (int i = 1; i <= numberOfInstallments; i++) {
            var dueDate = firstDueDate.plusDays((long) (i - 1) * intervalDays);
            var amount = (i == numberOfInstallments) ? installmentAmount.add(remainder) : installmentAmount;
            installments.add(new PayableInstallment(payableId, i, dueDate, amount));
        }

        return installmentRepository.saveAll(installments);
    }

    public List<PayableInstallment> findByPayable(UUID payableId) {
        return installmentRepository.findByPayableIdOrderByInstallmentNumber(payableId);
    }

    /**
     * Marca parcelas vencidas como OVERDUE.
     */
    public int markOverdue() {
        var overdue = installmentRepository.findByStatusAndDueDateBefore(InstallmentStatus.OPEN, LocalDate.now());
        overdue.forEach(PayableInstallment::markOverdue);
        installmentRepository.saveAll(overdue);
        return overdue.size();
    }
}
