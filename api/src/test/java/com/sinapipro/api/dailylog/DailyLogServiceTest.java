package com.sinapipro.api.dailylog;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.dailylog.application.DailyLogService;
import com.sinapipro.api.dailylog.application.DailyLogService.*;
import com.sinapipro.api.dailylog.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyLogServiceTest {

    @Mock DailyLogRepository dailyLogRepository;
    @Mock BudgetRepository budgetRepository;

    DailyLogService service;

    @BeforeEach
    void setUp() {
        service = new DailyLogService(dailyLogRepository, budgetRepository);
    }

    @Test
    @DisplayName("should create daily log for today")
    void shouldCreateForToday() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(dailyLogRepository.findByBudgetIdOrderByLogDateDesc(budgetId)).thenReturn(List.of());
        when(dailyLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DailyLog result = service.create(budgetId, LocalDate.now(), "Sunny", "Cloudy", "Normal day",
                List.of(new LaborInput("John", "Mason", new BigDecimal("8"))),
                List.of(new EquipmentInput("Excavator", new BigDecimal("4"), new BigDecimal("2"))),
                List.of(new OccurrenceInput("VISIT", "Engineer visit")));

        assertThat(result.getLogDate()).isEqualTo(LocalDate.now());
        assertThat(result.getLaborEntries()).hasSize(1);
        assertThat(result.getEquipmentEntries()).hasSize(1);
        assertThat(result.getOccurrences()).hasSize(1);
    }

    @Test
    @DisplayName("should reject future dates")
    void shouldRejectFutureDate() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        assertThatThrownBy(() -> service.create(budgetId, LocalDate.now().plusDays(1),
                null, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");
    }

    @Test
    @DisplayName("should reject dates older than 7 days")
    void shouldRejectOldDates() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        assertThatThrownBy(() -> service.create(budgetId, LocalDate.now().minusDays(8),
                null, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("older than 7 days");
    }

    @Test
    @DisplayName("should reject duplicate date for same budget")
    void shouldRejectDuplicateDate() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        DailyLog existing = mock(DailyLog.class);
        when(existing.getLogDate()).thenReturn(LocalDate.now());
        when(dailyLogRepository.findByBudgetIdOrderByLogDateDesc(budgetId)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.create(budgetId, LocalDate.now(),
                null, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("should calculate summary totals")
    void shouldCalculateSummary() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);

        DailyLog log = new DailyLog(budget, LocalDate.now(), null, null, null);
        log.getLaborEntries().add(new DailyLogLabor(log, "John", "Mason", new BigDecimal("8")));
        log.getLaborEntries().add(new DailyLogLabor(log, "Jane", "Electrician", new BigDecimal("6")));
        log.getEquipmentEntries().add(new DailyLogEquipment(log, "Excavator", new BigDecimal("4"), new BigDecimal("2")));
        log.getOccurrences().add(new DailyLogOccurrence(log, "RAIN", "Light rain afternoon"));

        when(dailyLogRepository.findByBudgetIdOrderByLogDateDesc(budgetId)).thenReturn(List.of(log));

        DailyLogSummary summary = service.summary(budgetId);

        assertThat(summary.totalLogs()).isEqualTo(1);
        assertThat(summary.totalLaborHours()).isEqualByComparingTo("14");
        assertThat(summary.totalEquipmentHours()).isEqualByComparingTo("4");
        assertThat(summary.totalOccurrences()).isEqualTo(1);
    }
}
