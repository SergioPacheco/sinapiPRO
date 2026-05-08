package com.sinapipro.api.analytics;

import com.sinapipro.api.analytics.application.EarnedValueService;
import com.sinapipro.api.analytics.application.EarnedValueService.EvmResult;
import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EarnedValueServiceTest {

    @Mock ScheduleActivityRepository scheduleRepository;
    @Mock CostTransactionRepository costTransactionRepository;
    @Mock CostCodeRepository costCodeRepository;

    EarnedValueService service;

    @BeforeEach
    void setUp() {
        service = new EarnedValueService(scheduleRepository, costTransactionRepository, costCodeRepository);
    }

    @Test
    @DisplayName("should return empty result when no activities")
    void shouldReturnEmptyWhenNoActivities() {
        UUID budgetId = UUID.randomUUID();
        when(scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of());
        when(costCodeRepository.findByBudgetIdOrderByCode(budgetId)).thenReturn(List.of());

        EvmResult result = service.calculate(budgetId);

        assertThat(result.bac()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.cpi()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("should calculate CPI > 1 when under budget")
    void shouldCalculateCpiGreaterThanOneWhenUnderBudget() {
        UUID budgetId = UUID.randomUUID();
        ScheduleActivity activity = mockActivity(new BigDecimal("1.0000"), new BigDecimal("50.00"));
        when(scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(activity));
        // BAC = 100 from cost codes
        CostCode cc = mock(CostCode.class);
        when(cc.getBudgetedAmount()).thenReturn(new BigDecimal("100"));
        when(costCodeRepository.findByBudgetIdOrderByCode(budgetId)).thenReturn(List.of(cc));
        // AC = 40 (spent less than earned)
        when(costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL))
                .thenReturn(new BigDecimal("40.00"));

        EvmResult result = service.calculate(budgetId);

        // EV = 100 * 0.5 = 50, AC = 40, CPI = 50/40 = 1.25
        assertThat(result.ev()).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.cpi()).isGreaterThan(BigDecimal.ONE);
    }

    @Test
    @DisplayName("should calculate CPI < 1 when over budget")
    void shouldCalculateCpiLessThanOneWhenOverBudget() {
        UUID budgetId = UUID.randomUUID();
        ScheduleActivity activity = mockActivity(new BigDecimal("1.0000"), new BigDecimal("50.00"));
        when(scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(activity));
        CostCode cc = mock(CostCode.class);
        when(cc.getBudgetedAmount()).thenReturn(new BigDecimal("100"));
        when(costCodeRepository.findByBudgetIdOrderByCode(budgetId)).thenReturn(List.of(cc));
        when(costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL))
                .thenReturn(new BigDecimal("80.00"));

        EvmResult result = service.calculate(budgetId);

        assertThat(result.cpi()).isLessThan(BigDecimal.ONE);
    }

    @Test
    @DisplayName("should calculate SPI based on planned vs actual progress")
    void shouldCalculateSpi() {
        UUID budgetId = UUID.randomUUID();
        ScheduleActivity a1 = mockActivity(new BigDecimal("50.0000"), new BigDecimal("100.00"));
        ScheduleActivity a2 = mockActivity(new BigDecimal("50.0000"), BigDecimal.ZERO);
        when(scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(a1, a2));
        CostCode cc = mock(CostCode.class);
        when(cc.getBudgetedAmount()).thenReturn(new BigDecimal("200"));
        when(costCodeRepository.findByBudgetIdOrderByCode(budgetId)).thenReturn(List.of(cc));
        when(costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL))
                .thenReturn(new BigDecimal("100.00"));

        EvmResult result = service.calculate(budgetId);

        // BAC=200, progress=50%, EV=100, PV=200, SPI=100/200=0.5
        assertThat(result.bac()).isEqualByComparingTo("200");
        assertThat(result.spi()).isEqualByComparingTo("0.5000");
    }

    @Test
    @DisplayName("should calculate EAC and VAC")
    void shouldCalculateEacAndVac() {
        UUID budgetId = UUID.randomUUID();
        ScheduleActivity activity = mockActivity(new BigDecimal("1.0000"), new BigDecimal("50.00"));
        when(scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(activity));
        CostCode cc = mock(CostCode.class);
        when(cc.getBudgetedAmount()).thenReturn(new BigDecimal("200"));
        when(costCodeRepository.findByBudgetIdOrderByCode(budgetId)).thenReturn(List.of(cc));
        // AC = 120, EV = 100, CPI = 100/120 ≈ 0.8333
        when(costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL))
                .thenReturn(new BigDecimal("120.00"));

        EvmResult result = service.calculate(budgetId);

        // EAC = BAC / CPI = 200 / 0.8333 ≈ 240
        assertThat(result.eac()).isGreaterThan(result.bac());
        // VAC = BAC - EAC (negative = over budget forecast)
        assertThat(result.vac()).isNegative();
    }

    private ScheduleActivity mockActivity(BigDecimal weight, BigDecimal progressPct) {
        ScheduleActivity a = mock(ScheduleActivity.class);
        when(a.getWeight()).thenReturn(weight);
        when(a.getProgressPct()).thenReturn(progressPct);
        return a;
    }
}
