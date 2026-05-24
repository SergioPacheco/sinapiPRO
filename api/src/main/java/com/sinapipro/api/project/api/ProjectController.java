package com.sinapipro.api.project.api;

import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import com.sinapipro.api.project.domain.ProjectStatus;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.domain.ContractRegime;
import com.sinapipro.api.shared.domain.ProjectType;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Projects", description = "Gestão de Obras")
@RestController
@RequestMapping("/api/v1/projects")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class ProjectController {

    private final ProjectRepository repository;
    private final EntityManager em;

    public ProjectController(ProjectRepository repository, EntityManager em) {
        this.repository = repository;
        this.em = em;
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
                req.artNumber(), req.startDate(), req.expectedEndDate(), req.totalArea(), req.totalBudget(),
                req.clientId(), req.employeeId(), req.projectType(), req.contractRegime(),
                req.permitNumber(), req.permitExpiry(), req.ceiCno(), req.postalCode());
        return ProjectResponse.from(repository.save(project));
    }

    @Operation(summary = "Update project")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ProjectResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateProjectRequest req) {
        var project = findOrThrow(id);
        project.update(req.name(), req.description(), req.customerName(), req.customerDocument(),
                req.address(), req.city(), req.state(), req.responsibleEngineer(),
                req.artNumber(), req.startDate(), req.expectedEndDate(), req.totalArea(), req.totalBudget(),
                req.clientId(), req.employeeId(), req.projectType(), req.contractRegime(),
                req.permitNumber(), req.permitExpiry(), req.ceiCno(), req.postalCode());
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

    @Operation(summary = "Recent activity timeline for a project")
    @GetMapping("/{id}/timeline")
    List<ActivityEvent> timeline(@PathVariable UUID id) {
        findOrThrow(id);
        var events = new java.util.ArrayList<ActivityEvent>();

        // Recent measurements
        em.createQuery("SELECT m FROM Measurement m WHERE m.budget.projectId = :pid ORDER BY m.createdAt DESC", Object.class)
                .setParameter("pid", id).setMaxResults(5).getResultList().forEach(obj -> {
                    var m = (com.sinapipro.api.measurement.domain.Measurement) obj;
                    events.add(new ActivityEvent(m.getId(), "Medição #" + m.getNumber() + " — " + m.getStatus(),
                            "measurement", "#ff9800", m.getCreatedAt()));
                });

        // Recent daily logs
        em.createQuery("SELECT d FROM DailyLog d WHERE d.budget.projectId = :pid ORDER BY d.createdAt DESC", Object.class)
                .setParameter("pid", id).setMaxResults(3).getResultList().forEach(obj -> {
                    var d = (com.sinapipro.api.dailylog.domain.DailyLog) obj;
                    events.add(new ActivityEvent(d.getId(), "Diário de obra — " + d.getLogDate(),
                            "daily_log", "#4caf50", d.getCreatedAt()));
                });

        // Recent purchase orders
        em.createQuery("SELECT o FROM PurchaseOrder o WHERE o.budget.projectId = :pid ORDER BY o.createdAt DESC", Object.class)
                .setParameter("pid", id).setMaxResults(3).getResultList().forEach(obj -> {
                    var o = (com.sinapipro.api.procurement.domain.PurchaseOrder) obj;
                    events.add(new ActivityEvent(o.getId(), "Pedido " + o.getNumber() + " — " + o.getStatus(),
                            "procurement", "#2196f3", o.getCreatedAt()));
                });

        events.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
        return events.stream().limit(10).toList();
    }

    @Operation(summary = "Project process dashboard — phase checklist, module counts, next actions")
    @GetMapping("/{id}/dashboard")
    ProjectDashboard dashboard(@PathVariable UUID id) {
        findOrThrow(id);
        var budgets = countByProject("Budget", id);
        var contracts = countByProject("Contract", id);
        var activities = countByProject("ScheduleActivity", id);
        var measurements = countByProject("Measurement", id);
        var dailyLogs = countByProject("DailyLog", id);
        var purchaseOrders = countByProject("PurchaseOrder", id);
        var teams = ((Number) em.createQuery("SELECT COUNT(t) FROM Team t WHERE t.projectId = :pid")
                .setParameter("pid", id).getSingleResult()).longValue();
        var pendingMeasurements = ((Number) em.createQuery(
                "SELECT COUNT(m) FROM Measurement m WHERE m.budget.projectId = :pid AND m.status IN ('DRAFT','SUBMITTED')")
                .setParameter("pid", id).getSingleResult()).longValue();
        var pendingOrders = ((Number) em.createQuery(
                "SELECT COUNT(o) FROM PurchaseOrder o WHERE o.budget.projectId = :pid AND o.status = 'PENDING'")
                .setParameter("pid", id).getSingleResult()).longValue();

        var planning = new PhaseChecklist(
                budgets > 0, contracts > 0, activities > 0, teams > 0);
        var execution = new ExecutionSummary(
                dailyLogs, measurements, purchaseOrders, pendingMeasurements, pendingOrders);

        var nextActions = new java.util.ArrayList<NextAction>();
        if (budgets == 0) nextActions.add(new NextAction("create_budget", "Criar orçamento", "request_quote", "../budgets"));
        else if (activities == 0) nextActions.add(new NextAction("create_schedule", "Criar cronograma", "event_note", "../schedule"));
        else if (contracts == 0) nextActions.add(new NextAction("create_contract", "Criar contrato", "description", "../contracts"));
        if (pendingMeasurements > 0) nextActions.add(new NextAction("approve_measurement", "Aprovar medição pendente (" + pendingMeasurements + ")", "straighten", "../measurements"));
        if (pendingOrders > 0) nextActions.add(new NextAction("receive_order", "Receber pedido pendente (" + pendingOrders + ")", "shopping_cart", "../procurement"));
        if (dailyLogs == 0 && budgets > 0) nextActions.add(new NextAction("create_daily_log", "Preencher diário de obra", "edit_note", "../daily-logs"));

        return new ProjectDashboard(planning, execution, List.copyOf(nextActions));
    }

    private long countByProject(String entity, UUID projectId) {
        var jpql = switch (entity) {
            case "Contract", "ScheduleActivity", "Measurement", "DailyLog", "PurchaseOrder" ->
                    "SELECT COUNT(e) FROM " + entity + " e WHERE e.budget.projectId = :pid";
            default -> "SELECT COUNT(e) FROM " + entity + " e WHERE e.projectId = :pid";
        };
        return ((Number) em.createQuery(jpql).setParameter("pid", projectId).getSingleResult()).longValue();
    }

    private Project findOrThrow(UUID id) {
        return repository.findById(id).orElseThrow(() -> new DomainNotFoundException("Project not found: " + id));
    }

    // DTOs
    public record CreateProjectRequest(
            @NotBlank String code, @NotBlank String name, @NotBlank String customerName,
            String description, String customerDocument, String address, String city, String state,
            String responsibleEngineer, String artNumber, LocalDate startDate, LocalDate expectedEndDate,
            BigDecimal totalArea, BigDecimal totalBudget,
            UUID clientId, UUID employeeId, ProjectType projectType, ContractRegime contractRegime,
            String permitNumber, LocalDate permitExpiry, String ceiCno, String postalCode) {}

    public record UpdateProjectRequest(
            @NotBlank String name, @NotBlank String customerName,
            String description, String customerDocument, String address, String city, String state,
            String responsibleEngineer, String artNumber, LocalDate startDate, LocalDate expectedEndDate,
            BigDecimal totalArea, BigDecimal totalBudget,
            UUID clientId, UUID employeeId, ProjectType projectType, ContractRegime contractRegime,
            String permitNumber, LocalDate permitExpiry, String ceiCno, String postalCode) {}

    public record StatusRequest(ProjectStatus status) {}

    public record ProjectResponse(UUID id, String code, String name, String description,
                                   String customerName, String customerDocument, String address,
                                   String city, String state, String responsibleEngineer, String artNumber,
                                   LocalDate startDate, LocalDate expectedEndDate, LocalDate actualEndDate,
                                   ProjectStatus status, BigDecimal totalArea, BigDecimal totalBudget,
                                   UUID clientId, UUID employeeId, ProjectType projectType,
                                   ContractRegime contractRegime, String permitNumber,
                                   LocalDate permitExpiry, String ceiCno, String postalCode,
                                   String neighborhood, String addressNumber, String phone,
                                   BigDecimal totalBuiltArea, UUID developmentId, UUID branchId,
                                   String accountingCode, boolean financialControlEnabled,
                                   boolean stockControlEnabled, boolean budgetControlEnabled,
                                   boolean costApportionmentEnabled, BigDecimal apportionmentRate,
                                   BigDecimal purchaseLimitNoAuth, boolean billingToClient,
                                   java.time.Instant createdAt) {
        public static ProjectResponse from(Project p) {
            return new ProjectResponse(p.getId(), p.getCode(), p.getName(), p.getDescription(),
                    p.getCustomerName(), p.getCustomerDocument(), p.getAddress(), p.getCity(), p.getState(),
                    p.getResponsibleEngineer(), p.getArtNumber(), p.getStartDate(), p.getExpectedEndDate(),
                    p.getActualEndDate(), p.getStatus(), p.getTotalArea(), p.getTotalBudget(),
                    p.getClientId(), p.getEmployeeId(), p.getProjectType(), p.getContractRegime(),
                    p.getPermitNumber(), p.getPermitExpiry(), p.getCeiCno(), p.getPostalCode(),
                    p.getNeighborhood(), p.getAddressNumber(), p.getPhone(),
                    p.getTotalBuiltArea(), p.getDevelopmentId(), p.getBranchId(),
                    p.getAccountingCode(), p.isFinancialControlEnabled(),
                    p.isStockControlEnabled(), p.isBudgetControlEnabled(),
                    p.isCostApportionmentEnabled(), p.getApportionmentRate(),
                    p.getPurchaseLimitNoAuth(), p.isBillingToClient(),
                    p.getCreatedAt());
        }
    }

    // Dashboard DTOs
    public record PhaseChecklist(boolean hasBudget, boolean hasContract, boolean hasSchedule, boolean hasTeam) {}
    public record ExecutionSummary(long dailyLogs, long measurements, long purchaseOrders,
                                    long pendingMeasurements, long pendingOrders) {}
    public record NextAction(String id, String label, String icon, String route) {}
    public record ProjectDashboard(PhaseChecklist planning, ExecutionSummary execution, List<NextAction> nextActions) {}
    public record ActivityEvent(UUID id, String description, String type, String color, java.time.Instant createdAt) {}
}
