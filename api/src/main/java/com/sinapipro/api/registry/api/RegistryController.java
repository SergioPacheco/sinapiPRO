package com.sinapipro.api.registry.api;

import com.sinapipro.api.registry.domain.*;
import com.sinapipro.api.shared.domain.ReportTemplate;
import com.sinapipro.api.shared.domain.ReportTemplateRepository;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Registry", description = "Auxiliary registrations: clients, employees, payment methods, bank accounts, units")
@RestController
@RequestMapping("/api/v1/registry")
public class RegistryController {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final UnitOfMeasureRepository unitRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ContractorRepository contractorRepository;
    private final InspectorRepository inspectorRepository;
    private final BdiTemplateRepository bdiTemplateRepository;
    private final SocialChargeRepository socialChargeRepository;
    private final PaymentConditionRepository paymentConditionRepository;
    private final CostCenterRepository costCenterRepository;
    private final FinanceCategoryRepository financeCategoryRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final DefaultStageRepository defaultStageRepository;
    private final IncidentTypeRepository incidentTypeRepository;
    private final EpiRepository epiRepository;
    private final ReportTemplateRepository reportTemplateRepository;

    public RegistryController(ClientRepository clientRepository, EmployeeRepository employeeRepository,
                              UnitOfMeasureRepository unitRepository, PaymentMethodRepository paymentMethodRepository,
                              BankAccountRepository bankAccountRepository, ContractorRepository contractorRepository,
                              InspectorRepository inspectorRepository, BdiTemplateRepository bdiTemplateRepository,
                              SocialChargeRepository socialChargeRepository, PaymentConditionRepository paymentConditionRepository,
                              CostCenterRepository costCenterRepository, FinanceCategoryRepository financeCategoryRepository,
                              ProjectTypeRepository projectTypeRepository, DefaultStageRepository defaultStageRepository,
                              IncidentTypeRepository incidentTypeRepository, EpiRepository epiRepository,
                              ReportTemplateRepository reportTemplateRepository) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.unitRepository = unitRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.contractorRepository = contractorRepository;
        this.inspectorRepository = inspectorRepository;
        this.bdiTemplateRepository = bdiTemplateRepository;
        this.socialChargeRepository = socialChargeRepository;
        this.paymentConditionRepository = paymentConditionRepository;
        this.costCenterRepository = costCenterRepository;
        this.financeCategoryRepository = financeCategoryRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.defaultStageRepository = defaultStageRepository;
        this.incidentTypeRepository = incidentTypeRepository;
        this.epiRepository = epiRepository;
        this.reportTemplateRepository = reportTemplateRepository;
    }

    // --- Clients ---

    @Operation(summary = "List active clients")
    @GetMapping("/clients")
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<ClientResponse> listClients(@RequestParam(required = false) String search,
                                             @PageableDefault(size = 20) Pageable pageable) {
        var page = search != null && !search.isBlank()
                ? clientRepository.findByActiveTrueAndNameContainingIgnoreCase(search.trim(), pageable)
                : clientRepository.findByActiveTrue(pageable);
        return PageResponse.from(page.map(ClientResponse::from));
    }

    @Operation(summary = "Create a client")
    @PostMapping("/clients")
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<ClientResponse> createClient(@Valid @RequestBody CreateClientRequest req) {
        var client = clientRepository.save(new Client(req.name(), req.document(), req.email(),
                req.phone(), req.address(), req.city(), req.state(), req.notes()));
        return ResponseEntity.created(URI.create("/api/v1/registry/clients/" + client.getId()))
                .body(ClientResponse.from(client));
    }

    @Operation(summary = "Deactivate a client")
    @DeleteMapping("/clients/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateClient(@PathVariable UUID id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Client not found: " + id));
        client.deactivate();
        clientRepository.save(client);
    }

    // --- Employees ---

    @Operation(summary = "List active employees")
    @GetMapping("/employees")
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<EmployeeResponse> listEmployees(@RequestParam(required = false) String type,
                                                  @RequestParam(required = false) String search,
                                                  @PageableDefault(size = 20) Pageable pageable) {
        var page = search != null && !search.isBlank()
                ? employeeRepository.findByActiveTrueAndNameContainingIgnoreCase(search.trim(), pageable)
                : type != null
                ? employeeRepository.findByActiveTrueAndType(type, pageable)
                : employeeRepository.findByActiveTrue(pageable);
        return PageResponse.from(page.map(EmployeeResponse::from));
    }

    @Operation(summary = "Get employee or contractor detail")
    @GetMapping("/employees/{id}")
    @PreAuthorize("@perm.check('registry.read')")
    EmployeeResponse getEmployee(@PathVariable UUID id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Employee not found: " + id));
        return EmployeeResponse.from(employee);
    }

    @Operation(summary = "Create an employee or contractor")
    @PostMapping("/employees")
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest req) {
        var employee = employeeRepository.save(new Employee(
                req.employeeCode(), req.name(), req.document(), req.role(), req.specialty(),
                req.type() != null ? req.type() : "EMPLOYEE",
                req.employmentStatus() != null ? req.employmentStatus() : "ACTIVE",
                req.email(), req.phone(), req.mobilePhone(), req.emergencyContactName(),
                req.emergencyContactPhone(), req.address(), req.city(), req.state(),
                req.postalCode(), req.costCenter(), req.companyName(), req.notes(),
                req.hourlyRate(), req.admissionDate(), req.terminationDate()));
        return ResponseEntity.created(URI.create("/api/v1/registry/employees/" + employee.getId()))
                .body(EmployeeResponse.from(employee));
    }

    @Operation(summary = "Update an employee or contractor")
    @PutMapping("/employees/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    EmployeeResponse updateEmployee(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest req) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Employee not found: " + id));
        employee.update(req.name(), req.document(), req.role(), req.specialty(),
                req.type() != null ? req.type() : employee.getType(),
                req.employmentStatus() != null ? req.employmentStatus() : employee.getEmploymentStatus(),
                req.email(), req.phone(), req.mobilePhone(), req.emergencyContactName(),
                req.emergencyContactPhone(), req.address(), req.city(), req.state(),
                req.postalCode(), req.costCenter(), req.companyName(), req.notes(),
                req.hourlyRate(), req.admissionDate(), req.terminationDate());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Operation(summary = "Deactivate an employee")
    @DeleteMapping("/employees/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateEmployee(@PathVariable UUID id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Employee not found: " + id));
        employee.deactivate();
        employeeRepository.save(employee);
    }

    // --- Units of Measure ---

    @Operation(summary = "List all units of measure")
    @GetMapping("/units")
    @PreAuthorize("@perm.check('registry.read')")
    List<UnitResponse> listUnits() {
        return unitRepository.findAll().stream().map(UnitResponse::from).toList();
    }

    @Operation(summary = "Create a unit of measure")
    @PostMapping("/units")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    UnitResponse createUnit(@Valid @RequestBody CreateUnitRequest req) {
        var unit = unitRepository.save(new UnitOfMeasure(req.symbol(), req.description()));
        return UnitResponse.from(unit);
    }

    // --- Payment Methods ---

    @Operation(summary = "List active payment methods")
    @GetMapping("/payment-methods")
    @PreAuthorize("@perm.check('registry.read')")
    List<PaymentMethodResponse> listPaymentMethods() {
        return paymentMethodRepository.findByActiveTrue().stream().map(PaymentMethodResponse::from).toList();
    }

    @Operation(summary = "Create a payment method")
    @PostMapping("/payment-methods")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    PaymentMethodResponse createPaymentMethod(@Valid @RequestBody CreatePaymentMethodRequest req) {
        var pm = paymentMethodRepository.save(new PaymentMethod(req.name(), req.installments() != null ? req.installments() : 1));
        return PaymentMethodResponse.from(pm);
    }

    // --- Bank Accounts ---

    @Operation(summary = "List active bank accounts")
    @GetMapping("/bank-accounts")
    @PreAuthorize("@perm.check('registry.read')")
    List<BankAccountResponse> listBankAccounts() {
        return bankAccountRepository.findByActiveTrue().stream().map(BankAccountResponse::from).toList();
    }

    @Operation(summary = "Create a bank account")
    @PostMapping("/bank-accounts")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    BankAccountResponse createBankAccount(@Valid @RequestBody CreateBankAccountRequest req) {
        var account = bankAccountRepository.save(new BankAccount(req.bankCode(), req.bankName(),
                req.agency(), req.accountNumber(), req.accountType() != null ? req.accountType() : "CHECKING", req.holderName()));
        return BankAccountResponse.from(account);
    }

    @Operation(summary = "Deactivate a bank account")
    @DeleteMapping("/bank-accounts/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateBankAccount(@PathVariable UUID id) {
        var account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Bank account not found: " + id));
        account.deactivate();
        bankAccountRepository.save(account);
    }

    // --- Contractors ---

    @GetMapping("/contractors")
    @PreAuthorize("@perm.check('registry.read')")
    List<ContractorResponse> listContractors() {
        return contractorRepository.findAll().stream().filter(Contractor::isActive).map(ContractorResponse::from).toList();
    }

    @PostMapping("/contractors")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ContractorResponse createContractor(@Valid @RequestBody CreateContractorRequest req) {
        return ContractorResponse.from(contractorRepository.save(new Contractor(req.name(), req.document(), req.specialty(), req.phone(), req.email(), req.city(), req.state())));
    }

    @PutMapping("/contractors/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    ContractorResponse updateContractor(@PathVariable UUID id, @Valid @RequestBody CreateContractorRequest req) {
        var e = contractorRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Contractor not found: " + id));
        e.update(req.name(), req.document(), req.specialty(), req.phone(), req.email(), req.city(), req.state());
        return ContractorResponse.from(contractorRepository.save(e));
    }

    @DeleteMapping("/contractors/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteContractor(@PathVariable UUID id) {
        var e = contractorRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Contractor not found: " + id));
        e.deactivate(); contractorRepository.save(e);
    }

    // --- Inspectors ---

    @GetMapping("/inspectors")
    @PreAuthorize("@perm.check('registry.read')")
    List<InspectorResponse> listInspectors() {
        return inspectorRepository.findAll().stream().filter(Inspector::isActive).map(InspectorResponse::from).toList();
    }

    @PostMapping("/inspectors")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    InspectorResponse createInspector(@Valid @RequestBody CreateInspectorRequest req) {
        return InspectorResponse.from(inspectorRepository.save(new Inspector(req.name(), req.document(), req.role(), req.organization(), req.phone(), req.email())));
    }

    @PutMapping("/inspectors/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    InspectorResponse updateInspector(@PathVariable UUID id, @Valid @RequestBody CreateInspectorRequest req) {
        var e = inspectorRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Inspector not found: " + id));
        e.update(req.name(), req.document(), req.role(), req.organization(), req.phone(), req.email());
        return InspectorResponse.from(inspectorRepository.save(e));
    }

    @DeleteMapping("/inspectors/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteInspector(@PathVariable UUID id) {
        var e = inspectorRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Inspector not found: " + id));
        e.deactivate(); inspectorRepository.save(e);
    }

    // --- BDI Templates ---

    @GetMapping("/bdi-templates")
    @PreAuthorize("@perm.check('registry.read')")
    List<BdiTemplateResponse> listBdiTemplates() {
        return bdiTemplateRepository.findAll().stream().map(BdiTemplateResponse::from).toList();
    }

    @PostMapping("/bdi-templates")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    BdiTemplateResponse createBdiTemplate(@Valid @RequestBody CreateBdiTemplateRequest req) {
        return BdiTemplateResponse.from(bdiTemplateRepository.save(new BdiTemplate(req.name(), req.administration(), req.profit(), req.financialCost(), req.taxes(), req.total())));
    }

    @PutMapping("/bdi-templates/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    BdiTemplateResponse updateBdiTemplate(@PathVariable UUID id, @Valid @RequestBody CreateBdiTemplateRequest req) {
        var e = bdiTemplateRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("BDI Template not found: " + id));
        e.update(req.name(), req.administration(), req.profit(), req.financialCost(), req.taxes(), req.total());
        return BdiTemplateResponse.from(bdiTemplateRepository.save(e));
    }

    @DeleteMapping("/bdi-templates/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteBdiTemplate(@PathVariable UUID id) { bdiTemplateRepository.deleteById(id); }

    // --- Social Charges ---

    @GetMapping("/social-charges")
    @PreAuthorize("@perm.check('registry.read')")
    List<SocialChargeResponse> listSocialCharges() {
        return socialChargeRepository.findAll().stream().map(SocialChargeResponse::from).toList();
    }

    @PostMapping("/social-charges")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    SocialChargeResponse createSocialCharge(@Valid @RequestBody CreateSocialChargeRequest req) {
        return SocialChargeResponse.from(socialChargeRepository.save(new SocialCharge(req.name(), req.type(), req.percentage())));
    }

    @PutMapping("/social-charges/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    SocialChargeResponse updateSocialCharge(@PathVariable UUID id, @Valid @RequestBody CreateSocialChargeRequest req) {
        var e = socialChargeRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Social Charge not found: " + id));
        e.update(req.name(), req.type(), req.percentage());
        return SocialChargeResponse.from(socialChargeRepository.save(e));
    }

    @DeleteMapping("/social-charges/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSocialCharge(@PathVariable UUID id) { socialChargeRepository.deleteById(id); }

    // --- Payment Conditions ---

    @GetMapping("/payment-conditions")
    @PreAuthorize("@perm.check('registry.read')")
    List<PaymentConditionResponse> listPaymentConditions() {
        return paymentConditionRepository.findAll().stream().filter(PaymentCondition::isActive).map(PaymentConditionResponse::from).toList();
    }

    @PostMapping("/payment-conditions")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    PaymentConditionResponse createPaymentCondition(@Valid @RequestBody CreatePaymentConditionRequest req) {
        return PaymentConditionResponse.from(paymentConditionRepository.save(new PaymentCondition(req.name(), req.installments() != null ? req.installments() : 1, req.description())));
    }

    @PutMapping("/payment-conditions/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    PaymentConditionResponse updatePaymentCondition(@PathVariable UUID id, @Valid @RequestBody CreatePaymentConditionRequest req) {
        var e = paymentConditionRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Payment Condition not found: " + id));
        e.update(req.name(), req.installments() != null ? req.installments() : 1, req.description());
        return PaymentConditionResponse.from(paymentConditionRepository.save(e));
    }

    @DeleteMapping("/payment-conditions/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletePaymentCondition(@PathVariable UUID id) {
        var e = paymentConditionRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Payment Condition not found: " + id));
        e.deactivate(); paymentConditionRepository.save(e);
    }

    // --- Cost Centers ---

    @GetMapping("/cost-centers")
    @PreAuthorize("@perm.check('registry.read')")
    List<CostCenterResponse> listCostCenters() {
        return costCenterRepository.findAll().stream().filter(CostCenter::isActive).map(CostCenterResponse::from).toList();
    }

    @PostMapping("/cost-centers")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    CostCenterResponse createCostCenter(@Valid @RequestBody CreateCostCenterRequest req) {
        return CostCenterResponse.from(costCenterRepository.save(new CostCenter(req.code(), req.name(), req.description())));
    }

    @PutMapping("/cost-centers/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    CostCenterResponse updateCostCenter(@PathVariable UUID id, @Valid @RequestBody CreateCostCenterRequest req) {
        var e = costCenterRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Cost Center not found: " + id));
        e.update(req.code(), req.name(), req.description());
        return CostCenterResponse.from(costCenterRepository.save(e));
    }

    @DeleteMapping("/cost-centers/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCostCenter(@PathVariable UUID id) {
        var e = costCenterRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Cost Center not found: " + id));
        e.deactivate(); costCenterRepository.save(e);
    }

    // --- Finance Categories ---

    @GetMapping("/finance-categories")
    @PreAuthorize("@perm.check('registry.read')")
    List<FinanceCategoryResponse> listFinanceCategories() {
        return financeCategoryRepository.findAll().stream().filter(FinanceCategory::isActive).map(FinanceCategoryResponse::from).toList();
    }

    @PostMapping("/finance-categories")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    FinanceCategoryResponse createFinanceCategory(@Valid @RequestBody CreateFinanceCategoryRequest req) {
        return FinanceCategoryResponse.from(financeCategoryRepository.save(new FinanceCategory(req.code(), req.name(), req.type())));
    }

    @PutMapping("/finance-categories/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    FinanceCategoryResponse updateFinanceCategory(@PathVariable UUID id, @Valid @RequestBody CreateFinanceCategoryRequest req) {
        var e = financeCategoryRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Finance Category not found: " + id));
        e.update(req.code(), req.name(), req.type());
        return FinanceCategoryResponse.from(financeCategoryRepository.save(e));
    }

    @DeleteMapping("/finance-categories/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteFinanceCategory(@PathVariable UUID id) {
        var e = financeCategoryRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Finance Category not found: " + id));
        e.deactivate(); financeCategoryRepository.save(e);
    }

    // --- Project Types ---

    @GetMapping("/project-types")
    @PreAuthorize("@perm.check('registry.read')")
    List<ProjectTypeResponse> listProjectTypes() {
        return projectTypeRepository.findAll().stream().map(ProjectTypeResponse::from).toList();
    }

    @PostMapping("/project-types")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ProjectTypeResponse createProjectType(@Valid @RequestBody CreateProjectTypeRequest req) {
        return ProjectTypeResponse.from(projectTypeRepository.save(new ProjectType(req.name(), req.description())));
    }

    @PutMapping("/project-types/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    ProjectTypeResponse updateProjectType(@PathVariable UUID id, @Valid @RequestBody CreateProjectTypeRequest req) {
        var e = projectTypeRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Project Type not found: " + id));
        e.update(req.name(), req.description());
        return ProjectTypeResponse.from(projectTypeRepository.save(e));
    }

    @DeleteMapping("/project-types/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProjectType(@PathVariable UUID id) { projectTypeRepository.deleteById(id); }

    // --- Default Stages ---

    @GetMapping("/default-stages")
    @PreAuthorize("@perm.check('registry.read')")
    List<DefaultStageResponse> listDefaultStages() {
        return defaultStageRepository.findAll().stream().sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder())).map(DefaultStageResponse::from).toList();
    }

    @PostMapping("/default-stages")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    DefaultStageResponse createDefaultStage(@Valid @RequestBody CreateDefaultStageRequest req) {
        return DefaultStageResponse.from(defaultStageRepository.save(new DefaultStage(req.name(), req.sortOrder() != null ? req.sortOrder() : 0, req.description())));
    }

    @PutMapping("/default-stages/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    DefaultStageResponse updateDefaultStage(@PathVariable UUID id, @Valid @RequestBody CreateDefaultStageRequest req) {
        var e = defaultStageRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Default Stage not found: " + id));
        e.update(req.name(), req.sortOrder() != null ? req.sortOrder() : 0, req.description());
        return DefaultStageResponse.from(defaultStageRepository.save(e));
    }

    @DeleteMapping("/default-stages/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDefaultStage(@PathVariable UUID id) { defaultStageRepository.deleteById(id); }

    // --- Incident Types ---

    @GetMapping("/incident-types")
    @PreAuthorize("@perm.check('registry.read')")
    List<IncidentTypeResponse> listIncidentTypes() {
        return incidentTypeRepository.findAll().stream().map(IncidentTypeResponse::from).toList();
    }

    @PostMapping("/incident-types")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    IncidentTypeResponse createIncidentType(@Valid @RequestBody CreateIncidentTypeRequest req) {
        return IncidentTypeResponse.from(incidentTypeRepository.save(new IncidentType(req.name(), req.severity(), req.description())));
    }

    @PutMapping("/incident-types/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    IncidentTypeResponse updateIncidentType(@PathVariable UUID id, @Valid @RequestBody CreateIncidentTypeRequest req) {
        var e = incidentTypeRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Incident Type not found: " + id));
        e.update(req.name(), req.severity(), req.description());
        return IncidentTypeResponse.from(incidentTypeRepository.save(e));
    }

    @DeleteMapping("/incident-types/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteIncidentType(@PathVariable UUID id) { incidentTypeRepository.deleteById(id); }

    // --- EPIs ---

    @GetMapping("/epis")
    @PreAuthorize("@perm.check('registry.read')")
    List<EpiResponse> listEpis() {
        return epiRepository.findAll().stream().map(EpiResponse::from).toList();
    }

    @PostMapping("/epis")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    EpiResponse createEpi(@Valid @RequestBody CreateEpiRequest req) {
        return EpiResponse.from(epiRepository.save(new Epi(req.name(), req.caNumber(), req.validityMonths(), req.description())));
    }

    @PutMapping("/epis/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    EpiResponse updateEpi(@PathVariable UUID id, @Valid @RequestBody CreateEpiRequest req) {
        var e = epiRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("EPI not found: " + id));
        e.update(req.name(), req.caNumber(), req.validityMonths(), req.description());
        return EpiResponse.from(epiRepository.save(e));
    }

    @DeleteMapping("/epis/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteEpi(@PathVariable UUID id) { epiRepository.deleteById(id); }

    // --- Report Templates ---

    @GetMapping("/report-templates")
    @PreAuthorize("@perm.check('registry.read')")
    List<ReportTemplateResponse> listReportTemplates() {
        return reportTemplateRepository.findAll().stream().map(ReportTemplateResponse::from).toList();
    }

    @PostMapping("/report-templates")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ReportTemplateResponse createReportTemplate(@Valid @RequestBody CreateReportTemplateRequest req) {
        return ReportTemplateResponse.from(reportTemplateRepository.save(new ReportTemplate(req.name(), req.type(), req.description())));
    }

    @PutMapping("/report-templates/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    ReportTemplateResponse updateReportTemplate(@PathVariable UUID id, @Valid @RequestBody CreateReportTemplateRequest req) {
        var e = reportTemplateRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Report Template not found: " + id));
        e.update(req.name(), req.type(), req.description());
        return ReportTemplateResponse.from(reportTemplateRepository.save(e));
    }

    @DeleteMapping("/report-templates/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteReportTemplate(@PathVariable UUID id) { reportTemplateRepository.deleteById(id); }

    // --- DTOs ---
    record CreateClientRequest(@NotBlank String name, String document, String email, String phone,
                               String address, String city, String state, String notes) {}
    record CreateEmployeeRequest(
            @NotBlank String employeeCode,
            @NotBlank String name,
            String document,
            @NotBlank String role,
            @NotBlank String specialty,
            String type,
            String employmentStatus,
            String email,
            String phone,
            String mobilePhone,
            String emergencyContactName,
            String emergencyContactPhone,
            String address,
            String city,
            String state,
            String postalCode,
            String costCenter,
            String companyName,
            String notes,
            BigDecimal hourlyRate,
            LocalDate admissionDate,
            LocalDate terminationDate) {}
    record UpdateEmployeeRequest(
            @NotBlank String name,
            String document,
            @NotBlank String role,
            @NotBlank String specialty,
            String type,
            String employmentStatus,
            String email,
            String phone,
            String mobilePhone,
            String emergencyContactName,
            String emergencyContactPhone,
            String address,
            String city,
            String state,
            String postalCode,
            String costCenter,
            String companyName,
            String notes,
            BigDecimal hourlyRate,
            LocalDate admissionDate,
            LocalDate terminationDate) {}
    record CreateUnitRequest(@NotBlank String symbol, @NotBlank String description) {}
    record CreatePaymentMethodRequest(@NotBlank String name, Integer installments) {}
    record CreateBankAccountRequest(@NotBlank String bankCode, @NotBlank String bankName, @NotBlank String agency,
                                    @NotBlank String accountNumber, String accountType, String holderName) {}

    record CreateContractorRequest(@NotBlank String name, String document, String specialty, String phone, String email, String city, String state) {}
    record CreateInspectorRequest(@NotBlank String name, String document, String role, String organization, String phone, String email) {}
    record CreateBdiTemplateRequest(@NotBlank String name, @NotNull BigDecimal administration, @NotNull BigDecimal profit, @NotNull BigDecimal financialCost, @NotNull BigDecimal taxes, @NotNull BigDecimal total) {}
    record CreateSocialChargeRequest(@NotBlank String name, @NotBlank String type, @NotNull BigDecimal percentage) {}
    record CreatePaymentConditionRequest(@NotBlank String name, Integer installments, String description) {}
    record CreateCostCenterRequest(@NotBlank String code, @NotBlank String name, String description) {}
    record CreateFinanceCategoryRequest(@NotBlank String code, @NotBlank String name, @NotBlank String type) {}
    record CreateProjectTypeRequest(@NotBlank String name, String description) {}
    record CreateDefaultStageRequest(@NotBlank String name, Integer sortOrder, String description) {}
    record CreateIncidentTypeRequest(@NotBlank String name, @NotBlank String severity, String description) {}
    record CreateEpiRequest(@NotBlank String name, String caNumber, Integer validityMonths, String description) {}
    record CreateReportTemplateRequest(@NotBlank String name, @NotBlank String type, String description) {}

    record ClientResponse(UUID id, String name, String document, String email, String phone,
                          String address, String city, String state,
                          String tradeName, String personType, String cellPhone, String whatsapp,
                          String website, String neighborhood, String postalCode,
                          String billingAddress, Integer preferredDueDay, boolean billingByEmail) {
        static ClientResponse from(Client c) {
            return new ClientResponse(c.getId(), c.getName(), c.getDocument(), c.getEmail(),
                    c.getPhone(), c.getAddress(), c.getCity(), c.getState(),
                    c.getTradeName(), c.getPersonType(), c.getCellPhone(), c.getWhatsapp(),
                    c.getWebsite(), c.getNeighborhood(), c.getPostalCode(),
                    c.getBillingAddress(), c.getPreferredDueDay(), c.isBillingByEmail());
        }
    }

    record EmployeeResponse(UUID id, String employeeCode, String name, String document, String role,
                            String specialty, String type, String employmentStatus, String email,
                            String phone, String mobilePhone, String emergencyContactName,
                            String emergencyContactPhone, String address, String city, String state,
                            String postalCode, String costCenter, String companyName, String notes,
                            BigDecimal hourlyRate, LocalDate admissionDate, LocalDate terminationDate) {
        static EmployeeResponse from(Employee e) {
            return new EmployeeResponse(e.getId(), e.getEmployeeCode(), e.getName(), e.getDocument(),
                    e.getRole(), e.getSpecialty(), e.getType(), e.getEmploymentStatus(),
                    e.getEmail(), e.getPhone(), e.getMobilePhone(), e.getEmergencyContactName(),
                    e.getEmergencyContactPhone(), e.getAddress(), e.getCity(), e.getState(),
                    e.getPostalCode(), e.getCostCenter(), e.getCompanyName(), e.getNotes(),
                    e.getHourlyRate(), e.getAdmissionDate(), e.getTerminationDate());
        }
    }

    record UnitResponse(UUID id, String symbol, String description) {
        static UnitResponse from(UnitOfMeasure u) { return new UnitResponse(u.getId(), u.getSymbol(), u.getDescription()); }
    }

    record PaymentMethodResponse(UUID id, String name, int installments) {
        static PaymentMethodResponse from(PaymentMethod pm) { return new PaymentMethodResponse(pm.getId(), pm.getName(), pm.getInstallments()); }
    }

    record BankAccountResponse(UUID id, String bankCode, String bankName, String agency,
                               String accountNumber, String accountType, String holderName) {
        static BankAccountResponse from(BankAccount a) {
            return new BankAccountResponse(a.getId(), a.getBankCode(), a.getBankName(), a.getAgency(),
                    a.getAccountNumber(), a.getAccountType(), a.getHolderName());
        }
    }

    record ContractorResponse(UUID id, String name, String document, String specialty, String phone, String email, String city, String state) {
        static ContractorResponse from(Contractor c) { return new ContractorResponse(c.getId(), c.getName(), c.getDocument(), c.getSpecialty(), c.getPhone(), c.getEmail(), c.getCity(), c.getState()); }
    }
    record InspectorResponse(UUID id, String name, String document, String role, String organization, String phone, String email) {
        static InspectorResponse from(Inspector i) { return new InspectorResponse(i.getId(), i.getName(), i.getDocument(), i.getRole(), i.getOrganization(), i.getPhone(), i.getEmail()); }
    }
    record BdiTemplateResponse(UUID id, String name, BigDecimal administration, BigDecimal profit, BigDecimal financialCost, BigDecimal taxes, BigDecimal total) {
        static BdiTemplateResponse from(BdiTemplate b) { return new BdiTemplateResponse(b.getId(), b.getName(), b.getAdministration(), b.getProfit(), b.getFinancialCost(), b.getTaxes(), b.getTotal()); }
    }
    record SocialChargeResponse(UUID id, String name, String type, BigDecimal percentage) {
        static SocialChargeResponse from(SocialCharge s) { return new SocialChargeResponse(s.getId(), s.getName(), s.getType(), s.getPercentage()); }
    }
    record PaymentConditionResponse(UUID id, String name, int installments, String description) {
        static PaymentConditionResponse from(PaymentCondition p) { return new PaymentConditionResponse(p.getId(), p.getName(), p.getInstallments(), p.getDescription()); }
    }
    record CostCenterResponse(UUID id, String code, String name, String description) {
        static CostCenterResponse from(CostCenter c) { return new CostCenterResponse(c.getId(), c.getCode(), c.getName(), c.getDescription()); }
    }
    record FinanceCategoryResponse(UUID id, String code, String name, String type) {
        static FinanceCategoryResponse from(FinanceCategory f) { return new FinanceCategoryResponse(f.getId(), f.getCode(), f.getName(), f.getType()); }
    }
    record ProjectTypeResponse(UUID id, String name, String description) {
        static ProjectTypeResponse from(ProjectType p) { return new ProjectTypeResponse(p.getId(), p.getName(), p.getDescription()); }
    }
    record DefaultStageResponse(UUID id, String name, int sortOrder, String description) {
        static DefaultStageResponse from(DefaultStage d) { return new DefaultStageResponse(d.getId(), d.getName(), d.getSortOrder(), d.getDescription()); }
    }
    record IncidentTypeResponse(UUID id, String name, String severity, String description) {
        static IncidentTypeResponse from(IncidentType i) { return new IncidentTypeResponse(i.getId(), i.getName(), i.getSeverity(), i.getDescription()); }
    }
    record EpiResponse(UUID id, String name, String caNumber, Integer validityMonths, String description) {
        static EpiResponse from(Epi e) { return new EpiResponse(e.getId(), e.getName(), e.getCaNumber(), e.getValidityMonths(), e.getDescription()); }
    }
    record ReportTemplateResponse(UUID id, String name, String type, String description) {
        static ReportTemplateResponse from(ReportTemplate r) { return new ReportTemplateResponse(r.getId(), r.getName(), r.getType(), r.getDescription()); }
    }
}
