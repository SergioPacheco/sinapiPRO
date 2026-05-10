package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.application.CompositionCostResult;
import com.sinapipro.api.sinapi.application.CompositionCostService;
import com.sinapipro.api.sinapi.application.SinapiImportService;
import com.sinapipro.api.sinapi.domain.Composition;
import com.sinapipro.api.sinapi.domain.CompositionRepository;
import com.sinapipro.api.sinapi.domain.MaterialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Compositions", description = "SINAPI composition catalog")
@RestController
@RequestMapping("/api/v1/compositions")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class CompositionController {

    private final CompositionRepository repository;
    private final CompositionCostService costService;
    private final SinapiImportService importService;
    private final MaterialRepository materialRepository;

    public CompositionController(CompositionRepository repository, CompositionCostService costService,
                                 SinapiImportService importService, MaterialRepository materialRepository) {
        this.repository = repository;
        this.costService = costService;
        this.importService = importService;
        this.materialRepository = materialRepository;
    }

    @Operation(summary = "List/search compositions with combined filters")
    @GetMapping
    PageResponse<CompositionResponse> list(@RequestParam(required = false) String q,
                                           @RequestParam(required = false) String origin,
                                           @RequestParam(required = false) String unit,
                                           @RequestParam(required = false) String groupName,
                                           @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(repository.findFiltered(q, origin, unit, groupName, pageable).map(CompositionResponse::from));
    }

    @Operation(summary = "Get filter options (distinct values)")
    @GetMapping("/filters")
    FilterOptions filters() {
        return new FilterOptions(repository.findDistinctUnits(), repository.findDistinctOrigins(), repository.findDistinctGroups());
    }

    public record FilterOptions(java.util.List<String> units, java.util.List<String> origins, java.util.List<String> groups) {}

    @Operation(summary = "Get composition by ID with items")
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    CompositionResponse findById(@PathVariable UUID id) {
        Composition c = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + id));
        return CompositionResponse.fromWithItems(c);
    }

    @Operation(summary = "Create custom composition")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    CompositionResponse create(@Valid @RequestBody CreateCompositionRequest req) {
        var comp = new Composition(req.code(), req.description(), req.unit(), req.groupName(), "PROPRIO");
        if (req.items() != null) {
            for (var item : req.items()) {
                var material = materialRepository.findBySinapiCode(item.materialCode())
                        .orElseThrow(() -> new DomainNotFoundException("Material not found: " + item.materialCode()));
                comp.addItem(material, item.coefficient());
            }
        }
        return CompositionResponse.from(repository.save(comp));
    }

    @Operation(summary = "Update custom composition")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    CompositionResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateCompositionRequest req) {
        Composition c = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + id));
        if (!c.isEditable()) throw new IllegalStateException("Cannot edit SINAPI compositions");
        c.update(req.description(), req.unit(), req.groupName());
        if (req.items() != null) {
            c.getItems().clear();
            for (var item : req.items()) {
                var material = materialRepository.findBySinapiCode(item.materialCode())
                        .orElseThrow(() -> new DomainNotFoundException("Material not found: " + item.materialCode()));
                c.addItem(material, item.coefficient());
            }
        }
        return CompositionResponse.fromWithItems(repository.save(c));
    }

    @Operation(summary = "Delete custom composition")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        Composition c = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + id));
        if (!c.isEditable()) throw new IllegalStateException("Cannot delete SINAPI compositions");
        repository.delete(c);
    }

    @Operation(summary = "Calculate unit cost for a composition by state and reference month")
    @GetMapping("/{id}/cost")
    CompositionCostResult calculateCost(@PathVariable UUID id,
                                        @RequestParam String state,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        return costService.calculateCost(id, state, month);
    }

    @Operation(summary = "Import materials with prices from SINAPI xlsx (Caixa format)")
    @PostMapping(value = "/import/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    SinapiImportService.ImportResult importMaterials(
            @RequestParam("file") MultipartFile file,
            @RequestParam String state,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceMonth,
            @RequestParam(defaultValue = "false") boolean desonerated) throws IOException {
        return importService.importMaterials(file.getInputStream(), state.toUpperCase(), referenceMonth, desonerated);
    }

    @Operation(summary = "Import compositions from SINAPI xlsx (Caixa analytic format)")
    @PostMapping(value = "/import/compositions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    SinapiImportService.ImportResult importCompositions(
            @RequestParam("file") MultipartFile file,
            @RequestParam String state,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceMonth,
            @RequestParam(defaultValue = "false") boolean desonerated) throws IOException {
        return importService.importCompositions(file.getInputStream(), state.toUpperCase(), referenceMonth, desonerated);
    }

    public record CreateCompositionRequest(@NotBlank String code, @NotBlank String description, @NotBlank String unit,
                                            String groupName, List<ItemRequest> items) {}
    public record UpdateCompositionRequest(@NotBlank String description, @NotBlank String unit,
                                            String groupName, List<ItemRequest> items) {}
    public record ItemRequest(@NotBlank String materialCode, @NotNull BigDecimal coefficient) {}
}
