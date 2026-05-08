package com.sinapipro.api.sinapi.api;

import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.sinapi.domain.MaterialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Materials", description = "SINAPI material/input catalog")
@RestController
@RequestMapping("/api/v1/materials")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class MaterialController {

    private final MaterialRepository repository;

    public MaterialController(MaterialRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "List all materials with pagination")
    @GetMapping
    PageResponse<MaterialResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(repository.findAll(pageable).map(MaterialResponse::from));
    }

    @Operation(summary = "Full-text search materials by code or description")
    @GetMapping("/search")
    PageResponse<MaterialResponse> search(@RequestParam String q,
                                          @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(repository.fullTextSearch(q, pageable).map(MaterialResponse::from));
    }
}
