package com.sinapipro.api.finance.api;

import com.sinapipro.api.finance.application.BankTransactionService;
import com.sinapipro.api.finance.application.BankTransactionService.BalanceSummary;
import com.sinapipro.api.finance.domain.BankTransaction;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Bank Transactions", description = "Movimentação bancária e conciliação")
@RestController
@RequestMapping("/api/v1/bank-accounts/{accountId}/transactions")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class BankTransactionController {

    private final BankTransactionService service;

    public BankTransactionController(BankTransactionService service) {
        this.service = service;
    }

    @Operation(summary = "List transactions for a bank account")
    @GetMapping
    PageResponse<TransactionResponse> list(@PathVariable UUID accountId,
                                            @PageableDefault(size = 30) Pageable pageable) {
        return PageResponse.from(service.listByAccount(accountId, pageable).map(TransactionResponse::from));
    }

    @Operation(summary = "Create a manual transaction")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    TransactionResponse create(@PathVariable UUID accountId,
                                @Valid @RequestBody CreateTransactionRequest req) {
        var tx = service.createTransaction(accountId, req.transactionDate(), req.type(),
                req.amount(), req.description());
        return TransactionResponse.from(tx);
    }

    @Operation(summary = "List unreconciled transactions")
    @GetMapping("/unreconciled")
    List<TransactionResponse> unreconciled(@PathVariable UUID accountId) {
        return service.getUnreconciled(accountId).stream().map(TransactionResponse::from).toList();
    }

    @Operation(summary = "Reconcile a transaction")
    @PostMapping("/{transactionId}/reconcile")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    TransactionResponse reconcile(@PathVariable UUID accountId, @PathVariable UUID transactionId) {
        return TransactionResponse.from(service.reconcile(transactionId));
    }

    @Operation(summary = "Batch reconcile transactions")
    @PostMapping("/reconcile-batch")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ReconcileResult reconcileBatch(@PathVariable UUID accountId,
                                    @RequestBody List<UUID> transactionIds) {
        return new ReconcileResult(service.reconcileBatch(transactionIds));
    }

    @Operation(summary = "Get balance summary for a period")
    @GetMapping("/balance")
    BalanceSummary balance(@PathVariable UUID accountId,
                           @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return service.getBalance(accountId, from, to);
    }

    // DTOs
    record CreateTransactionRequest(@NotNull LocalDate transactionDate, @NotBlank String type,
                                     @NotNull BigDecimal amount, @NotBlank String description) {}

    record TransactionResponse(UUID id, UUID bankAccountId, LocalDate transactionDate,
                                String type, BigDecimal amount, String description,
                                boolean reconciled, String referenceType, UUID referenceId) {
        static TransactionResponse from(BankTransaction t) {
            return new TransactionResponse(t.getId(), t.getBankAccountId(), t.getTransactionDate(),
                    t.getType(), t.getAmount(), t.getDescription(), t.isReconciled(),
                    t.getReferenceType(), t.getReferenceId());
        }
    }

    record ReconcileResult(int reconciled) {}
}
