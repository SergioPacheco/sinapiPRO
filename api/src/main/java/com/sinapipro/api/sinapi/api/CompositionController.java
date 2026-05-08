package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.application.CompositionCostResult;
import com.sinapipro.api.sinapi.application.CompositionCostService;
import com.sinapipro.api.sinapi.application.SinapiImportService;
import com.sinapipro.api.sinapi.domain.Composition;
import com.sinapipro.api.sinapi.domain.CompositionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Compositions", description = "SINAPI composition catalog")
@RestController
@RequestMapping("/api/v1/compositions")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class CompositionController {

    private final CompositionRepository repository;
    private final CompositionCostService costService;
    private final SinapiImportService importService;

    public CompositionController(CompositionRepository repository, CompositionCostService costService,
                                 SinapiImportService importService) {
        this.repository = repository;
        this.costService = costService;
        this.importService = importService;
    }

    @Operation(summary = "List compositions with optional group filter")
    @GetMapping
    PageResponse<CompositionResponse> list(@RequestParam(required = false) String groupName,
                                           @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(repository.findFiltered(groupName, pageable).map(CompositionResponse::from));
    }

    @Operation(summary = "Full-text search compositions by code or description")
    @GetMapping("/search")
    PageResponse<CompositionResponse> search(@RequestParam String q,
                                             @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(repository.fullTextSearch(q, pageable).map(CompositionResponse::from));
    }

    @Operation(summary = "Get composition by ID with items")
    @GetMapping("/{id}")
    CompositionResponse findById(@PathVariable UUID id) {
        Composition c = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + id));
        return CompositionResponse.from(c);
    }

    @Operation(summary = "Calculate unit cost for a composition by state and reference month")
    @GetMapping("/{id}/cost")
    CompositionCostResult calculateCost(@PathVariable UUID id,
                                        @RequestParam String state,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        return costService.calculateCost(id, state, month);
    }

    @Operation(summary = "Import materials with prices from CSV")
    @PostMapping(value = "/import/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    SinapiImportService.ImportResult importMaterials(@RequestParam("file") MultipartFile file,
                                                     @RequestParam(defaultValue = ";") String separator) throws IOException {
        return importService.importMaterials(file.getInputStream(), separator);
    }

    @Operation(summary = "Import compositions from CSV")
    @PostMapping(value = "/import/compositions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    SinapiImportService.ImportResult importCompositions(@RequestParam("file") MultipartFile file,
                                                        @RequestParam(defaultValue = ";") String separator) throws IOException {
        return importService.importCompositions(file.getInputStream(), separator);
    }
}
