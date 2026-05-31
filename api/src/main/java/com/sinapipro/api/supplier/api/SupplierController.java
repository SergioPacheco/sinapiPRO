package com.sinapipro.api.supplier.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.supplier.application.SupplierService;
import com.sinapipro.api.supplier.application.SupplierRankingService;
import com.sinapipro.api.supplier.domain.Supplier;
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

@Tag(name = "Suppliers", description = "Supplier management")
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierRankingService rankingService;

    public SupplierController(SupplierService supplierService, SupplierRankingService rankingService) {
        this.supplierService = supplierService;
        this.rankingService = rankingService;
    }

    @Operation(summary = "List suppliers with filters and pagination")
    @GetMapping
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<SupplierResponse> list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Supplier> page = supplierService.findAll(active, name, pageable);
        return PageResponse.from(page.map(SupplierResponse::from));
    }

    @Operation(summary = "Get supplier by ID")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.check('registry.read')")
    SupplierResponse findById(@PathVariable UUID id) {
        return SupplierResponse.from(supplierService.findById(id));
    }

    @Operation(summary = "Create a new supplier")
    @PostMapping
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<SupplierResponse> create(@Valid @RequestBody CreateSupplierRequest request) {
        Supplier supplier = supplierService.create(request);
        SupplierResponse response = SupplierResponse.from(supplier);
        return ResponseEntity.created(URI.create("/api/v1/suppliers/" + supplier.getId())).body(response);
    }

    @Operation(summary = "Update an existing supplier")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    SupplierResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateSupplierRequest request) {
        return SupplierResponse.from(supplierService.update(id, request));
    }

    @Operation(summary = "Delete a supplier")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        supplierService.delete(id);
    }
}
