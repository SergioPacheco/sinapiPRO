package com.sinapipro.api.finance.api;

import com.sinapipro.api.finance.application.*;
import com.sinapipro.api.finance.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Payable Installments", description = "Parcelamento e pagamento de contas a pagar")
@RestController
@RequestMapping("/api/v1/payables")
@PreAuthorize("@perm.check('finance.read')")
public class PayableInstallmentController {

    private final PayableInstallmentService installmentService;
    private final PaymentExecutionService paymentService;
    private final TaxRetentionService retentionService;

    public PayableInstallmentController(PayableInstallmentService installmentService,
                                         PaymentExecutionService paymentService,
                                         TaxRetentionService retentionService) {
        this.installmentService = installmentService;
        this.paymentService = paymentService;
        this.retentionService = retentionService;
    }

    @Operation(summary = "Generate installments for a payable")
    @PostMapping("/{payableId}/installments")
    @PreAuthorize("@perm.check('finance.write')")
    @ResponseStatus(HttpStatus.CREATED)
    List<InstallmentResponse> generateInstallments(@PathVariable UUID payableId,
                                                    @Valid @RequestBody GenerateInstallmentsRequest req) {
        return installmentService.generateInstallments(payableId, req.numberOfInstallments(),
                req.firstDueDate(), req.intervalDays() != null ? req.intervalDays() : 30)
                .stream().map(InstallmentResponse::from).toList();
    }

    @Operation(summary = "List installments of a payable")
    @GetMapping("/{payableId}/installments")
    List<InstallmentResponse> listInstallments(@PathVariable UUID payableId) {
        return installmentService.findByPayable(payableId).stream().map(InstallmentResponse::from).toList();
    }

    @Operation(summary = "Execute payment of an installment")
    @PostMapping("/installments/{installmentId}/pay")
    @PreAuthorize("@perm.check('finance.write')")
    PaymentResponse pay(@PathVariable UUID installmentId, @Valid @RequestBody PayInstallmentRequest req) {
        var result = paymentService.executePayment(installmentId, req.bankAccountId(),
                req.paymentMethod(), req.paymentDate() != null ? req.paymentDate() : LocalDate.now(),
                req.discount(), req.interest(), req.fine());
        return new PaymentResponse(InstallmentResponse.from(result.installment()),
                result.transaction().getId(), result.transaction().getAmount());
    }

    @Operation(summary = "Calculate tax retentions for a payable")
    @PostMapping("/{payableId}/retentions")
    @PreAuthorize("@perm.check('finance.write')")
    @ResponseStatus(HttpStatus.CREATED)
    List<RetentionResponse> calculateRetentions(@PathVariable UUID payableId,
                                                 @Valid @RequestBody CalculateRetentionsRequest req) {
        return retentionService.calculateRetentions(payableId, req.taxes())
                .stream().map(RetentionResponse::from).toList();
    }

    @Operation(summary = "List retentions of a payable")
    @GetMapping("/{payableId}/retentions")
    List<RetentionResponse> listRetentions(@PathVariable UUID payableId) {
        return retentionService.findByPayable(payableId).stream().map(RetentionResponse::from).toList();
    }

    @Operation(summary = "Get net amount (total - retentions)")
    @GetMapping("/{payableId}/net-amount")
    NetAmountResponse getNetAmount(@PathVariable UUID payableId) {
        return new NetAmountResponse(retentionService.getNetAmount(payableId));
    }

    // DTOs
    record GenerateInstallmentsRequest(@Positive @NotNull Integer numberOfInstallments,
                                        @NotNull LocalDate firstDueDate, Integer intervalDays) {}

    record PayInstallmentRequest(@NotNull UUID bankAccountId, @NotNull String paymentMethod,
                                  LocalDate paymentDate, BigDecimal discount,
                                  BigDecimal interest, BigDecimal fine) {}

    record CalculateRetentionsRequest(@NotNull List<TaxType> taxes) {}

    record InstallmentResponse(UUID id, UUID payableId, int number, LocalDate dueDate,
                                BigDecimal amount, BigDecimal paidAmount, LocalDate paidDate,
                                InstallmentStatus status, String paymentMethod) {
        static InstallmentResponse from(PayableInstallment i) {
            return new InstallmentResponse(i.getId(), i.getPayableId(), i.getInstallmentNumber(),
                    i.getDueDate(), i.getAmount(), i.getPaidAmount(), i.getPaidDate(),
                    i.getStatus(), i.getPaymentMethod());
        }
    }

    record RetentionResponse(UUID id, TaxType taxType, BigDecimal baseAmount,
                              BigDecimal rate, BigDecimal amount, boolean paid) {
        static RetentionResponse from(TaxRetention r) {
            return new RetentionResponse(r.getId(), r.getTaxType(), r.getBaseAmount(),
                    r.getRate(), r.getAmount(), r.isPaid());
        }
    }

    record PaymentResponse(InstallmentResponse installment, UUID transactionId, BigDecimal amountPaid) {}
    record NetAmountResponse(BigDecimal netAmount) {}
}
