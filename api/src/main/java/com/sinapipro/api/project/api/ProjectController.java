package com.sinapipro.api.project.api;

import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import com.sinapipro.api.project.domain.ProjectStatus;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Projects", description = "Gestão de Obras")
@RestController
@RequestMapping("/api/v1/projects")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class ProjectController {

    private final ProjectRepository repository;

    public ProjectController(ProjectRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "List projects with optional search and status filter")
    @GetMapping
    PageResponse<ProjectResponse> list(@RequestParam(required = false) String q,
                                        @RequestParam(required = false) String status,
                                        @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(repository.findFiltered(q, status, pageable).map(ProjectResponse::from));
    }

    @Operation(summary = "Get project by ID")
    @GetMapping("/{id}")
    ProjectResponse findById(@PathVariable UUID id) {
        return ProjectResponse.from(findOrThrow(id));
    }

    @Operation(summary = "Create new project (obra)")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ProjectResponse create(@Valid @RequestBody CreateProjectRequest req) {
        if (repository.existsByCode(req.code())) {
            throw new IllegalStateException("Project code already exists: " + req.code());
        }
        var project = new Project(req.code(), req.name(), req.customerName());
        project.update(req.name(), req.description(), req.customerName(), req.customerDocument(),
                req.address(), req.city(), req.state(), req.responsibleEngineer(),
                req.artNumber(), req.startDate(), req.expectedEndDate(), req.totalArea(), req.totalBudget());
        return ProjectResponse.from(repository.save(project));
    }

    @Operation(summary = "Update project")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ProjectResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateProjectRequest req) {
        var project = findOrThrow(id);
        project.update(req.name(), req.description(), req.customerName(), req.customerDocument(),
                req.address(), req.city(), req.state(), req.responsibleEngineer(),
                req.artNumber(), req.startDate(), req.expectedEndDate(), req.totalArea(), req.totalBudget());
        return ProjectResponse.from(repository.save(project));
    }

    @Operation(summary = "Update project status")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ProjectResponse updateStatus(@PathVariable UUID id, @RequestBody StatusRequest req) {
        var project = findOrThrow(id);
        project.updateStatus(req.status());
        return ProjectResponse.from(repository.save(project));
    }

    @Operation(summary = "Delete project")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id) {
        repository.delete(findOrThrow(id));
    }

    private Project findOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new DomainNotFoundException("Project not found: " + id));
    }

    // DTOs
    public record CreateProjectRequest(
            @NotBlank String code, @NotBlank String name, @NotBlank String customerName,
            String description, String customerDocument, String address, String city, String state,
            String responsibleEngineer, String artNumber, LocalDate startDate, LocalDate expectedEndDate,
            BigDecimal totalArea, BigDecimal totalBudget) {}

    public record UpdateProjectRequest(
            @NotBlank String name, @NotBlank String customerName,
            String description, String customerDocument, String address, String city, String state,
            String responsibleEngineer, String artNumber, LocalDate startDate, LocalDate expectedEndDate,
            BigDecimal totalArea, BigDecimal totalBudget) {}

    public record StatusRequest(ProjectStatus status) {}

    public record ProjectResponse(UUID id, String code, String name, String description,
                                   String customerName, String customerDocument, String address,
                                   String city, String state, String responsibleEngineer, String artNumber,
                                   LocalDate startDate, LocalDate expectedEndDate, LocalDate actualEndDate,
                                   ProjectStatus status, BigDecimal totalArea, BigDecimal totalBudget,
                                   java.time.Instant createdAt) {
        public static ProjectResponse from(Project p) {
            return new ProjectResponse(p.getId(), p.getCode(), p.getName(), p.getDescription(),
                    p.getCustomerName(), p.getCustomerDocument(), p.getAddress(), p.getCity(), p.getState(),
                    p.getResponsibleEngineer(), p.getArtNumber(), p.getStartDate(), p.getExpectedEndDate(),
                    p.getActualEndDate(), p.getStatus(), p.getTotalArea(), p.getTotalBudget(), p.getCreatedAt());
        }
    }
}
