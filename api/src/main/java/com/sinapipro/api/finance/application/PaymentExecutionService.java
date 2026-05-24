package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class PaymentExecutionService {

    private final PayableInstallmentRepository installmentRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final PayableRepository payableRepository;

    public PaymentExecutionService(PayableInstallmentRepository installmentRepository,
                                   BankTransactionRepository bankTransactionRepository,
                                   PayableRepository payableRepository) {
        this.installmentRepository = installmentRepository;
        this.bankTransactionRepository = bankTransactionRepository;
        this.payableRepository = payableRepository;
    }

    /**
     * Efetua pagamento de uma parcela, gerando movimentação bancária.
     */
    public PaymentResult executePayment(UUID installmentId, UUID bankAccountId,
                                         String paymentMethod, LocalDate paymentDate,
                                         BigDecimal discount, BigDecimal interest, BigDecimal fine) {
        var installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new IllegalArgumentException("Installment not found: " + installmentId));

        if (installment.getStatus() == InstallmentStatus.PAID) {
            throw new IllegalStateException("Installment already paid");
        }

        // Aplicar encargos
        installment.applyCharges(
                interest != null ? interest : BigDecimal.ZERO,
                fine != null ? fine : BigDecimal.ZERO,
                discount != null ? discount : BigDecimal.ZERO
        );

        var netAmount = installment.getNetAmount();
        installment.pay(netAmount, paymentDate, paymentMethod, bankAccountId);
        installmentRepository.save(installment);

        // Gerar movimentação bancária
        var transaction = new BankTransaction(bankAccountId, paymentDate, "DEBIT",
                netAmount, "Pgto parcela " + installment.getInstallmentNumber() + " - " + installment.getPayableId());
        transaction.setReference("PAYABLE_INSTALLMENT", installmentId);
        bankTransactionRepository.save(transaction);

        // Verificar se todas as parcelas do payable foram pagas
        checkPayableCompletion(installment.getPayableId());

        return new PaymentResult(installment, transaction);
    }

    private void checkPayableCompletion(UUID payableId) {
        var installments = installmentRepository.findByPayableIdOrderByInstallmentNumber(payableId);
        var allPaid = installments.stream().allMatch(i -> i.getStatus() == InstallmentStatus.PAID);
        if (allPaid) {
            payableRepository.findById(payableId).ifPresent(p -> {
                var totalPaid = installments.stream()
                        .map(PayableInstallment::getPaidAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                p.pay(totalPaid, LocalDate.now());
                payableRepository.save(p);
            });
        }
    }

    public record PaymentResult(PayableInstallment installment, BankTransaction transaction) {}
}
