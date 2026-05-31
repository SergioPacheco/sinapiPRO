package com.sinapipro.api.supplier.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.domain.EvaluationCriterion;
import com.sinapipro.api.shared.domain.SupplierDocumentType;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.supplier.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Supplier Sub-Resources", description = "Documents, evaluations and bank accounts per supplier")
@RestController
@RequestMapping("/api/v1/suppliers/{supplierId}")
public class SupplierSubResourceController {

    private final SupplierRepository supplierRepository;
    private final SupplierDocumentRepository documentRepository;
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierBankAccountRepository bankAccountRepository;

    public SupplierSubResourceController(SupplierRepository supplierRepository,
                                         SupplierDocumentRepository documentRepository,
                                         SupplierEvaluationRepository evaluationRepository,
                                         SupplierBankAccountRepository bankAccountRepository) {
        this.supplierRepository = supplierRepository;
        this.documentRepository = documentRepository;
        this.evaluationRepository = evaluationRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    // --- Documents ---

    @Operation(summary = "List documents for a supplier")
    @GetMapping("/documents")
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<SupplierDocument> listDocuments(@PathVariable UUID supplierId, @PageableDefault(size = 20) Pageable pageable) {
        ensureSupplierExists(supplierId);
        return PageResponse.from(documentRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId, pageable));
    }

    @Operation(summary = "Add document to supplier")
    @PostMapping("/documents")
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<SupplierDocument> createDocument(@PathVariable UUID supplierId, @Valid @RequestBody CreateDocumentRequest req) {
        ensureSupplierExists(supplierId);
        var doc = documentRepository.save(new SupplierDocument(supplierId, req.documentType(), req.number(), req.issueDate(), req.expiryDate(), req.filePath(), req.notes()));
        return ResponseEntity.created(URI.create("/api/v1/suppliers/" + supplierId + "/documents/" + doc.getId())).body(doc);
    }

    @Operation(summary = "Update a supplier document")
    @PutMapping("/documents/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    SupplierDocument updateDocument(@PathVariable UUID supplierId, @PathVariable UUID id, @Valid @RequestBody CreateDocumentRequest req) {
        ensureSupplierExists(supplierId);
        var doc = documentRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Document not found: " + id));
        doc.update(req.documentType(), req.number(), req.issueDate(), req.expiryDate(), req.filePath(), req.notes());
        return documentRepository.save(doc);
    }

    @Operation(summary = "Delete a supplier document")
    @DeleteMapping("/documents/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDocument(@PathVariable UUID supplierId, @PathVariable UUID id) {
        ensureSupplierExists(supplierId);
        if (!documentRepository.existsById(id)) throw new DomainNotFoundException("Document not found: " + id);
        documentRepository.deleteById(id);
    }

    // --- Evaluations ---

    @Operation(summary = "List evaluations for a supplier")
    @GetMapping("/evaluations")
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<SupplierEvaluation> listEvaluations(@PathVariable UUID supplierId, @PageableDefault(size = 20) Pageable pageable) {
        ensureSupplierExists(supplierId);
        return PageResponse.from(evaluationRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId, pageable));
    }

    @Operation(summary = "Add evaluation to supplier")
    @PostMapping("/evaluations")
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<SupplierEvaluation> createEvaluation(@PathVariable UUID supplierId, @Valid @RequestBody CreateEvaluationRequest req) {
        ensureSupplierExists(supplierId);
        var eval = evaluationRepository.save(new SupplierEvaluation(supplierId, req.evaluationDate(), req.criterion(), req.score(), req.evaluator(), req.notes()));
        return ResponseEntity.created(URI.create("/api/v1/suppliers/" + supplierId + "/evaluations/" + eval.getId())).body(eval);
    }

    @Operation(summary = "Delete an evaluation")
    @DeleteMapping("/evaluations/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteEvaluation(@PathVariable UUID supplierId, @PathVariable UUID id) {
        ensureSupplierExists(supplierId);
        if (!evaluationRepository.existsById(id)) throw new DomainNotFoundException("Evaluation not found: " + id);
        evaluationRepository.deleteById(id);
    }

    @Operation(summary = "Get average evaluation score for a supplier")
    @GetMapping("/evaluations/average")
    @PreAuthorize("@perm.check('registry.read')")
    AverageScoreResponse averageScore(@PathVariable UUID supplierId) {
        ensureSupplierExists(supplierId);
        return new AverageScoreResponse(evaluationRepository.averageScoreBySupplierId(supplierId));
    }

    // --- Bank Accounts ---

    @Operation(summary = "List bank accounts for a supplier")
    @GetMapping("/bank-accounts")
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<SupplierBankAccount> listBankAccounts(@PathVariable UUID supplierId, @PageableDefault(size = 20) Pageable pageable) {
        ensureSupplierExists(supplierId);
        return PageResponse.from(bankAccountRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId, pageable));
    }

    @Operation(summary = "Add bank account to supplier")
    @PostMapping("/bank-accounts")
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<SupplierBankAccount> createBankAccount(@PathVariable UUID supplierId, @Valid @RequestBody CreateBankAccountRequest req) {
        ensureSupplierExists(supplierId);
        var account = bankAccountRepository.save(new SupplierBankAccount(supplierId, req.bankCode(), req.bankName(), req.agency(), req.accountNumber(), req.accountType(), req.holderName(), req.holderDocument(), req.pixKey()));
        return ResponseEntity.created(URI.create("/api/v1/suppliers/" + supplierId + "/bank-accounts/" + account.getId())).body(account);
    }

    @Operation(summary = "Update a bank account")
    @PutMapping("/bank-accounts/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    SupplierBankAccount updateBankAccount(@PathVariable UUID supplierId, @PathVariable UUID id, @Valid @RequestBody UpdateBankAccountRequest req) {
        ensureSupplierExists(supplierId);
        var account = bankAccountRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Bank account not found: " + id));
        account.update(req.bankCode(), req.bankName(), req.agency(), req.accountNumber(), req.accountType(), req.holderName(), req.holderDocument(), req.pixKey(), req.active());
        return bankAccountRepository.save(account);
    }

    @Operation(summary = "Delete a bank account")
    @DeleteMapping("/bank-accounts/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteBankAccount(@PathVariable UUID supplierId, @PathVariable UUID id) {
        ensureSupplierExists(supplierId);
        if (!bankAccountRepository.existsById(id)) throw new DomainNotFoundException("Bank account not found: " + id);
        bankAccountRepository.deleteById(id);
    }

    // --- Helpers ---

    private void ensureSupplierExists(UUID supplierId) {
        if (!supplierRepository.existsById(supplierId)) throw new DomainNotFoundException("Supplier not found: " + supplierId);
    }

    // --- DTOs ---

    record CreateDocumentRequest(@NotNull SupplierDocumentType documentType, String number,
                                 LocalDate issueDate, LocalDate expiryDate, String filePath, String notes) {}

    record CreateEvaluationRequest(@NotNull LocalDate evaluationDate, @NotNull EvaluationCriterion criterion,
                                   @Min(1) @Max(5) int score, String evaluator, String notes) {}

    record CreateBankAccountRequest(@NotBlank String bankCode, @NotBlank String bankName, @NotBlank String agency,
                                    @NotBlank String accountNumber, String accountType, String holderName,
                                    String holderDocument, String pixKey) {}

    record UpdateBankAccountRequest(@NotBlank String bankCode, @NotBlank String bankName, @NotBlank String agency,
                                    @NotBlank String accountNumber, String accountType, String holderName,
                                    String holderDocument, String pixKey, boolean active) {}

    record AverageScoreResponse(double averageScore) {}
}
