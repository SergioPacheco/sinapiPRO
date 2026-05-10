package com.sinapipro.api.registry.api;

import com.sinapipro.api.registry.domain.*;
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

    public RegistryController(ClientRepository clientRepository, EmployeeRepository employeeRepository,
                              UnitOfMeasureRepository unitRepository, PaymentMethodRepository paymentMethodRepository,
                              BankAccountRepository bankAccountRepository) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.unitRepository = unitRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    // --- Clients ---

    @Operation(summary = "List active clients")
    @GetMapping("/clients")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<ClientResponse> listClients(@PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(clientRepository.findByActiveTrue(pageable).map(ClientResponse::from));
    }

    @Operation(summary = "Create a client")
    @PostMapping("/clients")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<ClientResponse> createClient(@Valid @RequestBody CreateClientRequest req) {
        var client = clientRepository.save(new Client(req.name(), req.document(), req.email(),
                req.phone(), req.address(), req.city(), req.state(), req.notes()));
        return ResponseEntity.created(URI.create("/api/v1/registry/clients/" + client.getId()))
                .body(ClientResponse.from(client));
    }

    @Operation(summary = "Deactivate a client")
    @DeleteMapping("/clients/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
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
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<EmployeeResponse> listEmployees(@RequestParam(required = false) String type,
                                                  @PageableDefault(size = 20) Pageable pageable) {
        var page = type != null
                ? employeeRepository.findByActiveTrueAndType(type, pageable)
                : employeeRepository.findByActiveTrue(pageable);
        return PageResponse.from(page.map(EmployeeResponse::from));
    }

    @Operation(summary = "Create an employee or contractor")
    @PostMapping("/employees")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest req) {
        var employee = employeeRepository.save(new Employee(req.name(), req.document(), req.role(),
                req.type() != null ? req.type() : "EMPLOYEE", req.email(), req.phone(), req.hourlyRate(), req.admissionDate()));
        return ResponseEntity.created(URI.create("/api/v1/registry/employees/" + employee.getId()))
                .body(EmployeeResponse.from(employee));
    }

    @Operation(summary = "Deactivate an employee")
    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
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
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<UnitResponse> listUnits() {
        return unitRepository.findAll().stream().map(UnitResponse::from).toList();
    }

    @Operation(summary = "Create a unit of measure")
    @PostMapping("/units")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    UnitResponse createUnit(@Valid @RequestBody CreateUnitRequest req) {
        var unit = unitRepository.save(new UnitOfMeasure(req.symbol(), req.description()));
        return UnitResponse.from(unit);
    }

    // --- Payment Methods ---

    @Operation(summary = "List active payment methods")
    @GetMapping("/payment-methods")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<PaymentMethodResponse> listPaymentMethods() {
        return paymentMethodRepository.findByActiveTrue().stream().map(PaymentMethodResponse::from).toList();
    }

    @Operation(summary = "Create a payment method")
    @PostMapping("/payment-methods")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    PaymentMethodResponse createPaymentMethod(@Valid @RequestBody CreatePaymentMethodRequest req) {
        var pm = paymentMethodRepository.save(new PaymentMethod(req.name(), req.installments() != null ? req.installments() : 1));
        return PaymentMethodResponse.from(pm);
    }

    // --- Bank Accounts ---

    @Operation(summary = "List active bank accounts")
    @GetMapping("/bank-accounts")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<BankAccountResponse> listBankAccounts() {
        return bankAccountRepository.findByActiveTrue().stream().map(BankAccountResponse::from).toList();
    }

    @Operation(summary = "Create a bank account")
    @PostMapping("/bank-accounts")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    BankAccountResponse createBankAccount(@Valid @RequestBody CreateBankAccountRequest req) {
        var account = bankAccountRepository.save(new BankAccount(req.bankCode(), req.bankName(),
                req.agency(), req.accountNumber(), req.accountType() != null ? req.accountType() : "CHECKING", req.holderName()));
        return BankAccountResponse.from(account);
    }

    @Operation(summary = "Deactivate a bank account")
    @DeleteMapping("/bank-accounts/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateBankAccount(@PathVariable UUID id) {
        var account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Bank account not found: " + id));
        account.deactivate();
        bankAccountRepository.save(account);
    }

    // --- DTOs ---
    record CreateClientRequest(@NotBlank String name, String document, String email, String phone,
                               String address, String city, String state, String notes) {}
    record CreateEmployeeRequest(@NotBlank String name, String document, @NotBlank String role, String type,
                                 String email, String phone, BigDecimal hourlyRate, LocalDate admissionDate) {}
    record CreateUnitRequest(@NotBlank String symbol, @NotBlank String description) {}
    record CreatePaymentMethodRequest(@NotBlank String name, Integer installments) {}
    record CreateBankAccountRequest(@NotBlank String bankCode, @NotBlank String bankName, @NotBlank String agency,
                                    @NotBlank String accountNumber, String accountType, String holderName) {}

    record ClientResponse(UUID id, String name, String document, String email, String phone,
                          String address, String city, String state) {
        static ClientResponse from(Client c) {
            return new ClientResponse(c.getId(), c.getName(), c.getDocument(), c.getEmail(),
                    c.getPhone(), c.getAddress(), c.getCity(), c.getState());
        }
    }

    record EmployeeResponse(UUID id, String name, String document, String role, String type,
                            String email, String phone, BigDecimal hourlyRate, LocalDate admissionDate) {
        static EmployeeResponse from(Employee e) {
            return new EmployeeResponse(e.getId(), e.getName(), e.getDocument(), e.getRole(), e.getType(),
                    e.getEmail(), e.getPhone(), e.getHourlyRate(), e.getAdmissionDate());
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
}
