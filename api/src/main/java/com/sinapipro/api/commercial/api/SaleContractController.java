package com.sinapipro.api.commercial.api;

import com.sinapipro.api.commercial.application.*;
import com.sinapipro.api.commercial.application.CommissionService.CommissionSummary;
import com.sinapipro.api.commercial.application.ContractCancellationService.CancellationResult;
import com.sinapipro.api.commercial.application.SalesProposalService.*;
import com.sinapipro.api.commercial.domain.*;
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

@Tag(name = "Sale Contracts", description = "Vendas imobiliárias: contratos, parcelas, reajuste, distrato, comissões")
@RestController
@RequestMapping("/api/v1/developments/{developmentId}/sales")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class SaleContractController {

    private final SaleContractService contractService;
    private final SaleInstallmentService installmentService;
    private final ContractCancellationService cancellationService;
    private final CommissionService commissionService;
    private final SalesProposalService proposalService;

    public SaleContractController(SaleContractService contractService,
                                   SaleInstallmentService installmentService,
                                   ContractCancellationService cancellationService,
                                   CommissionService commissionService,
                                   SalesProposalService proposalService) {
        this.contractService = contractService;
        this.installmentService = installmentService;
        this.cancellationService = cancellationService;
        this.commissionService = commissionService;
        this.proposalService = proposalService;
    }

    // --- Contracts ---

    @GetMapping("/contracts")
    PageResponse<ContractResponse> list(@PathVariable UUID developmentId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(contractService.listByDevelopment(developmentId, pageable).map(ContractResponse::from));
    }

    @PostMapping("/contracts")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ContractResponse create(@PathVariable UUID developmentId, @Valid @RequestBody CreateContractRequest req) {
        var contract = contractService.create(developmentId, req.contractNumber(), req.contractDate(),
                req.totalAmount(), req.installmentCount(), req.amortizationType(),
                req.downPayment(), req.indexId(), req.interestRate());
        return ContractResponse.from(contract);
    }

    @GetMapping("/contracts/{contractId}")
    ContractResponse get(@PathVariable UUID developmentId, @PathVariable UUID contractId) {
        return ContractResponse.from(contractService.findById(contractId));
    }

    @PostMapping("/contracts/{contractId}/sign")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ContractResponse sign(@PathVariable UUID developmentId, @PathVariable UUID contractId,
                           @RequestBody(required = false) SignRequest req) {
        return ContractResponse.from(contractService.sign(contractId, req != null ? req.signingDate() : null));
    }

    @PostMapping("/contracts/{contractId}/activate")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ContractResponse activate(@PathVariable UUID developmentId, @PathVariable UUID contractId) {
        return ContractResponse.from(contractService.activate(contractId));
    }

    @PostMapping("/contracts/{contractId}/cancel")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    CancellationResult cancel(@PathVariable UUID developmentId, @PathVariable UUID contractId,
                               @Valid @RequestBody CancelRequest req) {
        return cancellationService.cancel(contractId, req.reason(), req.finePct());
    }

    @PostMapping("/contracts/{contractId}/broker")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ContractResponse setBroker(@PathVariable UUID developmentId, @PathVariable UUID contractId,
                                @Valid @RequestBody SetBrokerRequest req) {
        return ContractResponse.from(contractService.setBroker(contractId, req.brokerId(), req.commissionRate()));
    }

    // --- Installments ---

    @PostMapping("/contracts/{contractId}/installments/generate")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    List<InstallmentResponse> generateInstallments(@PathVariable UUID developmentId, @PathVariable UUID contractId,
                                                    @Valid @RequestBody GenerateRequest req) {
        return installmentService.generateInstallments(contractId, req.firstDueDate())
                .stream().map(InstallmentResponse::from).toList();
    }

    @GetMapping("/contracts/{contractId}/installments")
    List<InstallmentResponse> listInstallments(@PathVariable UUID developmentId, @PathVariable UUID contractId) {
        return installmentService.findByContract(contractId).stream().map(InstallmentResponse::from).toList();
    }

    @PostMapping("/contracts/{contractId}/installments/adjust")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    AdjustResult adjustByIndex(@PathVariable UUID developmentId, @PathVariable UUID contractId,
                                @RequestBody AdjustRequest req) {
        return new AdjustResult(installmentService.adjustByIndex(contractId, req.indexFactor()));
    }

    @PostMapping("/installments/{installmentId}/pay")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    InstallmentResponse pay(@PathVariable UUID developmentId, @PathVariable UUID installmentId,
                             @Valid @RequestBody PayRequest req) {
        return InstallmentResponse.from(installmentService.pay(installmentId, req.amount(),
                req.date(), req.interest(), req.fine(), req.discount()));
    }

    // --- Commissions ---

    @GetMapping("/commissions")
    List<CommissionSummary> commissions(@PathVariable UUID developmentId) {
        return commissionService.listByDevelopment(developmentId);
    }

    @GetMapping("/contracts/{contractId}/commission")
    CommissionSummary commission(@PathVariable UUID developmentId, @PathVariable UUID contractId) {
        return commissionService.calculate(contractId);
    }

    // --- 9.2 Simulação de parcelas ---

    @Operation(summary = "Simulate installments for a proposal (no persistence)")
    @PostMapping("/simulate")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    InstallmentSimulation simulate(@PathVariable UUID developmentId, @Valid @RequestBody SimulateRequest req) {
        return proposalService.simulate(req.totalAmount(), req.downPayment(), req.installmentCount(),
                req.monthlyRate(), req.amortizationType(), req.firstDueDate());
    }

    // --- 9.6 Cessão/transferência ---

    @Operation(summary = "Transfer contract to a new buyer")
    @PostMapping("/contracts/{contractId}/transfer")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    TransferResult transfer(@PathVariable UUID developmentId, @PathVariable UUID contractId,
                             @Valid @RequestBody TransferRequest req) {
        return proposalService.transfer(contractId, req.newContractNumber(), req.newBuyerId(), req.transferDate());
    }

    // --- 9.8 Repasse bancário ---

    @Operation(summary = "Register bank handover (financing)")
    @PostMapping("/contracts/{contractId}/bank-handover")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    BankHandover bankHandover(@PathVariable UUID developmentId, @PathVariable UUID contractId,
                               @Valid @RequestBody BankHandoverRequest req) {
        return proposalService.registerBankHandover(contractId, req.bankName(), req.financedAmount(),
                req.submissionDate(), req.status(), req.notes());
    }

    // DTOs
    record CreateContractRequest(@NotBlank String contractNumber, @NotNull LocalDate contractDate,
                                  @NotNull BigDecimal totalAmount, int installmentCount,
                                  String amortizationType, BigDecimal downPayment,
                                  UUID indexId, BigDecimal interestRate) {}
    record SignRequest(LocalDate signingDate) {}
    record CancelRequest(@NotBlank String reason, BigDecimal finePct) {}
    record SetBrokerRequest(@NotNull UUID brokerId, @NotNull BigDecimal commissionRate) {}
    record GenerateRequest(@NotNull LocalDate firstDueDate) {}
    record AdjustRequest(@NotNull BigDecimal indexFactor) {}
    record PayRequest(@NotNull BigDecimal amount, LocalDate date, BigDecimal interest, BigDecimal fine, BigDecimal discount) {}
    record AdjustResult(int adjustedCount) {}
    record SimulateRequest(@NotNull BigDecimal totalAmount, BigDecimal downPayment, int installmentCount,
                           BigDecimal monthlyRate, String amortizationType, @NotNull LocalDate firstDueDate) {}
    record TransferRequest(@NotBlank String newContractNumber, UUID newBuyerId, @NotNull LocalDate transferDate) {}
    record BankHandoverRequest(@NotBlank String bankName, @NotNull BigDecimal financedAmount,
                               @NotNull LocalDate submissionDate, String status, String notes) {}

    record ContractResponse(UUID id, String contractNumber, LocalDate contractDate, String status,
                             BigDecimal totalAmount, BigDecimal downPayment, int installmentCount,
                             String amortizationType, BigDecimal interestRate, UUID brokerId,
                             BigDecimal commissionRate, BigDecimal commissionAmount) {
        static ContractResponse from(SaleContract c) {
            return new ContractResponse(c.getId(), c.getContractNumber(), c.getContractDate(), c.getStatus(),
                    c.getTotalAmount(), c.getDownPayment(), c.getInstallmentCount(),
                    c.getAmortizationType(), c.getInterestRate(), c.getBrokerId(),
                    c.getCommissionRate(), c.getCommissionAmount());
        }
    }

    record InstallmentResponse(UUID id, int number, String type, LocalDate dueDate,
                                BigDecimal originalAmount, BigDecimal adjustedAmount,
                                BigDecimal paidAmount, String status) {
        static InstallmentResponse from(SaleInstallment i) {
            return new InstallmentResponse(i.getId(), i.getInstallmentNumber(), i.getType(),
                    i.getCurrentDueDate(), i.getOriginalAmount(), i.getAdjustedAmount(),
                    i.getPaidAmount(), i.getStatus());
        }
    }
}
