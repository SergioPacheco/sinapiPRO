package com.sinapipro.api.equipment;

import com.sinapipro.api.equipment.application.EquipmentService;
import com.sinapipro.api.equipment.application.EquipmentService.*;
import com.sinapipro.api.equipment.domain.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock EquipmentRepository equipmentRepository;
    @Mock EquipmentUsageRepository usageRepository;

    EquipmentService service;

    @BeforeEach
    void setUp() {
        service = new EquipmentService(equipmentRepository, usageRepository);
    }

    @Test
    @DisplayName("should create equipment with initial zero hours/km")
    void shouldCreate() {
        when(equipmentRepository.existsByCode("EQ-001")).thenReturn(false);
        when(equipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Equipment result = service.create("EQ-001", "Excavator", "HEAVY", "CAT", "320D",
                2022, null, new BigDecimal("150.00"));

        assertThat(result.getCode()).isEqualTo("EQ-001");
        assertThat(result.getCurrentHours()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    @DisplayName("should throw when code already exists")
    void shouldThrowOnDuplicateCode() {
        when(equipmentRepository.existsByCode("EQ-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create("EQ-001", "Excavator", "HEAVY", null, null, null, null, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("should record usage and update equipment hours/km")
    void shouldRecordUsage() {
        UUID eqId = UUID.randomUUID();
        Equipment eq = new Equipment("EQ-001", "Excavator", "HEAVY", "CAT", "320D", 2022, null, new BigDecimal("150.00"));

        when(equipmentRepository.findById(eqId)).thenReturn(Optional.of(eq));
        when(equipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EquipmentUsage usage = service.recordUsage(eqId, UUID.randomUUID(), LocalDate.now(),
                new BigDecimal("8"), new BigDecimal("50"), "John", null);

        assertThat(eq.getCurrentHours()).isEqualByComparingTo("8");
        assertThat(eq.getCurrentKm()).isEqualByComparingTo("50");
        assertThat(usage.getCost()).isEqualByComparingTo("1200.00"); // 8h × 150
    }

    @Test
    @DisplayName("should detect maintenance due by hours")
    void shouldDetectMaintenanceDueByHours() {
        Equipment eq = new Equipment("EQ-001", "Excavator", "HEAVY", null, null, null, null, BigDecimal.TEN);
        eq.setMaintenanceSchedule(new BigDecimal("500"), null);
        eq.addUsage(new BigDecimal("600"), BigDecimal.ZERO); // over limit

        when(equipmentRepository.findMaintenanceDueByHours()).thenReturn(List.of(eq));
        when(equipmentRepository.findMaintenanceDueByDate()).thenReturn(List.of());

        List<MaintenanceAlert> alerts = service.getMaintenanceAlerts();

        assertThat(alerts).hasSize(1);
        assertThat(alerts.getFirst().alertType()).isEqualTo("HOURS");
    }

    @Test
    @DisplayName("should calculate cost summary for a budget")
    void shouldCalculateCostSummary() {
        UUID budgetId = UUID.randomUUID();
        Equipment eq = new Equipment("EQ-001", "Excavator", "HEAVY", null, null, null, null, new BigDecimal("100"));
        EquipmentUsage u1 = new EquipmentUsage(eq, budgetId, LocalDate.now(), new BigDecimal("4"), BigDecimal.ZERO, "Op1", null);
        EquipmentUsage u2 = new EquipmentUsage(eq, budgetId, LocalDate.now(), new BigDecimal("6"), BigDecimal.ZERO, "Op2", null);

        when(usageRepository.findByBudgetIdOrderByUsageDateDesc(budgetId)).thenReturn(List.of(u1, u2));

        EquipmentCostSummary summary = service.costSummary(budgetId);

        assertThat(summary.totalUsages()).isEqualTo(2);
        assertThat(summary.totalHours()).isEqualByComparingTo("10");
        assertThat(summary.totalCost()).isEqualByComparingTo("1000"); // 10h × 100
    }
}
