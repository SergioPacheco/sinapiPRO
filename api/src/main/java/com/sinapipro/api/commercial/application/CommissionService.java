package com.sinapipro.api.commercial.application;

import com.sinapipro.api.commercial.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CommissionService {

    private final SaleContractRepository contractRepository;
    private final SaleInstallmentRepository installmentRepository;

    public CommissionService(SaleContractRepository contractRepository,
                              SaleInstallmentRepository installmentRepository) {
        this.contractRepository = contractRepository;
        this.installmentRepository = installmentRepository;
    }

    /**
     * Calcula comissão de um contrato: total, pago proporcional, saldo.
     */
    public CommissionSummary calculate(UUID contractId) {
        var contract = contractRepository.findById(contractId).orElseThrow();
        var commissionTotal = contract.getCommissionAmount() != null ? contract.getCommissionAmount() : BigDecimal.ZERO;

        if (commissionTotal.signum() == 0) {
            return new CommissionSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        var installments = installmentRepository.findByContractIdOrderByInstallmentNumber(contractId);
        var totalContract = contract.getTotalAmount();
        var totalPaid = installments.stream()
                .map(SaleInstallment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Comissão proporcional ao recebido
        var paidPct = totalPaid.divide(totalContract, 6, RoundingMode.HALF_UP);
        var commissionEarned = commissionTotal.multiply(paidPct).setScale(2, RoundingMode.HALF_UP);
        var commissionPending = commissionTotal.subtract(commissionEarned);

        return new CommissionSummary(commissionTotal, commissionEarned, commissionPending, contract.getCommissionRate());
    }

    /**
     * Lista comissões de todos os contratos de um empreendimento.
     */
    public List<CommissionSummary> listByDevelopment(UUID developmentId) {
        return contractRepository.findByDevelopmentIdAndStatus(developmentId, "ACTIVE").stream()
                .filter(c -> c.getBrokerId() != null)
                .map(c -> calculate(c.getId()))
                .toList();
    }

    public record CommissionSummary(BigDecimal total, BigDecimal earned, BigDecimal pending, BigDecimal rate) {}
}
