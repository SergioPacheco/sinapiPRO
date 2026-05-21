package com.sinapipro.api.procurement.api;

import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Supplier Portal", description = "Public endpoints for suppliers to respond to quotations (no auth required)")
@RestController
@RequestMapping("/api/v1/supplier-portal")
public class SupplierPortalController {

    private final SupplierPortalTokenRepository tokenRepository;
    private final QuotationRepository quotationRepository;
    private final SupplierRepository supplierRepository;

    public SupplierPortalController(SupplierPortalTokenRepository tokenRepository,
                                    QuotationRepository quotationRepository,
                                    SupplierRepository supplierRepository) {
        this.tokenRepository = tokenRepository;
        this.quotationRepository = quotationRepository;
        this.supplierRepository = supplierRepository;
    }

    @Operation(summary = "Validate token and get quotation details for supplier")
    @GetMapping("/quotation")
    public PortalQuotationResponse getQuotation(@RequestParam String token) {
        var portalToken = validateToken(token);
        var quotation = quotationRepository.findById(portalToken.getQuotationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quotation not found"));
        var supplier = supplierRepository.findById(portalToken.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
        var request = quotation.getPurchaseRequest();

        return new PortalQuotationResponse(
                quotation.getId(),
                request.getDescription(),
                request.getQuantity(),
                request.getUnit(),
                quotation.getDeadline() != null ? quotation.getDeadline().toString() : null,
                quotation.getStatus(),
                supplier.getName(),
                portalToken.isUsed()
        );
    }

    @Operation(summary = "Submit quotation response (supplier fills price and delivery)")
    @PostMapping("/quotation/respond")
    @Transactional
    public ResponseEntity<PortalResponseConfirmation> respond(@RequestParam String token,
                                                              @Valid @RequestBody PortalSubmitRequest req) {
        var portalToken = validateToken(token);
        if (portalToken.isUsed()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This quotation has already been responded to");
        }

        var quotation = quotationRepository.findById(portalToken.getQuotationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!"OPEN".equals(quotation.getStatus())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Quotation is no longer open");
        }

        var supplier = supplierRepository.findById(portalToken.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var response = new QuotationResponse(quotation, supplier, req.unitPrice(), req.deliveryDays(), req.notes());
        quotation.getResponses().add(response);
        quotationRepository.save(quotation);
        portalToken.markUsed();
        tokenRepository.save(portalToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PortalResponseConfirmation("Cotação enviada com sucesso", supplier.getName()));
    }

    @Operation(summary = "Generate a portal token for a supplier (internal use)")
    @PostMapping("/tokens")
    @Transactional
    public TokenResponse generateToken(@Valid @RequestBody GenerateTokenRequest req) {
        var token = new SupplierPortalToken(req.quotationId(), req.supplierId(), req.expirationDays() != null ? req.expirationDays() : 7);
        token = tokenRepository.save(token);
        return new TokenResponse(token.getToken(), token.getExpiresAt().toString());
    }

    private SupplierPortalToken validateToken(String token) {
        var portalToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token"));
        if (portalToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Token has expired");
        }
        return portalToken;
    }

    // --- DTOs ---
    record PortalQuotationResponse(UUID quotationId, String itemDescription, BigDecimal quantity, String unit,
                                   String deadline, String status, String supplierName, boolean alreadyResponded) {}
    record PortalSubmitRequest(@NotNull @Positive BigDecimal unitPrice, Integer deliveryDays, String notes) {}
    record PortalResponseConfirmation(String message, String supplierName) {}
    record GenerateTokenRequest(@NotNull UUID quotationId, @NotNull UUID supplierId, Integer expirationDays) {}
    record TokenResponse(String token, String expiresAt) {}
}
