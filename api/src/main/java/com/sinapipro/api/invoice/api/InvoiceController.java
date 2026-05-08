package com.sinapipro.api.invoice.api;

import com.sinapipro.api.invoice.application.InvoiceService;
import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceStatus;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Invoices", description = "Invoice management")
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "List invoices with filters and pagination")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<InvoiceResponse> list(
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) UUID budgetId,
            @RequestParam(required = false) UUID supplierId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Invoice> page = invoiceService.findAll(status, budgetId, supplierId, pageable);
        return PageResponse.from(page.map(InvoiceResponse::from));
    }

    @Operation(summary = "Get invoice by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    InvoiceResponse findById(@PathVariable UUID id) {
        return InvoiceResponse.from(invoiceService.findById(id));
    }

    @Operation(summary = "Create a new invoice")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<InvoiceResponse> create(@Valid @RequestBody CreateInvoiceRequest request) {
        Invoice invoice = invoiceService.create(request);
        InvoiceResponse response = InvoiceResponse.from(invoice);
        return ResponseEntity.created(URI.create("/api/v1/invoices/" + invoice.getId())).body(response);
    }

    @Operation(summary = "Update an existing invoice")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    InvoiceResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateInvoiceRequest request) {
        return InvoiceResponse.from(invoiceService.update(id, request));
    }

    @Operation(summary = "Delete an invoice")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        invoiceService.delete(id);
    }
}
