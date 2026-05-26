package com.sinapipro.api.timetracking.application;

import com.sinapipro.api.registry.domain.EmployeeEpiDelivery;
import com.sinapipro.api.registry.domain.EmployeeEpiDeliveryRepository;
import com.sinapipro.api.registry.domain.EmployeeRepository;
import com.sinapipro.api.shared.domain.TenantAwareEntity;
import com.sinapipro.api.timetracking.domain.*;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// --- 8.3 Entity: Absence (faltas, atestados, afastamentos) ---

@Entity @Table(name = "employee_absence")
class EmployeeAbsence extends TenantAwareEntity {
    @Column(name = "employee_id", nullable = false) private UUID employeeId;
    @Column(nullable = false, length = 30) private String type; // ABSENCE, MEDICAL, LEAVE, VACATION
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Column(length = 300) private String reason;
    @Column(name = "document_number", length = 50) private String documentNumber;
    @Column(nullable = false) private boolean justified = false;

    protected EmployeeAbsence() {}
    public EmployeeAbsence(UUID employeeId, String type, LocalDate startDate, LocalDate endDate, String reason, String documentNumber, boolean justified) {
        this.employeeId = employeeId; this.type = type; this.startDate = startDate; this.endDate = endDate;
        this.reason = reason; this.documentNumber = documentNumber; this.justified = justified;
    }
    public UUID getEmployeeId() { return employeeId; }
    public String getType() { return type; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getDocumentNumber() { return documentNumber; }
    public boolean isJustified() { return justified; }
    public int getDays() { return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1; }
}

interface EmployeeAbsenceRepository extends JpaRepository<EmployeeAbsence, UUID> {
    List<EmployeeAbsence> findByEmployeeIdOrderByStartDateDesc(UUID employeeId);
    List<EmployeeAbsence> findByStartDateBetween(LocalDate from, LocalDate to);
}

// --- Service ---

@Service @Transactional
public class LaborManagementService {

    private final EmployeeAbsenceRepository absenceRepo;
    private final TimesheetRepository timesheetRepo;
    private final EmployeeRepository employeeRepo;
    private final EmployeeEpiDeliveryRepository epiRepo;

    public LaborManagementService(EmployeeAbsenceRepository absenceRepo, TimesheetRepository timesheetRepo,
                                   EmployeeRepository employeeRepo, EmployeeEpiDeliveryRepository epiRepo) {
        this.absenceRepo = absenceRepo; this.timesheetRepo = timesheetRepo;
        this.employeeRepo = employeeRepo; this.epiRepo = epiRepo;
    }

    // ═══════════════════════════════════════════════════════════
    // 8.3 — Controle de faltas, atestados e afastamentos
    // ═══════════════════════════════════════════════════════════

    public EmployeeAbsence registerAbsence(UUID employeeId, String type, LocalDate startDate,
                                            LocalDate endDate, String reason, String documentNumber, boolean justified) {
        return absenceRepo.save(new EmployeeAbsence(employeeId, type, startDate, endDate, reason, documentNumber, justified));
    }

    public List<EmployeeAbsence> listAbsences(UUID employeeId) {
        return absenceRepo.findByEmployeeIdOrderByStartDateDesc(employeeId);
    }

    public AbsenceSummary absenceSummary(UUID employeeId, LocalDate from, LocalDate to) {
        var absences = absenceRepo.findByEmployeeIdOrderByStartDateDesc(employeeId).stream()
                .filter(a -> !a.getStartDate().isBefore(from) && !a.getEndDate().isAfter(to))
                .toList();
        var totalDays = absences.stream().mapToInt(EmployeeAbsence::getDays).sum();
        var justified = absences.stream().filter(EmployeeAbsence::isJustified).mapToInt(EmployeeAbsence::getDays).sum();
        return new AbsenceSummary(totalDays, justified, totalDays - justified, absences.size());
    }

    // ═══════════════════════════════════════════════════════════
    // 8.4 — Cálculo de encargos sociais por funcionário
    // ═══════════════════════════════════════════════════════════

    public SocialChargesResult calculateSocialCharges(UUID employeeId) {
        var employee = employeeRepo.findById(employeeId).orElseThrow();
        var salary = employee.getSalary() != null ? employee.getSalary() : BigDecimal.ZERO;

        // Encargos padrão CLT
        var inss = salary.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        var fgts = salary.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
        var thirteenth = salary.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        var vacation = salary.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("1.3333")).setScale(2, RoundingMode.HALF_UP);
        var total = inss.add(fgts).add(thirteenth).add(vacation);

        return new SocialChargesResult(employeeId, salary, inss, fgts, thirteenth, vacation, total);
    }

    // ═══════════════════════════════════════════════════════════
    // 8.5 — Apropriação de MO por centro de custo
    // ═══════════════════════════════════════════════════════════

    public List<CostCenterAllocation> laborByCostCenter(UUID budgetId) {
        var entries = timesheetRepo.findByBudgetId(budgetId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        return entries.stream()
                .collect(java.util.stream.Collectors.groupingBy(TimesheetEntry::getRole))
                .entrySet().stream()
                .map(e -> {
                    var hours = e.getValue().stream().map(TimesheetEntry::getTotalHours).reduce(BigDecimal.ZERO, BigDecimal::add);
                    var cost = e.getValue().stream().map(TimesheetEntry::getLaborCost).reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new CostCenterAllocation(e.getKey(), hours, cost, e.getValue().size());
                })
                .toList();
    }

    // ═══════════════════════════════════════════════════════════
    // 8.7 — Controle de EPI (validade)
    // ═══════════════════════════════════════════════════════════

    public List<EmployeeEpiDelivery> expiredEpis() {
        return epiRepo.findExpiring(LocalDate.of(2000, 1, 1), LocalDate.now());
    }

    // Records
    public record AbsenceSummary(int totalDays, int justifiedDays, int unjustifiedDays, int occurrences) {}
    public record SocialChargesResult(UUID employeeId, BigDecimal salary, BigDecimal inss, BigDecimal fgts,
                                       BigDecimal thirteenth, BigDecimal vacation, BigDecimal totalCharges) {}
    public record CostCenterAllocation(String costCenter, BigDecimal totalHours, BigDecimal totalCost, int entries) {}
}
