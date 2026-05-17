package com.sinapipro.api.schedule.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.schedule.application.CriticalPathService;
import com.sinapipro.api.schedule.application.SCurveService;
import com.sinapipro.api.schedule.application.ScheduleReportService;
import com.sinapipro.api.schedule.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleControllerTest {

    @Mock ScheduleActivityRepository activityRepository;
    @Mock ActivityDependencyRepository dependencyRepository;
    @Mock ScheduleBaselineRepository baselineRepository;
    @Mock BudgetRepository budgetRepository;
    @Mock SCurveService sCurveService;
    @Mock CriticalPathService criticalPathService;
    @Mock ScheduleReportService scheduleReportService;
    @Mock HolidayRepository holidayRepository;

    private ScheduleController controller;

    @BeforeEach
    void setUp() {
        controller = new ScheduleController(
                activityRepository,
                dependencyRepository,
                baselineRepository,
                budgetRepository,
                sCurveService,
                criticalPathService,
                scheduleReportService,
                holidayRepository
        );
    }

    @Test
    @DisplayName("should reject progress update when activity belongs to another project")
    void shouldRejectProgressUpdateFromAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();

        ScheduleActivity activity = createActivity(anotherProjectId, "Estrutura");
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        assertThatThrownBy(() -> controller.updateProgress(projectId, activityId,
                new ScheduleController.UpdateProgressRequest(new BigDecimal("50"), LocalDate.now(), null)))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Activity not found in project");

        verify(activityRepository, never()).save(any());
    }

    @Test
    @DisplayName("should reject dependency creation when predecessor is from another project")
    void shouldRejectDependencyFromAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID predecessorId = UUID.randomUUID();
        UUID successorId = UUID.randomUUID();

        ScheduleActivity predecessor = createActivity(anotherProjectId, "Predecessora");

        when(activityRepository.findById(predecessorId)).thenReturn(Optional.of(predecessor));

        assertThatThrownBy(() -> controller.addDependency(projectId,
                new ScheduleController.CreateDependencyRequest(predecessorId, successorId, "FS")))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Activity not found in project");

        verify(dependencyRepository, never()).save(any());
    }

    @Test
    @DisplayName("should reject baseline detail when baseline belongs to another project")
    void shouldRejectBaselineFromAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID baselineId = UUID.randomUUID();

        ScheduleBaseline baseline = new ScheduleBaseline(anotherProjectId, "Baseline A", java.util.List.of());
        when(baselineRepository.findById(baselineId)).thenReturn(Optional.of(baseline));

        assertThatThrownBy(() -> controller.getBaseline(projectId, baselineId))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Baseline not found in project");
    }

    private ScheduleActivity createActivity(UUID projectId, String name) {
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(projectId);
        return new ScheduleActivity(
                budget,
                name,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 10),
                new BigDecimal("10"),
                1
        );
    }
}
