package com.sinapipro.api.commercial.application;

import com.sinapipro.api.commercial.domain.*;
import com.sinapipro.api.finance.domain.MonetaryIndexValue;
import com.sinapipro.api.finance.domain.MonetaryIndexValueRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
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
public class SaleInstallmentService {

    private final SaleInstallmentRepository installmentRepository;
    private final SaleContractRepository contractRepository;

    public SaleInstallmentService(SaleInstallmentRepository installmentRepository,
                                   SaleContractRepository contractRepository) {
        this.installmentRepository = installmentRepository;
        this.contractRepository = contractRepository;
    }

    /**
     * Gera parcelas para um contrato de venda (Price ou SAC).
     */
    public List<SaleInstallment> generateInstallments(UUID contractId, LocalDate firstDueDate) {
        var contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new DomainNotFoundException("Contract not found: " + contractId));

        var existing = installmentRepository.findByContractIdOrderByInstallmentNumber(contractId);
        if (!existing.isEmpty()) throw new IllegalStateException("Installments already exist");

        var principal = contract.getFinancedAmount() != null ? contract.getFinancedAmount() : contract.getTotalAmount();
        var n = contract.getInstallmentCount();
        var rate = contract.getInterestRate() != null
                ? contract.getInterestRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        var installments = new ArrayList<SaleInstallment>();
        int startNumber = 1;

        // Entrada (down payment)
        if (contract.getDownPayment() != null && contract.getDownPayment().signum() > 0) {
            installments.add(new SaleInstallment(contractId, startNumber++, "DOWN_PAYMENT",
                    contract.getContractDate(), contract.getDownPayment()));
        }

        // Parcelas
        List<BigDecimal> amounts;
        if ("SAC".equals(contract.getAmortizationType())) {
            amounts = generateSAC(principal, n, rate);
        } else {
            amounts = generatePrice(principal, n, rate);
        }

        for (int i = 0; i < amounts.size(); i++) {
            var dueDate = firstDueDate.plusMonths(i);
            installments.add(new SaleInstallment(contractId, startNumber + i, "MONTHLY", dueDate, amounts.get(i)));
        }

        return installmentRepository.saveAll(installments);
    }

    /**
     * Reajusta parcelas futuras por índice acumulado.
     */
    public int adjustByIndex(UUID contractId, BigDecimal indexFactor) {
        var future = installmentRepository.findByContractIdAndStatus(contractId, "FUTURE");
        future.forEach(i -> i.adjust(indexFactor));
        installmentRepository.saveAll(future);
        return future.size();
    }

    /**
     * Recebe pagamento de uma parcela.
     */
    public SaleInstallment pay(UUID installmentId, BigDecimal amount, LocalDate date,
                                BigDecimal interest, BigDecimal fine, BigDecimal discount) {
        var installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new DomainNotFoundException("Installment not found: " + installmentId));
        installment.pay(amount, date != null ? date : LocalDate.now(), interest, fine, discount);
        return installmentRepository.save(installment);
    }

    public List<SaleInstallment> findByContract(UUID contractId) {
        return installmentRepository.findByContractIdOrderByInstallmentNumber(contractId);
    }

    private List<BigDecimal> generatePrice(BigDecimal principal, int n, BigDecimal rate) {
        if (rate.signum() == 0) {
            var each = principal.divide(BigDecimal.valueOf(n), 2, RoundingMode.FLOOR);
            var remainder = principal.subtract(each.multiply(BigDecimal.valueOf(n)));
            var list = new ArrayList<BigDecimal>();
            for (int i = 0; i < n; i++) list.add(i == n - 1 ? each.add(remainder) : each);
            return list;
        }
        var onePlusI = BigDecimal.ONE.add(rate);
        var power = onePlusI.pow(n, MathContext.DECIMAL128);
        var pmt = principal.multiply(rate.multiply(power)).divide(power.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        var list = new ArrayList<BigDecimal>();
        for (int i = 0; i < n; i++) list.add(pmt);
        return list;
    }

    private List<BigDecimal> generateSAC(BigDecimal principal, int n, BigDecimal rate) {
        var amort = principal.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        var list = new ArrayList<BigDecimal>();
        var balance = principal;
        for (int i = 0; i < n; i++) {
            var interestAmount = balance.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            list.add(i == n - 1 ? balance.add(interestAmount) : amort.add(interestAmount));
            balance = balance.subtract(amort);
        }
        return list;
    }
}
