package com.sinapipro.api.schedule;

import com.sinapipro.api.schedule.application.CriticalPathService;
import com.sinapipro.api.schedule.application.CriticalPathService.CriticalActivity;
import com.sinapipro.api.schedule.application.CriticalPathService.CriticalPathResult;
import com.sinapipro.api.schedule.domain.ActivityDependency;
import com.sinapipro.api.schedule.domain.ActivityDependencyRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriticalPathServiceTest {

    @Mock ScheduleActivityRepository activityRepository;
    @Mock ActivityDependencyRepository dependencyRepository;

    CriticalPathService service;

    @BeforeEach
    void setUp() {
        service = new CriticalPathService(activityRepository, dependencyRepository);
    }

    @Test
    @DisplayName("should return empty result when no activities")
    void shouldReturnEmptyWhenNoActivities() {
        UUID budgetId = UUID.randomUUID();
        when(activityRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of());
        when(dependencyRepository.findByBudgetId(budgetId)).thenReturn(List.of());

        CriticalPathResult result = service.calculate(budgetId);

        assertThat(result.activities()).isEmpty();
        assertThat(result.projectDurationDays()).isZero();
    }

    @Test
    @DisplayName("should calculate critical path for linear sequence A→B→C")
    void shouldCalculateLinearCriticalPath() {
        UUID budgetId = UUID.randomUUID();
        // A: 10 days, B: 5 days, C: 8 days (all sequential = all critical)
        ScheduleActivity a = mockActivity("A", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
        ScheduleActivity b = mockActivity("B", LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 15));
        ScheduleActivity c = mockActivity("C", LocalDate.of(2026, 1, 16), LocalDate.of(2026, 1, 23));

        ActivityDependency depAB = mockDependency(a, b);
        ActivityDependency depBC = mockDependency(b, c);

        when(activityRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(a, b, c));
        when(dependencyRepository.findByBudgetId(budgetId)).thenReturn(List.of(depAB, depBC));

        CriticalPathResult result = service.calculate(budgetId);

        assertThat(result.activities()).hasSize(3);
        assertThat(result.criticalPath()).hasSize(3); // all critical in linear sequence
        assertThat(result.projectDurationDays()).isEqualTo(10 + 5 + 8); // 23 days
    }

    @Test
    @DisplayName("should identify non-critical activities with float")
    void shouldIdentifyNonCriticalWithFloat() {
        UUID budgetId = UUID.randomUUID();
        // A: 10 days → C: 5 days (critical path = 15 days)
        // A: 10 days → B: 3 days → C: 5 days (B has float = 15 - 10 - 3 - 5 = not critical)
        // Actually: parallel paths from A. B is shorter so has float.
        ScheduleActivity a = mockActivity("A", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));
        ScheduleActivity b = mockActivity("B", LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 13)); // 3 days
        ScheduleActivity c = mockActivity("C", LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 17)); // 7 days

        // A→B, A→C (parallel after A)
        ActivityDependency depAB = mockDependency(a, b);
        ActivityDependency depAC = mockDependency(a, c);

        when(activityRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(a, b, c));
        when(dependencyRepository.findByBudgetId(budgetId)).thenReturn(List.of(depAB, depAC));

        CriticalPathResult result = service.calculate(budgetId);

        // Project duration = 10 (A) + 7 (C) = 17
        assertThat(result.projectDurationDays()).isEqualTo(17);
        // A and C are critical, B has float
        CriticalActivity actB = result.activities().stream().filter(x -> x.name().equals("B")).findFirst().orElseThrow();
        assertThat(actB.critical()).isFalse();
        assertThat(actB.totalFloat()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should detect cycle in dependencies")
    void shouldDetectCycle() {
        UUID budgetId = UUID.randomUUID();
        ScheduleActivity a = mockActivity("A", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 5));
        ScheduleActivity b = mockActivity("B", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 10));

        // Cycle: A→B and B→A
        ActivityDependency depAB = mockDependency(a, b);
        ActivityDependency depBA = mockDependency(b, a);

        when(activityRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(a, b));
        when(dependencyRepository.findByBudgetId(budgetId)).thenReturn(List.of(depAB, depBA));

        assertThatThrownBy(() -> service.calculate(budgetId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cycle");
    }

    @Test
    @DisplayName("should handle single activity with no dependencies")
    void shouldHandleSingleActivity() {
        UUID budgetId = UUID.randomUUID();
        ScheduleActivity a = mockActivity("A", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));

        when(activityRepository.findByBudgetIdOrderBySortOrder(budgetId)).thenReturn(List.of(a));
        when(dependencyRepository.findByBudgetId(budgetId)).thenReturn(List.of());

        CriticalPathResult result = service.calculate(budgetId);

        assertThat(result.activities()).hasSize(1);
        assertThat(result.projectDurationDays()).isEqualTo(10);
        assertThat(result.criticalPath()).hasSize(1);
    }

    private ScheduleActivity mockActivity(String name, LocalDate start, LocalDate end) {
        UUID id = UUID.randomUUID();
        ScheduleActivity a = mock(ScheduleActivity.class);
        when(a.getId()).thenReturn(id);
        lenient().when(a.getName()).thenReturn(name);
        when(a.getPlannedStart()).thenReturn(start);
        when(a.getPlannedEnd()).thenReturn(end);
        lenient().when(a.getWeight()).thenReturn(BigDecimal.ONE);
        return a;
    }

    private ActivityDependency mockDependency(ScheduleActivity pred, ScheduleActivity succ) {
        ActivityDependency dep = mock(ActivityDependency.class);
        when(dep.getPredecessor()).thenReturn(pred);
        when(dep.getSuccessor()).thenReturn(succ);
        return dep;
    }
}
