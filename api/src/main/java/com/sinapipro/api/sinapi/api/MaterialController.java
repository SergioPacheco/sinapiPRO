package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.domain.Material;
import com.sinapipro.api.sinapi.domain.MaterialPrice;
import com.sinapipro.api.sinapi.domain.MaterialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Materials", description = "SINAPI material/input catalog")
@RestController
@RequestMapping("/api/v1/materials")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class MaterialController {

    private final MaterialRepository repository;

    public MaterialController(MaterialRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "List/search materials with combined filters")
    @GetMapping
    PageResponse<MaterialResponse> list(@RequestParam(required = false) String q,
                                        @RequestParam(required = false) String origin,
                                        @RequestParam(required = false) String unit,
                                        @RequestParam(required = false) String state,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceMonth,
                                        @RequestParam(defaultValue = "false") boolean desonerated,
                                        @PageableDefault(size = 20) Pageable pageable) {
        Page<Material> page = repository.findFiltered(q, origin, unit, pageable);
        return toResponse(page, state, referenceMonth, desonerated);
    }

    @Operation(summary = "Get filter options (distinct values)")
    @GetMapping("/filters")
    FilterOptions filters() {
        return new FilterOptions(repository.findDistinctUnits(), repository.findDistinctOrigins());
    }

    public record FilterOptions(java.util.List<String> units, java.util.List<String> origins) {}

    @Operation(summary = "Get material by ID with prices")
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    MaterialDetailResponse findById(@PathVariable UUID id) {
        Material m = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Material not found: " + id));
        return MaterialDetailResponse.from(m);
    }

    @Operation(summary = "Add price to material")
    @PostMapping("/{id}/prices")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    MaterialDetailResponse addPrice(@PathVariable UUID id, @Valid @RequestBody AddPriceRequest req) {
        Material m = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Material not found: " + id));
        boolean exists = m.getPrices().stream()
                .anyMatch(p -> p.getState().equals(req.state()) && p.getReferenceMonth().equals(req.referenceMonth()) && p.isDesonerated() == req.desonerated());
        if (exists) throw new IllegalStateException("Price already exists for this state/month/desonerated combination");
        m.getPrices().add(new MaterialPrice(m, req.state(), req.referenceMonth(), req.price(), req.desonerated()));
        return MaterialDetailResponse.from(repository.save(m));
    }

    public record AddPriceRequest(
            @NotBlank String state,
            @NotNull java.time.LocalDate referenceMonth,
            @NotNull java.math.BigDecimal price,
            boolean desonerated) {}

    @Operation(summary = "Create custom material")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    MaterialResponse create(@Valid @RequestBody CreateMaterialRequest req) {
        var material = new Material(req.code(), req.description(), req.unit(), "PROPRIO");
        return MaterialResponse.from(repository.save(material));
    }

    @Operation(summary = "Update custom material")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    MaterialResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateMaterialRequest req) {
        Material m = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Material not found: " + id));
        if (!m.isEditable()) throw new IllegalStateException("Cannot edit SINAPI materials");
        m.update(req.description(), req.unit());
        return MaterialResponse.from(repository.save(m));
    }

    @Operation(summary = "Delete custom material")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        Material m = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Material not found: " + id));
        if (!m.isEditable()) throw new IllegalStateException("Cannot delete SINAPI materials");
        repository.delete(m);
    }

    private PageResponse<MaterialResponse> toResponse(Page<Material> page, String state, LocalDate referenceMonth, boolean desonerated) {
        if (state == null || referenceMonth == null) {
            return PageResponse.from(page.map(MaterialResponse::from));
        }
        var ids = page.getContent().stream().map(Material::getId).toList();
        var prices = repository.findPricesBatch(ids, state.toUpperCase(), referenceMonth);
        Map<UUID, BigDecimal> priceMap = prices.stream()
                .filter(p -> p.isDesonerated() == desonerated)
                .collect(Collectors.toMap(p -> p.getMaterial().getId(), MaterialPrice::getPrice, (a, b) -> a));
        return PageResponse.from(page.map(m -> new MaterialResponse(
                m.getId(), m.getSinapiCode(), m.getDescription(), m.getUnit(), m.getOrigin(),
                priceMap.get(m.getId()), state.toUpperCase(), referenceMonth, m.getCreatedAt()
        )));
    }

    public record CreateMaterialRequest(@NotBlank String code, @NotBlank String description, @NotBlank String unit) {}
    public record UpdateMaterialRequest(@NotBlank String description, @NotBlank String unit) {}
    public record MaterialDetailResponse(UUID id, String sinapiCode, String description, String unit, String origin,
                                          boolean editable, java.util.List<PriceEntry> prices) {
        public static MaterialDetailResponse from(Material m) {
            var prices = m.getPrices().stream()
                    .map(p -> new PriceEntry(p.getState(), p.getReferenceMonth(), p.getPrice(), p.isDesonerated()))
                    .toList();
            return new MaterialDetailResponse(m.getId(), m.getSinapiCode(), m.getDescription(), m.getUnit(),
                    m.getOrigin(), m.isEditable(), prices);
        }
        public record PriceEntry(String state, LocalDate referenceMonth, BigDecimal price, boolean desonerated) {}
    }
}
