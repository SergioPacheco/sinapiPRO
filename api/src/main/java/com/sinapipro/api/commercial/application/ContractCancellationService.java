package com.sinapipro.api.commercial.application;

import com.sinapipro.api.commercial.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class ContractCancellationService {

    private final SaleContractRepository contractRepository;
    private final SaleInstallmentRepository installmentRepository;

    public ContractCancellationService(SaleContractRepository contractRepository,
                                        SaleInstallmentRepository installmentRepository) {
        this.contractRepository = contractRepository;
        this.installmentRepository = installmentRepository;
    }

    /**
     * Distrato: cancela contrato, calcula multa, retorna valor a devolver.
     */
    public CancellationResult cancel(UUID contractId, String reason, BigDecimal finePct) {
        var contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new DomainNotFoundException("Contract not found: " + contractId));

        // Calcular total pago
        var installments = installmentRepository.findByContractIdOrderByInstallmentNumber(contractId);
        var totalPaid = installments.stream()
                .map(SaleInstallment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular multa
        var fineAmount = totalPaid.multiply(finePct != null ? finePct : BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        var refundAmount = totalPaid.subtract(fineAmount);

        // Cancelar parcelas futuras
        installments.stream()
                .filter(i -> "FUTURE".equals(i.getStatus()) || "OVERDUE".equals(i.getStatus()))
                .forEach(SaleInstallment::cancel);
        installmentRepository.saveAll(installments);

        // Cancelar contrato
        contract.cancel(LocalDate.now(), reason, finePct);
        contractRepository.save(contract);

        return new CancellationResult(totalPaid, fineAmount, refundAmount);
    }

    public record CancellationResult(BigDecimal totalPaid, BigDecimal fineAmount, BigDecimal refundAmount) {}
}
