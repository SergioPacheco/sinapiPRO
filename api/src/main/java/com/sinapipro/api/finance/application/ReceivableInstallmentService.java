package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReceivableInstallmentService {

    private final ReceivableInstallmentRepository installmentRepository;
    private final ReceivableRepository receivableRepository;

    public ReceivableInstallmentService(ReceivableInstallmentRepository installmentRepository,
                                         ReceivableRepository receivableRepository) {
        this.installmentRepository = installmentRepository;
        this.receivableRepository = receivableRepository;
    }

    /**
     * Gera parcelas com amortização Price (parcelas fixas).
     */
    public List<ReceivableInstallment> generatePrice(UUID receivableId, int numberOfInstallments,
                                                      LocalDate firstDueDate, BigDecimal monthlyRate) {
        var receivable = findReceivable(receivableId);
        assertNoExisting(receivableId);

        var principal = receivable.getAmount();
        List<BigDecimal> amounts;

        if (monthlyRate == null || monthlyRate.signum() == 0) {
            // Sem juros: parcelas iguais
            amounts = equalInstallments(principal, numberOfInstallments);
        } else {
            // Price: PMT = PV * [i(1+i)^n] / [(1+i)^n - 1]
            var i = monthlyRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            var onePlusI = BigDecimal.ONE.add(i);
            var power = onePlusI.pow(numberOfInstallments, MathContext.DECIMAL128);
            var pmt = principal.multiply(i.multiply(power))
                    .divide(power.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
            amounts = new ArrayList<>();
            for (int j = 0; j < numberOfInstallments; j++) amounts.add(pmt);
            // Ajustar centavos na última
            var total = pmt.multiply(BigDecimal.valueOf(numberOfInstallments));
            var diff = principal.subtract(total.subtract(pmt.multiply(BigDecimal.valueOf(numberOfInstallments - 1))));
            // Recalcular: total das parcelas deve cobrir principal + juros totais
        }

        var installments = new ArrayList<ReceivableInstallment>();
        var installmentAmounts = (monthlyRate == null || monthlyRate.signum() == 0)
                ? equalInstallments(principal, numberOfInstallments)
                : priceInstallments(principal, numberOfInstallments, monthlyRate);

        for (int j = 0; j < numberOfInstallments; j++) {
            var dueDate = firstDueDate.plusMonths(j);
            installments.add(new ReceivableInstallment(receivableId, j + 1, dueDate, installmentAmounts.get(j)));
        }

        return installmentRepository.saveAll(installments);
    }

    /**
     * Gera parcelas com amortização SAC (amortização constante, parcelas decrescentes).
     */
    public List<ReceivableInstallment> generateSAC(UUID receivableId, int numberOfInstallments,
                                                    LocalDate firstDueDate, BigDecimal monthlyRate) {
        var receivable = findReceivable(receivableId);
        assertNoExisting(receivableId);

        var principal = receivable.getAmount();
        var amortization = principal.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
        var rate = (monthlyRate != null) ? monthlyRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        var installments = new ArrayList<ReceivableInstallment>();
        var balance = principal;

        for (int j = 0; j < numberOfInstallments; j++) {
            var interestAmount = balance.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            var installmentAmount = amortization.add(interestAmount);
            // Última parcela: ajustar para zerar saldo
            if (j == numberOfInstallments - 1) {
                installmentAmount = balance.add(interestAmount);
            }
            var dueDate = firstDueDate.plusMonths(j);
            installments.add(new ReceivableInstallment(receivableId, j + 1, dueDate, installmentAmount));
            balance = balance.subtract(amortization);
        }

        return installmentRepository.saveAll(installments);
    }

    public List<ReceivableInstallment> findByReceivable(UUID receivableId) {
        return installmentRepository.findByReceivableIdOrderByInstallmentNumber(receivableId);
    }

    private List<BigDecimal> equalInstallments(BigDecimal total, int n) {
        var each = total.divide(BigDecimal.valueOf(n), 2, RoundingMode.FLOOR);
        var remainder = total.subtract(each.multiply(BigDecimal.valueOf(n)));
        var list = new ArrayList<BigDecimal>();
        for (int i = 0; i < n; i++) {
            list.add(i == n - 1 ? each.add(remainder) : each);
        }
        return list;
    }

    private List<BigDecimal> priceInstallments(BigDecimal principal, int n, BigDecimal monthlyRatePct) {
        var i = monthlyRatePct.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        var onePlusI = BigDecimal.ONE.add(i);
        var power = onePlusI.pow(n, MathContext.DECIMAL128);
        var pmt = principal.multiply(i.multiply(power))
                .divide(power.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        var list = new ArrayList<BigDecimal>();
        for (int j = 0; j < n; j++) list.add(pmt);
        return list;
    }

    private Receivable findReceivable(UUID id) {
        return receivableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receivable not found: " + id));
    }

    private void assertNoExisting(UUID receivableId) {
        if (!installmentRepository.findByReceivableIdOrderByInstallmentNumber(receivableId).isEmpty()) {
            throw new IllegalStateException("Installments already exist for receivable: " + receivableId);
        }
    }
}
