package com.sinapipro.api.registry.api;

import com.sinapipro.api.finance.domain.ChartOfAccounts;
import com.sinapipro.api.finance.domain.ChartOfAccountsRepository;
import com.sinapipro.api.registry.domain.*;
import com.sinapipro.api.shared.application.AuditService;
import com.sinapipro.api.shared.domain.AuditLog;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Advanced Registry", description = "Sprint 16: Transporters, sales reps, input categories, project settings, contacts, audit")
@RestController
@RequestMapping("/api/v1/registry")
public class AdvancedRegistryController {

    private final TransporterRepository transporterRepository;
    private final SalesRepresentativeRepository salesRepRepository;
    private final InputCategoryRepository inputCategoryRepository;
    private final ProjectSettingsRepository projectSettingsRepository;
    private final ContactInfoRepository contactInfoRepository;
    private final ChartOfAccountsRepository chartOfAccountsRepository;
    private final AuditService auditService;

    public AdvancedRegistryController(TransporterRepository transporterRepository,
                                      SalesRepresentativeRepository salesRepRepository,
                                      InputCategoryRepository inputCategoryRepository,
                                      ProjectSettingsRepository projectSettingsRepository,
                                      ContactInfoRepository contactInfoRepository,
                                      ChartOfAccountsRepository chartOfAccountsRepository,
                                      AuditService auditService) {
        this.transporterRepository = transporterRepository;
        this.salesRepRepository = salesRepRepository;
        this.inputCategoryRepository = inputCategoryRepository;
        this.projectSettingsRepository = projectSettingsRepository;
        this.contactInfoRepository = contactInfoRepository;
        this.chartOfAccountsRepository = chartOfAccountsRepository;
        this.auditService = auditService;
    }

    // ═══════════════════════════════════════════════════════════
    // 16.1 — Transportadores
    // ═══════════════════════════════════════════════════════════

    record TransporterRequest(@NotBlank String name, String document, String vehiclePlate,
                              String vehicleType, String phone, String cellPhone, String whatsapp,
                              String email, String address, String city, String state,
                              String postalCode, String notes) {}

    record TransporterResponse(UUID id, String name, String document, String vehiclePlate,
                               String vehicleType, String phone, String cellPhone, String whatsapp,
                               String email, String city, String state, boolean active) {
        static TransporterResponse from(Transporter t) {
            return new TransporterResponse(t.getId(), t.getName(), t.getDocument(), t.getVehiclePlate(),
                    t.getVehicleType(), t.getPhone(), t.getCellPhone(), t.getWhatsapp(),
                    t.getEmail(), t.getCity(), t.getState(), t.isActive());
        }
    }

    @Operation(summary = "List active transporters")
    @GetMapping("/transporters")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<TransporterResponse> listTransporters() {
        return transporterRepository.findAll().stream().filter(Transporter::isActive).map(TransporterResponse::from).toList();
    }

    @Operation(summary = "Create a transporter")
    @PostMapping("/transporters")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<TransporterResponse> createTransporter(@Valid @RequestBody TransporterRequest req) {
        var t = transporterRepository.save(new Transporter(req.name(), req.document(), req.vehiclePlate(),
                req.vehicleType(), req.phone(), req.cellPhone(), req.whatsapp(), req.email(),
                req.address(), req.city(), req.state(), req.postalCode(), req.notes()));
        auditService.log("TRANSPORTER", t.getId(), "CREATE", null);
        return ResponseEntity.created(URI.create("/api/v1/registry/transporters/" + t.getId()))
                .body(TransporterResponse.from(t));
    }

    @Operation(summary = "Update a transporter")
    @PutMapping("/transporters/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    TransporterResponse updateTransporter(@PathVariable UUID id, @Valid @RequestBody TransporterRequest req) {
        var t = transporterRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Transporter not found: " + id));
        t.update(req.name(), req.document(), req.vehiclePlate(), req.vehicleType(), req.phone(),
                req.cellPhone(), req.whatsapp(), req.email(), req.address(), req.city(), req.state(), req.postalCode(), req.notes());
        auditService.log("TRANSPORTER", id, "UPDATE", null);
        return TransporterResponse.from(transporterRepository.save(t));
    }

    @Operation(summary = "Deactivate a transporter")
    @DeleteMapping("/transporters/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTransporter(@PathVariable UUID id) {
        var t = transporterRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Transporter not found: " + id));
        t.deactivate(); transporterRepository.save(t);
        auditService.log("TRANSPORTER", id, "DELETE", null);
    }

    // ═══════════════════════════════════════════════════════════
    // 16.2 — Representantes comerciais
    // ═══════════════════════════════════════════════════════════

    record SalesRepRequest(@NotBlank String name, String document, String phone, String cellPhone,
                           String whatsapp, String email, BigDecimal commissionRate, String region, String notes) {}

    record SalesRepResponse(UUID id, String name, String document, String phone, String cellPhone,
                            String whatsapp, String email, BigDecimal commissionRate, String region, boolean active) {
        static SalesRepResponse from(SalesRepresentative r) {
            return new SalesRepResponse(r.getId(), r.getName(), r.getDocument(), r.getPhone(),
                    r.getCellPhone(), r.getWhatsapp(), r.getEmail(), r.getCommissionRate(), r.getRegion(), r.isActive());
        }
    }

    @Operation(summary = "List active sales representatives")
    @GetMapping("/sales-representatives")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<SalesRepResponse> listSalesReps() {
        return salesRepRepository.findAll().stream().filter(SalesRepresentative::isActive).map(SalesRepResponse::from).toList();
    }

    @Operation(summary = "Create a sales representative")
    @PostMapping("/sales-representatives")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<SalesRepResponse> createSalesRep(@Valid @RequestBody SalesRepRequest req) {
        var r = salesRepRepository.save(new SalesRepresentative(req.name(), req.document(), req.phone(),
                req.cellPhone(), req.whatsapp(), req.email(), req.commissionRate(), req.region(), req.notes()));
        auditService.log("SALES_REPRESENTATIVE", r.getId(), "CREATE", null);
        return ResponseEntity.created(URI.create("/api/v1/registry/sales-representatives/" + r.getId()))
                .body(SalesRepResponse.from(r));
    }

    @Operation(summary = "Update a sales representative")
    @PutMapping("/sales-representatives/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    SalesRepResponse updateSalesRep(@PathVariable UUID id, @Valid @RequestBody SalesRepRequest req) {
        var r = salesRepRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Sales representative not found: " + id));
        r.update(req.name(), req.document(), req.phone(), req.cellPhone(), req.whatsapp(),
                req.email(), req.commissionRate(), req.region(), req.notes());
        auditService.log("SALES_REPRESENTATIVE", id, "UPDATE", null);
        return SalesRepResponse.from(salesRepRepository.save(r));
    }

    @Operation(summary = "Deactivate a sales representative")
    @DeleteMapping("/sales-representatives/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSalesRep(@PathVariable UUID id) {
        var r = salesRepRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Sales representative not found: " + id));
        r.deactivate(); salesRepRepository.save(r);
        auditService.log("SALES_REPRESENTATIVE", id, "DELETE", null);
    }

    // ═══════════════════════════════════════════════════════════
    // 16.5 — Divisão/subdivisão de insumos
    // ═══════════════════════════════════════════════════════════

    record InputCategoryRequest(@NotBlank String code, @NotBlank String name, UUID parentId, int level) {}

    record InputCategoryResponse(UUID id, String code, String name, UUID parentId, int level, boolean active) {
        static InputCategoryResponse from(InputCategory c) {
            return new InputCategoryResponse(c.getId(), c.getCode(), c.getName(), c.getParentId(), c.getLevel(), c.isActive());
        }
    }

    @Operation(summary = "List root input categories (tree roots)")
    @GetMapping("/input-categories")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<InputCategoryResponse> listInputCategories(@RequestParam(required = false) UUID parentId) {
        var list = parentId != null
                ? inputCategoryRepository.findByParentIdAndActiveTrue(parentId)
                : inputCategoryRepository.findByParentIdIsNullAndActiveTrue();
        return list.stream().map(InputCategoryResponse::from).toList();
    }

    @Operation(summary = "Create an input category")
    @PostMapping("/input-categories")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<InputCategoryResponse> createInputCategory(@Valid @RequestBody InputCategoryRequest req) {
        var c = inputCategoryRepository.save(new InputCategory(req.code(), req.name(), req.parentId(), req.level()));
        return ResponseEntity.created(URI.create("/api/v1/registry/input-categories/" + c.getId()))
                .body(InputCategoryResponse.from(c));
    }

    @Operation(summary = "Update an input category")
    @PutMapping("/input-categories/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    InputCategoryResponse updateInputCategory(@PathVariable UUID id, @Valid @RequestBody InputCategoryRequest req) {
        var c = inputCategoryRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Input category not found: " + id));
        c.update(req.code(), req.name(), req.parentId(), req.level());
        return InputCategoryResponse.from(inputCategoryRepository.save(c));
    }

    @Operation(summary = "Deactivate an input category")
    @DeleteMapping("/input-categories/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteInputCategory(@PathVariable UUID id) {
        var c = inputCategoryRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Input category not found: " + id));
        c.deactivate(); inputCategoryRepository.save(c);
    }

    // ═══════════════════════════════════════════════════════════
    // 16.4 — Parâmetros por obra
    // ═══════════════════════════════════════════════════════════

    record ProjectSettingRequest(@NotBlank String key, String value, String description) {}

    record ProjectSettingResponse(UUID id, UUID projectId, String key, String value, String description) {
        static ProjectSettingResponse from(ProjectSettings s) {
            return new ProjectSettingResponse(s.getId(), s.getProjectId(), s.getKey(), s.getValue(), s.getDescription());
        }
    }

    @Operation(summary = "List settings for a project")
    @GetMapping("/projects/{projectId}/settings")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<ProjectSettingResponse> listProjectSettings(@PathVariable UUID projectId) {
        return projectSettingsRepository.findByProjectId(projectId).stream().map(ProjectSettingResponse::from).toList();
    }

    @Operation(summary = "Create or update a project setting")
    @PutMapping("/projects/{projectId}/settings")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ProjectSettingResponse upsertProjectSetting(@PathVariable UUID projectId, @Valid @RequestBody ProjectSettingRequest req) {
        var setting = projectSettingsRepository.findByProjectIdAndKey(projectId, req.key())
                .map(s -> { s.updateValue(req.value()); return s; })
                .orElseGet(() -> new ProjectSettings(projectId, req.key(), req.value(), req.description()));
        return ProjectSettingResponse.from(projectSettingsRepository.save(setting));
    }

    @Operation(summary = "Delete a project setting")
    @DeleteMapping("/projects/{projectId}/settings/{key}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProjectSetting(@PathVariable UUID projectId, @PathVariable String key) {
        projectSettingsRepository.findByProjectIdAndKey(projectId, key)
                .ifPresent(projectSettingsRepository::delete);
    }

    // ═══════════════════════════════════════════════════════════
    // 16.6 — Múltiplos telefones/endereços por pessoa
    // ═══════════════════════════════════════════════════════════

    record ContactInfoRequest(@NotBlank String infoType, @NotBlank String label, @NotBlank String value, boolean primary) {}

    record ContactInfoResponse(UUID id, String entityType, UUID entityId, String infoType, String label, String value, boolean primary) {
        static ContactInfoResponse from(ContactInfo c) {
            return new ContactInfoResponse(c.getId(), c.getEntityType(), c.getEntityId(), c.getInfoType(), c.getLabel(), c.getValue(), c.isPrimary());
        }
    }

    @Operation(summary = "List contact info for an entity (client, supplier, employee)")
    @GetMapping("/contacts/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<ContactInfoResponse> listContacts(@PathVariable String entityType, @PathVariable UUID entityId) {
        return contactInfoRepository.findByEntityTypeAndEntityId(entityType.toUpperCase(), entityId)
                .stream().map(ContactInfoResponse::from).toList();
    }

    @Operation(summary = "Add contact info to an entity")
    @PostMapping("/contacts/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<ContactInfoResponse> addContact(@PathVariable String entityType, @PathVariable UUID entityId,
                                                   @Valid @RequestBody ContactInfoRequest req) {
        var c = contactInfoRepository.save(new ContactInfo(entityType.toUpperCase(), entityId, req.infoType(), req.label(), req.value(), req.primary()));
        return ResponseEntity.created(URI.create("/api/v1/registry/contacts/" + entityType + "/" + entityId))
                .body(ContactInfoResponse.from(c));
    }

    @Operation(summary = "Update a contact info entry")
    @PutMapping("/contacts/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ContactInfoResponse updateContact(@PathVariable UUID id, @Valid @RequestBody ContactInfoRequest req) {
        var c = contactInfoRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Contact info not found: " + id));
        c.update(req.infoType(), req.label(), req.value(), req.primary());
        return ContactInfoResponse.from(contactInfoRepository.save(c));
    }

    @Operation(summary = "Delete a contact info entry")
    @DeleteMapping("/contacts/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteContact(@PathVariable UUID id) {
        contactInfoRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════
    // 16.8 — Audit trail (histórico de alterações)
    // ═══════════════════════════════════════════════════════════

    record AuditLogResponse(UUID id, String entityType, UUID entityId, String action, String changedBy,
                            java.time.Instant changedAt, String changes) {
        static AuditLogResponse from(AuditLog a) {
            return new AuditLogResponse(a.getId(), a.getEntityType(), a.getEntityId(), a.getAction(),
                    a.getChangedBy(), a.getChangedAt(), a.getChanges());
        }
    }

    @Operation(summary = "Get audit history for an entity")
    @GetMapping("/audit/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<AuditLogResponse> getAuditHistory(@PathVariable String entityType, @PathVariable UUID entityId,
                                           @PageableDefault(size = 50) Pageable pageable) {
        return auditService.getHistory(entityType.toUpperCase(), entityId, pageable)
                .getContent().stream().map(AuditLogResponse::from).toList();
    }

    // ═══════════════════════════════════════════════════════════
    // 16.3 — Plano de contas contábil (hierárquico)
    // ═══════════════════════════════════════════════════════════

    record ChartOfAccountsRequest(@NotBlank String code, @NotBlank String name, UUID parentId,
                                  @NotBlank String type, int level, String description, boolean acceptsEntries) {}

    record ChartOfAccountsResponse(UUID id, String code, String name, UUID parentId, String type,
                                   int level, String description, boolean acceptsEntries, boolean active) {
        static ChartOfAccountsResponse from(ChartOfAccounts c) {
            return new ChartOfAccountsResponse(c.getId(), c.getCode(), c.getName(), c.getParentId(),
                    c.getType(), c.getLevel(), c.getDescription(), c.isAcceptsEntries(), c.isActive());
        }
    }

    @Operation(summary = "List chart of accounts (tree roots or children)")
    @GetMapping("/chart-of-accounts")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<ChartOfAccountsResponse> listChartOfAccounts(@RequestParam(required = false) UUID parentId) {
        var list = parentId != null
                ? chartOfAccountsRepository.findByParentIdAndActiveTrue(parentId)
                : chartOfAccountsRepository.findByParentIdIsNullAndActiveTrue();
        return list.stream().map(ChartOfAccountsResponse::from).toList();
    }

    @Operation(summary = "Create a chart of accounts entry")
    @PostMapping("/chart-of-accounts")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<ChartOfAccountsResponse> createChartOfAccounts(@Valid @RequestBody ChartOfAccountsRequest req) {
        var c = chartOfAccountsRepository.save(new ChartOfAccounts(req.code(), req.name(), req.parentId(), req.type(), req.level()));
        c.update(req.code(), req.name(), req.parentId(), req.type(), req.level(), req.description(), req.acceptsEntries());
        c = chartOfAccountsRepository.save(c);
        return ResponseEntity.created(URI.create("/api/v1/registry/chart-of-accounts/" + c.getId()))
                .body(ChartOfAccountsResponse.from(c));
    }

    @Operation(summary = "Update a chart of accounts entry")
    @PutMapping("/chart-of-accounts/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ChartOfAccountsResponse updateChartOfAccounts(@PathVariable UUID id, @Valid @RequestBody ChartOfAccountsRequest req) {
        var c = chartOfAccountsRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Chart of accounts not found: " + id));
        c.update(req.code(), req.name(), req.parentId(), req.type(), req.level(), req.description(), req.acceptsEntries());
        return ChartOfAccountsResponse.from(chartOfAccountsRepository.save(c));
    }

    @Operation(summary = "Deactivate a chart of accounts entry")
    @DeleteMapping("/chart-of-accounts/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteChartOfAccounts(@PathVariable UUID id) {
        var c = chartOfAccountsRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Chart of accounts not found: " + id));
        c.deactivate(); chartOfAccountsRepository.save(c);
    }
}
