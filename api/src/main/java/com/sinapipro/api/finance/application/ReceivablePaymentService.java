package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class ReceivablePaymentService {

    private final ReceivableInstallmentRepository installmentRepository;
    private final ReceivableRepository receivableRepository;
    private final BankTransactionRepository bankTransactionRepository;

    public ReceivablePaymentService(ReceivableInstallmentRepository installmentRepository,
                                     ReceivableRepository receivableRepository,
                                     BankTransactionRepository bankTransactionRepository) {
        this.installmentRepository = installmentRepository;
        this.receivableRepository = receivableRepository;
        this.bankTransactionRepository = bankTransactionRepository;
    }

    /**
     * Baixa de parcela com juros/multa/desconto. Gera movimentação bancária CREDIT.
     */
    public ReceivableInstallment receive(UUID installmentId, BigDecimal receivedAmount,
                                          LocalDate receivedDate, UUID bankAccountId,
                                          BigDecimal interest, BigDecimal fine, BigDecimal discount) {
        var installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new IllegalArgumentException("Installment not found: " + installmentId));

        if (installment.getStatus() == InstallmentStatus.PAID) {
            throw new IllegalStateException("Installment already paid");
        }

        installment.receive(receivedAmount, receivedDate != null ? receivedDate : LocalDate.now(),
                interest, fine, discount);
        installmentRepository.save(installment);

        // Gerar movimentação bancária
        if (bankAccountId != null) {
            var tx = new BankTransaction(bankAccountId,
                    receivedDate != null ? receivedDate : LocalDate.now(),
                    "CREDIT", receivedAmount,
                    "Recebimento parcela " + installment.getInstallmentNumber());
            tx.setReference("RECEIVABLE_INSTALLMENT", installmentId);
            bankTransactionRepository.save(tx);
        }

        // Verificar se todas as parcelas foram recebidas
        checkReceivableCompletion(installment.getReceivableId());

        return installment;
    }

    private void checkReceivableCompletion(UUID receivableId) {
        var installments = installmentRepository.findByReceivableIdOrderByInstallmentNumber(receivableId);
        var allPaid = installments.stream().allMatch(i -> i.getStatus() == InstallmentStatus.PAID);
        if (allPaid) {
            receivableRepository.findById(receivableId).ifPresent(r -> {
                var totalReceived = installments.stream()
                        .map(ReceivableInstallment::getReceivedAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                r.receive(totalReceived, LocalDate.now());
                receivableRepository.save(r);
            });
        }
    }
}
