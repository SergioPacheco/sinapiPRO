package com.sinapipro.api.finance.api;

import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Finance Global", description = "Cross-project payables and receivables")
@RestController
@RequestMapping("/api/v1/finance")
public class GlobalFinanceController {

    private final PayableRepository payableRepository;
    private final ReceivableRepository receivableRepository;
    private final ProjectRepository projectRepository;

    public GlobalFinanceController(PayableRepository payableRepository, ReceivableRepository receivableRepository,
                                   ProjectRepository projectRepository) {
        this.payableRepository = payableRepository;
        this.receivableRepository = receivableRepository;
        this.projectRepository = projectRepository;
    }

    @Operation(summary = "List all payables across projects")
    @GetMapping("/payables")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<GlobalPayableResponse> listPayables(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) UUID supplierId,
            @RequestParam(required = false) LocalDate dueDateFrom,
            @RequestParam(required = false) LocalDate dueDateTo,
            @PageableDefault(size = 20) Pageable pageable) {

        Specification<Payable> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (supplierId != null) predicates.add(cb.equal(root.get("supplierId"), supplierId));
            if (dueDateFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
            if (dueDateTo != null) predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var page = payableRepository.findAll(spec, pageable);
        var projectNames = resolveProjectNames(page.getContent().stream().map(Payable::getProjectId).filter(java.util.Objects::nonNull).distinct().toList());

        return PageResponse.from(page.map(p -> new GlobalPayableResponse(
                p.getId(), p.getDescription(), p.getAmount(), p.getDueDate(),
                p.getStatus(), p.getCategory(), p.getSupplierId(),
                p.getProjectId(), p.getProjectId() != null ? projectNames.getOrDefault(p.getProjectId(), "") : null
        )));
    }

    @Operation(summary = "List all receivables across projects")
    @GetMapping("/receivables")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<GlobalReceivableResponse> listReceivables(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) LocalDate dueDateFrom,
            @RequestParam(required = false) LocalDate dueDateTo,
            @PageableDefault(size = 20) Pageable pageable) {

        Specification<Receivable> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (dueDateFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateFrom));
            if (dueDateTo != null) predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueDateTo));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var page = receivableRepository.findAll(spec, pageable);
        var projectNames = resolveProjectNames(page.getContent().stream().map(Receivable::getProjectId).filter(java.util.Objects::nonNull).distinct().toList());

        return PageResponse.from(page.map(r -> new GlobalReceivableResponse(
                r.getId(), r.getDescription(), r.getAmount(), r.getDueDate(),
                r.getStatus(), r.getCategory(),
                r.getProjectId(), r.getProjectId() != null ? projectNames.getOrDefault(r.getProjectId(), "") : null
        )));
    }

    private Map<UUID, String> resolveProjectNames(java.util.List<UUID> projectIds) {
        if (projectIds.isEmpty()) return Map.of();
        return projectRepository.findAllById(projectIds).stream().collect(Collectors.toMap(Project::getId, Project::getName));
    }

    record GlobalPayableResponse(UUID id, String description, BigDecimal amount, LocalDate dueDate,
                                 PaymentStatus status, String category, UUID supplierId,
                                 UUID projectId, String projectName) {}

    record GlobalReceivableResponse(UUID id, String description, BigDecimal amount, LocalDate dueDate,
                                    PaymentStatus status, String category,
                                    UUID projectId, String projectName) {}
}
