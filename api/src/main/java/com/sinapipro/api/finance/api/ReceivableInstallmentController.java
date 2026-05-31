package com.sinapipro.api.finance.api;

import com.sinapipro.api.finance.application.ReceivableInstallmentService;
import com.sinapipro.api.finance.application.ReceivablePaymentService;
import com.sinapipro.api.finance.domain.InstallmentStatus;
import com.sinapipro.api.finance.domain.ReceivableInstallment;
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

@Tag(name = "Receivable Installments", description = "Parcelas de contas a receber e recebimentos")
@RestController
@RequestMapping("/api/v1/receivables")
@PreAuthorize("@perm.check('finance.read')")
public class ReceivableInstallmentController {

    private final ReceivableInstallmentService installmentService;
    private final ReceivablePaymentService paymentService;

    public ReceivableInstallmentController(ReceivableInstallmentService installmentService,
                                            ReceivablePaymentService paymentService) {
        this.installmentService = installmentService;
        this.paymentService = paymentService;
    }

    @Operation(summary = "Generate installments (Price amortization)")
    @PostMapping("/{receivableId}/installments/price")
    @PreAuthorize("@perm.check('finance.write')")
    @ResponseStatus(HttpStatus.CREATED)
    List<InstallmentResponse> generatePrice(@PathVariable UUID receivableId,
                                             @Valid @RequestBody GenerateRequest req) {
        return installmentService.generatePrice(receivableId, req.numberOfInstallments(),
                req.firstDueDate(), req.monthlyRate())
                .stream().map(InstallmentResponse::from).toList();
    }

    @Operation(summary = "Generate installments (SAC amortization)")
    @PostMapping("/{receivableId}/installments/sac")
    @PreAuthorize("@perm.check('finance.write')")
    @ResponseStatus(HttpStatus.CREATED)
    List<InstallmentResponse> generateSAC(@PathVariable UUID receivableId,
                                           @Valid @RequestBody GenerateRequest req) {
        return installmentService.generateSAC(receivableId, req.numberOfInstallments(),
                req.firstDueDate(), req.monthlyRate())
                .stream().map(InstallmentResponse::from).toList();
    }

    @Operation(summary = "List installments of a receivable")
    @GetMapping("/{receivableId}/installments")
    List<InstallmentResponse> list(@PathVariable UUID receivableId) {
        return installmentService.findByReceivable(receivableId).stream()
                .map(InstallmentResponse::from).toList();
    }

    @Operation(summary = "Receive payment for an installment")
    @PostMapping("/installments/{installmentId}/receive")
    @PreAuthorize("@perm.check('finance.write')")
    InstallmentResponse receive(@PathVariable UUID installmentId,
                                 @Valid @RequestBody ReceiveRequest req) {
        var result = paymentService.receive(installmentId, req.receivedAmount(),
                req.receivedDate(), req.bankAccountId(), req.interest(), req.fine(), req.discount());
        return InstallmentResponse.from(result);
    }

    // DTOs
    record GenerateRequest(@Positive @NotNull Integer numberOfInstallments,
                            @NotNull LocalDate firstDueDate, BigDecimal monthlyRate) {}

    record ReceiveRequest(@NotNull BigDecimal receivedAmount, LocalDate receivedDate,
                           UUID bankAccountId, BigDecimal interest,
                           BigDecimal fine, BigDecimal discount) {}

    record InstallmentResponse(UUID id, UUID receivableId, int number, LocalDate dueDate,
                                BigDecimal amount, BigDecimal receivedAmount, LocalDate receivedDate,
                                InstallmentStatus status, String boletoNumber, String ourNumber) {
        static InstallmentResponse from(ReceivableInstallment i) {
            return new InstallmentResponse(i.getId(), i.getReceivableId(), i.getInstallmentNumber(),
                    i.getDueDate(), i.getAmount(), i.getReceivedAmount(), i.getReceivedDate(),
                    i.getStatus(), i.getBoletoNumber(), i.getOurNumber());
        }
    }
}
