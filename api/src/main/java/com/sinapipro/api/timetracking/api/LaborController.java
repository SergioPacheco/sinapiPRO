package com.sinapipro.api.timetracking.api;

import com.sinapipro.api.timetracking.application.LaborService;
import com.sinapipro.api.timetracking.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Labor", description = "Mão de obra: competência, banco de horas, salários, tabela de preços")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/labor")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class LaborController {

    private final LaborService service;

    public LaborController(LaborService service) { this.service = service; }

    // --- Competency Periods ---

    @GetMapping("/competency-periods")
    List<PeriodResponse> listPeriods(@PathVariable UUID projectId) {
        return service.listPeriods(projectId).stream().map(PeriodResponse::from).toList();
    }

    @PostMapping("/competency-periods")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    PeriodResponse openPeriod(@PathVariable UUID projectId, @Valid @RequestBody OpenPeriodRequest req) {
        return PeriodResponse.from(service.openPeriod(projectId, req.yearMonth()));
    }

    @PostMapping("/competency-periods/{periodId}/close")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    PeriodResponse closePeriod(@PathVariable UUID projectId, @PathVariable UUID periodId,
                                @RequestBody CloseRequest req) {
        return PeriodResponse.from(service.closePeriod(periodId, req.closedBy()));
    }

    @PostMapping("/competency-periods/{periodId}/reopen")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    PeriodResponse reopenPeriod(@PathVariable UUID projectId, @PathVariable UUID periodId) {
        return PeriodResponse.from(service.reopenPeriod(periodId));
    }

    // --- Hour Bank ---

    @GetMapping("/hour-bank/{employeeId}")
    List<HourBankResponse> listHourBank(@PathVariable UUID projectId, @PathVariable UUID employeeId) {
        return service.listByEmployee(employeeId, projectId).stream().map(HourBankResponse::from).toList();
    }

    @GetMapping("/hour-bank/{employeeId}/balance")
    BalanceResponse getBalance(@PathVariable UUID projectId, @PathVariable UUID employeeId) {
        return new BalanceResponse(service.getBalance(employeeId, projectId));
    }

    @PostMapping("/hour-bank")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    HourBankResponse addHours(@PathVariable UUID projectId, @Valid @RequestBody AddHoursRequest req) {
        var entry = service.addHours(req.employeeId(), projectId, req.competencyId(),
                req.type(), req.hours(), req.description(), req.referenceDate());
        return HourBankResponse.from(entry);
    }

    // --- Salary History ---

    @Operation(summary = "Get salary history for an employee")
    @GetMapping("/salary-history/{employeeId}")
    List<SalaryResponse> salaryHistory(@PathVariable UUID projectId, @PathVariable UUID employeeId) {
        return service.getSalaryHistory(employeeId).stream().map(SalaryResponse::from).toList();
    }

    @PostMapping("/salary-history")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    SalaryResponse addSalary(@PathVariable UUID projectId, @Valid @RequestBody AddSalaryRequest req) {
        return SalaryResponse.from(service.addSalaryRecord(req.employeeId(), req.effectiveDate(),
                req.salary(), req.hourlyRate(), req.reason()));
    }

    // --- Price Tables ---

    @GetMapping("/price-tables")
    List<PriceTableResponse> listPriceTables(@PathVariable UUID projectId) {
        return service.listActiveTables().stream().map(PriceTableResponse::from).toList();
    }

    @PostMapping("/price-tables")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    PriceTableResponse createPriceTable(@PathVariable UUID projectId, @Valid @RequestBody CreateTableRequest req) {
        return PriceTableResponse.from(service.createPriceTable(req.name(), req.validFrom()));
    }

    @GetMapping("/price-tables/{tableId}/items")
    List<TableItemResponse> listTableItems(@PathVariable UUID projectId, @PathVariable UUID tableId) {
        return service.listTableItems(tableId).stream().map(TableItemResponse::from).toList();
    }

    @PostMapping("/price-tables/{tableId}/items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    TableItemResponse addTableItem(@PathVariable UUID projectId, @PathVariable UUID tableId,
                                    @Valid @RequestBody AddTableItemRequest req) {
        return TableItemResponse.from(service.addPriceTableItem(tableId, req.role(), req.hourlyRate(), req.monthlyRate()));
    }

    // DTOs
    record OpenPeriodRequest(@NotNull LocalDate yearMonth) {}
    record CloseRequest(String closedBy) {}
    record AddHoursRequest(@NotNull UUID employeeId, @NotNull UUID competencyId, @NotBlank String type,
                            @NotNull BigDecimal hours, String description, @NotNull LocalDate referenceDate) {}
    record AddSalaryRequest(@NotNull UUID employeeId, @NotNull LocalDate effectiveDate,
                             @NotNull BigDecimal salary, @NotNull BigDecimal hourlyRate, String reason) {}
    record CreateTableRequest(@NotBlank String name, @NotNull LocalDate validFrom) {}
    record AddTableItemRequest(@NotBlank String role, @NotNull BigDecimal hourlyRate, BigDecimal monthlyRate) {}

    record PeriodResponse(UUID id, UUID projectId, LocalDate yearMonth, String status) {
        static PeriodResponse from(CompetencyPeriod p) { return new PeriodResponse(p.getId(), p.getProjectId(), p.getYearMonth(), p.getStatus()); }
    }
    record HourBankResponse(UUID id, UUID employeeId, String type, BigDecimal hours, String description, LocalDate referenceDate) {
        static HourBankResponse from(HourBank h) { return new HourBankResponse(h.getId(), h.getEmployeeId(), h.getType(), h.getHours(), h.getDescription(), h.getReferenceDate()); }
    }
    record BalanceResponse(BigDecimal balance) {}
    record SalaryResponse(UUID id, UUID employeeId, LocalDate effectiveDate, BigDecimal salary, BigDecimal hourlyRate, String reason) {
        static SalaryResponse from(SalaryHistory s) { return new SalaryResponse(s.getId(), s.getEmployeeId(), s.getEffectiveDate(), s.getSalary(), s.getHourlyRate(), s.getReason()); }
    }
    record PriceTableResponse(UUID id, String name, LocalDate validFrom, boolean active) {
        static PriceTableResponse from(LaborPriceTable t) { return new PriceTableResponse(t.getId(), t.getName(), t.getValidFrom(), t.isActive()); }
    }
    record TableItemResponse(UUID id, String role, BigDecimal hourlyRate, BigDecimal monthlyRate) {
        static TableItemResponse from(LaborPriceTableItem i) { return new TableItemResponse(i.getId(), i.getRole(), i.getHourlyRate(), i.getMonthlyRate()); }
    }
}
