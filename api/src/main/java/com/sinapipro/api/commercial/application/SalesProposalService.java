package com.sinapipro.api.commercial.application;

import com.sinapipro.api.commercial.domain.*;
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
public class SalesProposalService {

    private final SaleContractRepository contractRepository;
    private final SaleInstallmentRepository installmentRepository;

    public SalesProposalService(SaleContractRepository contractRepository,
                                 SaleInstallmentRepository installmentRepository) {
        this.contractRepository = contractRepository;
        this.installmentRepository = installmentRepository;
    }

    // ═══════════════════════════════════════════════════════════
    // 9.2 — Simulação de parcelas na proposta
    // ═══════════════════════════════════════════════════════════

    /**
     * Simula parcelas sem persistir — para proposta comercial.
     */
    public InstallmentSimulation simulate(BigDecimal totalAmount, BigDecimal downPayment,
                                           int installmentCount, BigDecimal monthlyRate,
                                           String amortizationType, LocalDate firstDueDate) {
        var principal = totalAmount.subtract(downPayment != null ? downPayment : BigDecimal.ZERO);
        var rate = monthlyRate != null ? monthlyRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        List<SimulatedInstallment> installments = new ArrayList<>();

        if (downPayment != null && downPayment.signum() > 0) {
            installments.add(new SimulatedInstallment(0, "ENTRADA", firstDueDate.minusMonths(1), downPayment, BigDecimal.ZERO, downPayment));
        }

        if ("SAC".equalsIgnoreCase(amortizationType)) {
            var amort = principal.divide(BigDecimal.valueOf(installmentCount), 2, RoundingMode.HALF_UP);
            var balance = principal;
            for (int i = 0; i < installmentCount; i++) {
                var interest = balance.multiply(rate).setScale(2, RoundingMode.HALF_UP);
                var total = (i == installmentCount - 1) ? balance.add(interest) : amort.add(interest);
                installments.add(new SimulatedInstallment(i + 1, "MENSAL", firstDueDate.plusMonths(i), total, interest, amort));
                balance = balance.subtract(amort);
            }
        } else {
            // PRICE
            BigDecimal pmt;
            if (rate.signum() == 0) {
                pmt = principal.divide(BigDecimal.valueOf(installmentCount), 2, RoundingMode.HALF_UP);
            } else {
                var onePlusI = BigDecimal.ONE.add(rate);
                var power = onePlusI.pow(installmentCount, MathContext.DECIMAL128);
                pmt = principal.multiply(rate.multiply(power)).divide(power.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
            }
            var balance = principal;
            for (int i = 0; i < installmentCount; i++) {
                var interest = balance.multiply(rate).setScale(2, RoundingMode.HALF_UP);
                var amort = pmt.subtract(interest);
                installments.add(new SimulatedInstallment(i + 1, "MENSAL", firstDueDate.plusMonths(i), pmt, interest, amort));
                balance = balance.subtract(amort);
            }
        }

        var totalInterest = installments.stream().map(SimulatedInstallment::interest).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalPaid = installments.stream().map(SimulatedInstallment::amount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InstallmentSimulation(totalAmount, downPayment, principal, installmentCount,
                monthlyRate, amortizationType, totalInterest, totalPaid, installments);
    }

    // ═══════════════════════════════════════════════════════════
    // 9.6 — Cessão/transferência de contrato
    // ═══════════════════════════════════════════════════════════

    /**
     * Transfere contrato para novo comprador: cria novo contrato com saldo devedor.
     */
    public TransferResult transfer(UUID originalContractId, String newContractNumber,
                                    UUID newBuyerId, LocalDate transferDate) {
        var original = contractRepository.findById(originalContractId)
                .orElseThrow(() -> new DomainNotFoundException("Contract not found: " + originalContractId));

        // Calcular saldo devedor
        var installments = installmentRepository.findByContractIdOrderByInstallmentNumber(originalContractId);
        var totalPaid = installments.stream().map(SaleInstallment::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var remainingBalance = original.getTotalAmount().subtract(totalPaid);

        // Criar novo contrato com saldo devedor
        var remainingInstallments = (int) installments.stream().filter(i -> "FUTURE".equals(i.getStatus())).count();
        var newContract = new SaleContract(original.getDevelopmentId(), newContractNumber,
                transferDate, remainingBalance, Math.max(remainingInstallments, 1), original.getAmortizationType());
        if (original.getIndexId() != null) newContract.setIndexId(original.getIndexId());
        if (original.getInterestRate() != null) newContract.setInterestRate(original.getInterestRate());
        newContract.sign(transferDate);
        newContract.activate();
        newContract = contractRepository.save(newContract);

        // Cancelar parcelas futuras do contrato original
        installments.stream()
                .filter(i -> "FUTURE".equals(i.getStatus()))
                .forEach(SaleInstallment::cancel);
        installmentRepository.saveAll(installments);

        // Marcar original como transferido
        original.transfer(newContract.getId(), transferDate);
        contractRepository.save(original);

        return new TransferResult(original.getId(), newContract.getId(), remainingBalance, totalPaid);
    }

    // ═══════════════════════════════════════════════════════════
    // 9.8 — Repasse bancário
    // ═══════════════════════════════════════════════════════════

    /**
     * Registra repasse bancário (financiamento do comprador junto ao banco).
     */
    public BankHandover registerBankHandover(UUID contractId, String bankName, BigDecimal financedAmount,
                                              LocalDate submissionDate, String status, String notes) {
        var contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new DomainNotFoundException("Contract not found: " + contractId));
        // Atualizar financed amount no contrato
        contract.setFinancedAmount(financedAmount);
        contractRepository.save(contract);
        return new BankHandover(contractId, bankName, financedAmount, submissionDate, status, notes);
    }

    // Records
    public record InstallmentSimulation(BigDecimal totalAmount, BigDecimal downPayment, BigDecimal principal,
                                         int installmentCount, BigDecimal monthlyRate, String amortizationType,
                                         BigDecimal totalInterest, BigDecimal totalPaid,
                                         List<SimulatedInstallment> installments) {}

    public record SimulatedInstallment(int number, String type, LocalDate dueDate,
                                        BigDecimal amount, BigDecimal interest, BigDecimal amortization) {}

    public record TransferResult(UUID originalContractId, UUID newContractId,
                                  BigDecimal remainingBalance, BigDecimal totalPaid) {}

    public record BankHandover(UUID contractId, String bankName, BigDecimal financedAmount,
                                LocalDate submissionDate, String status, String notes) {}
}
