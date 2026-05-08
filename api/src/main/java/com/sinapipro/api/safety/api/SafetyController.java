package com.sinapipro.api.safety.api;

import com.sinapipro.api.safety.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Safety", description = "Safety checklists, inspections and incident reporting")
@RestController
@RequestMapping("/api/v1")
public class SafetyController {

    private final SafetyChecklistTemplateRepository templateRepository;
    private final SafetyInspectionRepository inspectionRepository;
    private final SafetyIncidentRepository incidentRepository;

    public SafetyController(SafetyChecklistTemplateRepository templateRepository,
                            SafetyInspectionRepository inspectionRepository,
                            SafetyIncidentRepository incidentRepository) {
        this.templateRepository = templateRepository;
        this.inspectionRepository = inspectionRepository;
        this.incidentRepository = incidentRepository;
    }

    // --- Templates ---

    @Operation(summary = "List active checklist templates")
    @GetMapping("/safety/templates")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<TemplateResponse> listTemplates() {
        return templateRepository.findByActiveTrue().stream().map(TemplateResponse::from).toList();
    }

    @Operation(summary = "Create a checklist template")
    @PostMapping("/safety/templates")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    TemplateResponse createTemplate(@Valid @RequestBody CreateTemplateRequest req) {
        SafetyChecklistTemplate t = templateRepository.save(
                new SafetyChecklistTemplate(req.name(), req.category(), req.items()));
        return TemplateResponse.from(t);
    }

    // --- Inspections ---

    @Operation(summary = "List inspections for a budget")
    @GetMapping("/budgets/{budgetId}/safety/inspections")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<InspectionResponse> listInspections(@PathVariable UUID budgetId,
                                                     @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(inspectionRepository.findByBudgetId(budgetId, pageable).map(InspectionResponse::from));
    }

    @Operation(summary = "Record an inspection")
    @PostMapping("/budgets/{budgetId}/safety/inspections")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<InspectionResponse> recordInspection(@PathVariable UUID budgetId,
                                                        @Valid @RequestBody CreateInspectionRequest req) {
        SafetyChecklistTemplate template = templateRepository.findById(req.templateId())
                .orElseThrow(() -> new DomainNotFoundException("Template not found: " + req.templateId()));
        SafetyInspection inspection = inspectionRepository.save(
                new SafetyInspection(budgetId, template, req.inspector(), req.inspectionDate(),
                        req.status(), req.results(), req.notes()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/safety/inspections/" + inspection.getId()))
                .body(InspectionResponse.from(inspection));
    }

    // --- Incidents ---

    @Operation(summary = "List incidents for a budget")
    @GetMapping("/budgets/{budgetId}/safety/incidents")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<IncidentResponse> listIncidents(@PathVariable UUID budgetId,
                                                  @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(incidentRepository.findByBudgetId(budgetId, pageable).map(IncidentResponse::from));
    }

    @Operation(summary = "Report a safety incident")
    @PostMapping("/budgets/{budgetId}/safety/incidents")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    IncidentResponse reportIncident(@PathVariable UUID budgetId, @Valid @RequestBody CreateIncidentRequest req) {
        SafetyIncident incident = incidentRepository.save(new SafetyIncident(budgetId, req.incidentDate(),
                req.severity(), req.description(), req.location(), req.injuredParty(), req.reportedBy()));
        return IncidentResponse.from(incident);
    }

    @Operation(summary = "Resolve a safety incident")
    @PostMapping("/budgets/{budgetId}/safety/incidents/{id}/resolve")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    IncidentResponse resolveIncident(@PathVariable UUID budgetId, @PathVariable UUID id,
                                     @Valid @RequestBody ResolveIncidentRequest req) {
        SafetyIncident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Incident not found: " + id));
        incident.resolve(req.correctiveAction());
        return IncidentResponse.from(incidentRepository.save(incident));
    }

    // --- DTOs ---
    record CreateTemplateRequest(@NotBlank String name, @NotBlank String category, @NotBlank String items) {}
    record CreateInspectionRequest(@NotNull UUID templateId, @NotBlank String inspector,
                                   @NotNull LocalDate inspectionDate, @NotBlank String status,
                                   @NotBlank String results, String notes) {}
    record CreateIncidentRequest(@NotNull LocalDate incidentDate, @NotBlank String severity,
                                 @NotBlank String description, String location,
                                 String injuredParty, String reportedBy) {}
    record ResolveIncidentRequest(@NotBlank String correctiveAction) {}

    record TemplateResponse(UUID id, String name, String category, String items, Boolean active) {
        static TemplateResponse from(SafetyChecklistTemplate t) {
            return new TemplateResponse(t.getId(), t.getName(), t.getCategory(), t.getItems(), t.getActive());
        }
    }

    record InspectionResponse(UUID id, String templateName, String inspector, LocalDate inspectionDate,
                              String status, String results, String notes) {
        static InspectionResponse from(SafetyInspection i) {
            return new InspectionResponse(i.getId(), i.getTemplate().getName(), i.getInspector(),
                    i.getInspectionDate(), i.getStatus(), i.getResults(), i.getNotes());
        }
    }

    record IncidentResponse(UUID id, LocalDate incidentDate, String severity, String description,
                            String location, String injuredParty, String status, String correctiveAction) {
        static IncidentResponse from(SafetyIncident i) {
            return new IncidentResponse(i.getId(), i.getIncidentDate(), i.getSeverity(), i.getDescription(),
                    i.getLocation(), i.getInjuredParty(), i.getStatus(), i.getCorrectiveAction());
        }
    }
}
