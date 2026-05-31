package com.sinapipro.api.finance.api;

import com.sinapipro.api.finance.application.CnabService;
import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Checks & CNAB", description = "Sprint 10: Checks issued/received, custody, CNAB integration")
@RestController
@RequestMapping("/api/v1/finance")
public class CheckController {

    private final CheckIssuanceRepository checkIssuanceRepo;
    private final CheckReceivedRepository checkReceivedRepo;
    private final CnabService cnabService;

    public CheckController(CheckIssuanceRepository checkIssuanceRepo,
                           CheckReceivedRepository checkReceivedRepo,
                           CnabService cnabService) {
        this.checkIssuanceRepo = checkIssuanceRepo;
        this.checkReceivedRepo = checkReceivedRepo;
        this.cnabService = cnabService;
    }

    // ═══════════════════════════════════════════════════════════
    // Cheques emitidos
    // ═══════════════════════════════════════════════════════════

    @Operation(summary = "List issued checks by status")
    @GetMapping("/checks/issued")
    @PreAuthorize("@perm.check('finance.read')")
    List<CheckIssuance> listIssuedChecks(@RequestParam(defaultValue = "ISSUED") String status) {
        return checkIssuanceRepo.findByStatus(status);
    }

    @Operation(summary = "Clear an issued check")
    @PostMapping("/checks/issued/{id}/clear")
    @PreAuthorize("@perm.check('finance.write')")
    CheckIssuance clearIssuedCheck(@PathVariable UUID id, @RequestParam LocalDate clearedDate) {
        var check = checkIssuanceRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("Check not found: " + id));
        check.clear(clearedDate);
        return checkIssuanceRepo.save(check);
    }

    @Operation(summary = "Cancel an issued check")
    @PostMapping("/checks/issued/{id}/cancel")
    @PreAuthorize("@perm.check('finance.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancelIssuedCheck(@PathVariable UUID id) {
        var check = checkIssuanceRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("Check not found: " + id));
        check.cancel();
        checkIssuanceRepo.save(check);
    }

    // ═══════════════════════════════════════════════════════════
    // Cheques recebidos + custódia
    // ═══════════════════════════════════════════════════════════

    record CheckReceivedRequest(@NotBlank String bankCode, @NotBlank String agency, @NotBlank String accountNumber,
                                @NotBlank String checkNumber, @NotNull BigDecimal amount, @NotNull LocalDate issueDate,
                                LocalDate dueDate, @NotBlank String issuerName, String issuerDocument,
                                UUID receivableInstallmentId, String notes) {}

    record CheckReceivedResponse(UUID id, String checkNumber, BigDecimal amount, LocalDate dueDate,
                                 String issuerName, String status, LocalDate custodyDate, LocalDate clearedDate) {
        static CheckReceivedResponse from(CheckReceived c) {
            return new CheckReceivedResponse(c.getId(), c.getCheckNumber(), c.getAmount(), c.getDueDate(),
                    c.getIssuerName(), c.getStatus(), c.getCustodyDate(), c.getClearedDate());
        }
    }

    @Operation(summary = "List received checks by status")
    @GetMapping("/checks/received")
    @PreAuthorize("@perm.check('finance.read')")
    List<CheckReceivedResponse> listReceivedChecks(@RequestParam(defaultValue = "RECEIVED") String status) {
        return checkReceivedRepo.findByStatus(status).stream().map(CheckReceivedResponse::from).toList();
    }

    @Operation(summary = "Register a received check")
    @PostMapping("/checks/received")
    @PreAuthorize("@perm.check('finance.write')")
    ResponseEntity<CheckReceivedResponse> createReceivedCheck(@Valid @RequestBody CheckReceivedRequest req) {
        var check = new CheckReceived(req.bankCode(), req.agency(), req.accountNumber(), req.checkNumber(),
                req.amount(), req.issueDate(), req.dueDate(), req.issuerName(), req.issuerDocument());
        check.setReceivableInstallmentId(req.receivableInstallmentId());
        check.setNotes(req.notes());
        check = checkReceivedRepo.save(check);
        return ResponseEntity.created(URI.create("/api/v1/finance/checks/received/" + check.getId()))
                .body(CheckReceivedResponse.from(check));
    }

    @Operation(summary = "Send check to bank custody")
    @PostMapping("/checks/received/{id}/custody")
    @PreAuthorize("@perm.check('finance.write')")
    CheckReceivedResponse sendToCustody(@PathVariable UUID id, @RequestParam UUID bankAccountId) {
        var check = checkReceivedRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("Check not found: " + id));
        check.sendToCustody(bankAccountId);
        return CheckReceivedResponse.from(checkReceivedRepo.save(check));
    }

    @Operation(summary = "Clear a received check (compensated)")
    @PostMapping("/checks/received/{id}/clear")
    @PreAuthorize("@perm.check('finance.write')")
    CheckReceivedResponse clearReceivedCheck(@PathVariable UUID id, @RequestParam LocalDate clearedDate) {
        var check = checkReceivedRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("Check not found: " + id));
        check.clear(clearedDate);
        return CheckReceivedResponse.from(checkReceivedRepo.save(check));
    }

    @Operation(summary = "Return a received check (bounced)")
    @PostMapping("/checks/received/{id}/return")
    @PreAuthorize("@perm.check('finance.write')")
    CheckReceivedResponse returnCheck(@PathVariable UUID id, @RequestParam String reason) {
        var check = checkReceivedRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("Check not found: " + id));
        check.returnCheck(reason);
        return CheckReceivedResponse.from(checkReceivedRepo.save(check));
    }

    @Operation(summary = "List checks in custody for a bank account")
    @GetMapping("/checks/custody/{bankAccountId}")
    @PreAuthorize("@perm.check('finance.read')")
    List<CheckReceivedResponse> listCustody(@PathVariable UUID bankAccountId) {
        return checkReceivedRepo.findByCustodyBankAccountId(bankAccountId).stream()
                .filter(c -> "IN_CUSTODY".equals(c.getStatus()))
                .map(CheckReceivedResponse::from).toList();
    }

    // ═══════════════════════════════════════════════════════════
    // CNAB — Pagamento (complemento ao CnabService existente)
    // ═══════════════════════════════════════════════════════════

    @Operation(summary = "Generate CNAB remittance file for receivables (boletos)")
    @PostMapping("/cnab/remittance")
    @PreAuthorize("@perm.check('finance.write')")
    CnabService.CnabFile generateRemittance(@RequestParam UUID bankAccountId, @RequestBody List<UUID> installmentIds) {
        return cnabService.generateRemittance(bankAccountId, installmentIds);
    }

    @Operation(summary = "Process CNAB return file (automatic reconciliation)")
    @PostMapping("/cnab/return")
    @PreAuthorize("@perm.check('finance.write')")
    CnabService.CnabReturnResult processReturn(@RequestBody String fileContent) {
        return cnabService.processReturn(fileContent);
    }
}
