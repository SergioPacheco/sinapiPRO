package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.budget.domain.BudgetStatus;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Budgets Global", description = "Cross-project budget listing")
@RestController
@RequestMapping("/api/v1/budgets")
public class GlobalBudgetController {

    private final BudgetRepository budgetRepository;
    private final ProjectRepository projectRepository;

    public GlobalBudgetController(BudgetRepository budgetRepository, ProjectRepository projectRepository) {
        this.budgetRepository = budgetRepository;
        this.projectRepository = projectRepository;
    }

    @Operation(summary = "List all budgets across projects with filters")
    @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    PageResponse<GlobalBudgetResponse> listAll(
            @RequestParam(required = false) BudgetStatus status,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {

        Specification<Budget> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (projectId != null) predicates.add(cb.equal(root.get("projectId"), projectId));
            if (search != null && !search.isBlank()) {
                var pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), pattern),
                        cb.like(cb.lower(root.get("title")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var page = budgetRepository.findAll(spec, pageable);
        var projectIds = page.getContent().stream().map(Budget::getProjectId).filter(java.util.Objects::nonNull).distinct().toList();
        var projectNames = projectIds.isEmpty() ? Map.<UUID, String>of() :
                projectRepository.findAllById(projectIds).stream().collect(Collectors.toMap(Project::getId, Project::getName));

        return PageResponse.from(page.map(b -> new GlobalBudgetResponse(
                b.getId(), b.getCode(), b.getTitle(), b.getCustomerName(),
                b.getTotalAmount(), b.getStatus(), b.isActive(),
                b.getProjectId(), b.getProjectId() != null ? projectNames.getOrDefault(b.getProjectId(), "") : null
        )));
    }

    record GlobalBudgetResponse(UUID id, String code, String title, String customerName,
                                BigDecimal totalAmount, BudgetStatus status, boolean active,
                                UUID projectId, String projectName) {}
}
