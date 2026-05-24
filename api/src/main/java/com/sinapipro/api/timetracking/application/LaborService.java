package com.sinapipro.api.timetracking.application;

import com.sinapipro.api.timetracking.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LaborService {

    private final CompetencyPeriodRepository competencyRepository;
    private final HourBankRepository hourBankRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final LaborPriceTableRepository priceTableRepository;
    private final LaborPriceTableItemRepository priceTableItemRepository;

    public LaborService(CompetencyPeriodRepository competencyRepository,
                        HourBankRepository hourBankRepository,
                        SalaryHistoryRepository salaryHistoryRepository,
                        LaborPriceTableRepository priceTableRepository,
                        LaborPriceTableItemRepository priceTableItemRepository) {
        this.competencyRepository = competencyRepository;
        this.hourBankRepository = hourBankRepository;
        this.salaryHistoryRepository = salaryHistoryRepository;
        this.priceTableRepository = priceTableRepository;
        this.priceTableItemRepository = priceTableItemRepository;
    }

    // --- Competency Period ---

    public CompetencyPeriod openPeriod(UUID projectId, LocalDate yearMonth) {
        var existing = competencyRepository.findByProjectIdAndYearMonth(projectId, yearMonth);
        if (existing.isPresent()) throw new IllegalStateException("Period already exists for " + yearMonth);
        return competencyRepository.save(new CompetencyPeriod(projectId, yearMonth));
    }

    public CompetencyPeriod closePeriod(UUID periodId, String closedBy) {
        var period = findPeriod(periodId);
        if (!period.isOpen()) throw new IllegalStateException("Period already closed");
        period.close(closedBy);
        return competencyRepository.save(period);
    }

    public CompetencyPeriod reopenPeriod(UUID periodId) {
        var period = findPeriod(periodId);
        period.reopen();
        return competencyRepository.save(period);
    }

    public List<CompetencyPeriod> listPeriods(UUID projectId) {
        return competencyRepository.findByProjectIdOrderByYearMonthDesc(projectId);
    }

    // --- Hour Bank ---

    public HourBank addHours(UUID employeeId, UUID projectId, UUID competencyId,
                              String type, BigDecimal hours, String description, LocalDate referenceDate) {
        var period = findPeriod(competencyId);
        if (!period.isOpen()) throw new IllegalStateException("Cannot add hours to a closed period");
        return hourBankRepository.save(new HourBank(employeeId, projectId, competencyId, type, hours, description, referenceDate));
    }

    public BigDecimal getBalance(UUID employeeId, UUID projectId) {
        return hourBankRepository.getBalance(employeeId, projectId);
    }

    public List<HourBank> listByEmployee(UUID employeeId, UUID projectId) {
        return hourBankRepository.findByEmployeeIdAndProjectIdOrderByReferenceDateDesc(employeeId, projectId);
    }

    // --- Salary History ---

    public SalaryHistory addSalaryRecord(UUID employeeId, LocalDate effectiveDate,
                                          BigDecimal salary, BigDecimal hourlyRate, String reason) {
        return salaryHistoryRepository.save(new SalaryHistory(employeeId, effectiveDate, salary, hourlyRate, reason));
    }

    public List<SalaryHistory> getSalaryHistory(UUID employeeId) {
        return salaryHistoryRepository.findByEmployeeIdOrderByEffectiveDateDesc(employeeId);
    }

    // --- Labor Price Table ---

    public LaborPriceTable createPriceTable(String name, LocalDate validFrom) {
        return priceTableRepository.save(new LaborPriceTable(name, validFrom));
    }

    public LaborPriceTableItem addPriceTableItem(UUID tableId, String role, BigDecimal hourlyRate, BigDecimal monthlyRate) {
        return priceTableItemRepository.save(new LaborPriceTableItem(tableId, role, hourlyRate, monthlyRate));
    }

    public List<LaborPriceTable> listActiveTables() {
        return priceTableRepository.findByActiveTrue();
    }

    public List<LaborPriceTableItem> listTableItems(UUID tableId) {
        return priceTableItemRepository.findByTableId(tableId);
    }

    private CompetencyPeriod findPeriod(UUID id) {
        return competencyRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Competency period not found: " + id));
    }
}
