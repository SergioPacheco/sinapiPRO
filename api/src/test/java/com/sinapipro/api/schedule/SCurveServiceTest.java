package com.sinapipro.api.schedule;

import com.sinapipro.api.schedule.application.SCurveService;
import com.sinapipro.api.schedule.application.SCurveService.SCurveData;
import com.sinapipro.api.schedule.application.SCurveService.SCurvePoint;
import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SCurveServiceTest {

    @Mock ScheduleActivityRepository repository;

    SCurveService service;

    @BeforeEach
    void setUp() {
        service = new SCurveService(repository);
    }

    @Test
    @DisplayName("should return empty data when no activities")
    void shouldReturnEmptyWhenNoActivities() {
        UUID budgetId = UUID.randomUUID();
        when(repository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of());

        SCurveData result = service.calculate(budgetId);

        assertThat(result.points()).isEmpty();
    }

    @Test
    @DisplayName("should calculate planned cumulative progress over months")
    void shouldCalculatePlannedCumulative() {
        UUID budgetId = UUID.randomUUID();
        // Activity spanning 2 months: Jan 1 - Feb 28, weight 1.0
        ScheduleActivity activity = mockActivity(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 28),
                null, null, new BigDecimal("1.0000"), BigDecimal.ZERO);

        when(repository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(activity));

        SCurveData result = service.calculate(budgetId);

        assertThat(result.points()).hasSize(2);
        // First month (Jan): 31 days out of 59 total ≈ 0.5254
        assertThat(result.points().get(0).plannedCumulative()).isGreaterThan(BigDecimal.ZERO);
        // Second month (Feb): cumulative should reach ~1.0
        assertThat(result.points().get(1).plannedCumulative().compareTo(new BigDecimal("0.9")))
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("should calculate actual progress based on activity progressPct")
    void shouldCalculateActualProgress() {
        UUID budgetId = UUID.randomUUID();
        // Activity 50% complete, started
        ScheduleActivity activity = mockActivity(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 28),
                LocalDate.of(2026, 1, 5), null,
                new BigDecimal("1.0000"), new BigDecimal("50.00"));

        when(repository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(activity));

        SCurveData result = service.calculate(budgetId);

        assertThat(result.points()).isNotEmpty();
        // Actual cumulative should reflect 50% of weight
        SCurvePoint lastPoint = result.points().getLast();
        assertThat(lastPoint.actualCumulative()).isEqualByComparingTo("0.5000");
    }

    @Test
    @DisplayName("should handle multiple activities with different weights")
    void shouldHandleMultipleActivities() {
        UUID budgetId = UUID.randomUUID();
        ScheduleActivity a1 = mockActivity(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 1, 1), null,
                new BigDecimal("0.6000"), new BigDecimal("100.00"));
        ScheduleActivity a2 = mockActivity(
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 31),
                null, null,
                new BigDecimal("0.4000"), BigDecimal.ZERO);

        when(repository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(a1, a2));

        SCurveData result = service.calculate(budgetId);

        assertThat(result.points()).hasSizeGreaterThanOrEqualTo(3);
        // After first month, actual should be 0.6 (a1 is 100% done)
        assertThat(result.points().get(0).actualCumulative()).isEqualByComparingTo("0.6000");
    }

    private ScheduleActivity mockActivity(LocalDate plannedStart, LocalDate plannedEnd,
                                           LocalDate actualStart, LocalDate actualEnd,
                                           BigDecimal weight, BigDecimal progressPct) {
        ScheduleActivity a = mock(ScheduleActivity.class);
        when(a.getPlannedStart()).thenReturn(plannedStart);
        when(a.getPlannedEnd()).thenReturn(plannedEnd);
        lenient().when(a.getActualStart()).thenReturn(actualStart);
        lenient().when(a.getActualEnd()).thenReturn(actualEnd);
        when(a.getWeight()).thenReturn(weight);
        lenient().when(a.getProgressPct()).thenReturn(progressPct);
        return a;
    }
}
