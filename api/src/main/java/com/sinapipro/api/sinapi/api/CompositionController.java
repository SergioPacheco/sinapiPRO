package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.application.CompositionCostResult;
import com.sinapipro.api.sinapi.application.CompositionCostService;
import com.sinapipro.api.sinapi.application.CompositionVersionService;
import com.sinapipro.api.sinapi.application.ItemSearchService;
import com.sinapipro.api.sinapi.application.SinapiImportService;
import com.sinapipro.api.sinapi.domain.*;
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
@PreAuthorize("@perm.check('budget.read')")
public class CompositionController {

    private final CompositionRepository compositionRepository;
    private final CompositionCostService costService;
    private final SinapiImportService importService;
    private final MaterialRepository materialRepository;
    private final CompositionVersionService versionService;
    private final ItemSearchService itemSearchService;

    public CompositionController(CompositionRepository compositionRepository,
                                 CompositionCostService costService,
                                 SinapiImportService importService,
                                 MaterialRepository materialRepository,
                                 CompositionVersionService versionService,
                                 ItemSearchService itemSearchService) {
        this.compositionRepository = compositionRepository;
        this.costService = costService;
        this.importService = importService;
        this.materialRepository = materialRepository;
        this.versionService = versionService;
        this.itemSearchService = itemSearchService;
    }

    @Operation(summary = "List/search compositions with combined filters")
    @GetMapping
    PageResponse<CompositionResponse> list(@RequestParam(required = false) String q,
                                           @RequestParam(required = false) String origin,
                                           @RequestParam(required = false) String unit,
                                           @RequestParam(required = false) String groupName,
                                           @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(compositionRepository.findFiltered(q, origin, unit, groupName, pageable).map(CompositionResponse::from));
    }

    @Operation(summary = "Get filter options (distinct values)")
    @GetMapping("/filters")
    FilterOptions filters() {
        return new FilterOptions(compositionRepository.findDistinctUnits(), compositionRepository.findDistinctOrigins(), compositionRepository.findDistinctGroups());
    }

    public record FilterOptions(List<String> units, List<String> origins, List<String> groups) {}

    @Operation(summary = "Search materials and compositions for autocomplete")
    @GetMapping("/items/search")
    List<ItemSearchService.ItemSearchResult> searchItems(
            @RequestParam String q,
            @RequestParam(required = false) ItemType type) {
        return itemSearchService.search(q, type);
    }

    @Operation(summary = "Get composition by ID with items")
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    CompositionResponse findById(@PathVariable UUID id) {
        Composition c = compositionRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + id));
        return CompositionResponse.fromWithItems(c);
    }

    @Operation(summary = "Copy SINAPI composition to custom catalog")
    @PostMapping("/{id}/copy")
    @PreAuthorize("@perm.check('sinapi.import')")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    CompositionResponse copy(@PathVariable UUID id) {
        return CompositionResponse.from(versionService.copyFromSinapi(id));
    }

    @Operation(summary = "Create custom composition")
    @PostMapping
    @PreAuthorize("@perm.check('sinapi.import')")
    @ResponseStatus(HttpStatus.CREATED)
    CompositionResponse create(@Valid @RequestBody CreateCompositionRequest req) {
        var comp = new Composition(req.code(), req.description(), req.unit(), req.groupName(), "PROPRIO");
        if (req.items() != null) {
            for (var item : req.items()) {
                if (item.itemType() == ItemType.COMPOSITION) {
                    var child = compositionRepository.findById(item.childCompositionId())
                            .orElseThrow(() -> new DomainNotFoundException("Child composition not found: " + item.childCompositionId()));
                    comp.addCompositionItem(child, item.coefficient());
                } else {
                    var material = materialRepository.findBySinapiCode(item.materialCode())
                            .orElseThrow(() -> new DomainNotFoundException("Material not found: " + item.materialCode()));
                    comp.addItem(material, item.coefficient(), item.itemType());
                }
            }
        }
        return CompositionResponse.from(compositionRepository.save(comp));
    }

    @Operation(summary = "Update custom composition (creates new version)")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.check('sinapi.import')")
    @Transactional
    CompositionResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateCompositionRequest req) {
        var items = req.items() != null ? req.items().stream()
                .map(i -> new CompositionVersionService.ItemInput(i.materialCode(), i.childCompositionId(), i.coefficient(), i.itemType()))
                .toList() : null;
        var updated = versionService.updateWithNewVersion(id, req.description(), req.unit(), req.groupName(), items);
        return CompositionResponse.fromWithItems(updated);
    }

    @Operation(summary = "Delete custom composition")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.check('sinapi.import')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        Composition c = compositionRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + id));
        if (!c.isEditable()) throw new IllegalStateException("Cannot delete SINAPI compositions");
        compositionRepository.delete(c);
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

    @Operation(summary = "Import SINAPI ZIP (auto-detects and imports materials + compositions)")
    @PostMapping(value = "/import/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    ZipImportResult importZip(@RequestParam("file") MultipartFile file) throws IOException {
        return importService.importZip(file.getInputStream(), file.getOriginalFilename());
    }

    public record ZipImportResult(String state, String referenceMonth, boolean desonerated,
                                   SinapiImportService.ImportResult materials,
                                   SinapiImportService.ImportResult compositions) {}

    public record CreateCompositionRequest(@NotBlank String code, @NotBlank String description, @NotBlank String unit,
                                            String groupName, List<ItemRequest> items) {}
    public record UpdateCompositionRequest(@NotBlank String description, @NotBlank String unit,
                                            String groupName, List<ItemRequest> items) {}
    public record ItemRequest(
            String materialCode,
            UUID childCompositionId,
            @NotNull BigDecimal coefficient,
            @NotNull ItemType itemType
    ) {}
}
