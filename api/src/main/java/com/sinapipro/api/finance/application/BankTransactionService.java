package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BankTransactionService {

    private final BankTransactionRepository transactionRepository;

    public BankTransactionService(BankTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public BankTransaction createTransaction(UUID bankAccountId, LocalDate date, String type,
                                              BigDecimal amount, String description) {
        var tx = new BankTransaction(bankAccountId, date, type, amount, description);
        return transactionRepository.save(tx);
    }

    public Page<BankTransaction> listByAccount(UUID bankAccountId, Pageable pageable) {
        return transactionRepository.findByBankAccountIdOrderByTransactionDateDesc(bankAccountId, pageable);
    }

    public List<BankTransaction> getUnreconciled(UUID bankAccountId) {
        return transactionRepository.findByBankAccountIdAndReconciledFalse(bankAccountId);
    }

    /**
     * Concilia uma transação (match com extrato bancário).
     */
    public BankTransaction reconcile(UUID transactionId) {
        var tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
        tx.reconcile();
        return transactionRepository.save(tx);
    }

    /**
     * Concilia múltiplas transações de uma vez.
     */
    public int reconcileBatch(List<UUID> transactionIds) {
        var transactions = transactionRepository.findAllById(transactionIds);
        transactions.forEach(BankTransaction::reconcile);
        transactionRepository.saveAll(transactions);
        return transactions.size();
    }

    /**
     * Calcula saldo do período para uma conta.
     */
    public BalanceSummary getBalance(UUID bankAccountId, LocalDate from, LocalDate to) {
        var transactions = transactionRepository.findByBankAccountIdAndTransactionDateBetween(bankAccountId, from, to);
        var credits = transactions.stream()
                .filter(t -> "CREDIT".equals(t.getType()))
                .map(BankTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var debits = transactions.stream()
                .filter(t -> "DEBIT".equals(t.getType()))
                .map(BankTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new BalanceSummary(credits, debits, credits.subtract(debits), transactions.size());
    }

    public record BalanceSummary(BigDecimal credits, BigDecimal debits, BigDecimal net, int transactionCount) {}
}
