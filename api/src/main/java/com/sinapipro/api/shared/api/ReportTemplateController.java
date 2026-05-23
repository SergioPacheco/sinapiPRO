package com.sinapipro.api.shared.api;

import com.sinapipro.api.shared.domain.ReportTemplate;
import com.sinapipro.api.shared.domain.ReportTemplateRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Report Templates", description = "Customizable report templates (logo, header, footer, colors)")
@RestController
@RequestMapping("/api/v1/report-templates")
public class ReportTemplateController {

    private final ReportTemplateRepository repository;

    public ReportTemplateController(ReportTemplateRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "List all report templates")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<ReportTemplate> list(@RequestParam(required = false) String type) {
        if (type != null) return repository.findByTypeOrderByNameAsc(type);
        return repository.findAll();
    }

    @Operation(summary = "Get a report template by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ReportTemplate get(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Report template not found: " + id));
    }

    @Operation(summary = "Create a report template")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<ReportTemplate> create(@Valid @RequestBody CreateTemplateRequest req) {
        var template = new ReportTemplate(req.name(), req.type(), req.description());
        template.setLogoPath(req.logoPath());
        template.setHeaderText(req.headerText());
        template.setFooterText(req.footerText());
        if (req.primaryColor() != null) template.setPrimaryColor(req.primaryColor());
        template.setSettings(req.settings());
        var saved = repository.save(template);
        return ResponseEntity.created(URI.create("/api/v1/report-templates/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Update a report template")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ReportTemplate update(@PathVariable UUID id, @Valid @RequestBody UpdateTemplateRequest req) {
        var template = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Report template not found: " + id));
        template.update(req.name(), req.type(), req.description());
        template.setLogoPath(req.logoPath());
        template.setHeaderText(req.headerText());
        template.setFooterText(req.footerText());
        if (req.primaryColor() != null) template.setPrimaryColor(req.primaryColor());
        template.setSettings(req.settings());
        return repository.save(template);
    }

    @Operation(summary = "Delete a report template")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        if (!repository.existsById(id)) throw new DomainNotFoundException("Report template not found: " + id);
        repository.deleteById(id);
    }

    record CreateTemplateRequest(@NotBlank String name, String type, String description,
                                 String logoPath, String headerText, String footerText,
                                 String primaryColor, Map<String, Object> settings) {}

    record UpdateTemplateRequest(@NotBlank String name, String type, String description,
                                 String logoPath, String headerText, String footerText,
                                 String primaryColor, Map<String, Object> settings) {}
}
