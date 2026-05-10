package com.sinapipro.api.measurement;

import com.sinapipro.api.TestcontainersConfiguration;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.budget.domain.BudgetStatus;
import com.sinapipro.api.jobcosting.domain.*;
import com.sinapipro.api.measurement.application.MeasurementService;
import com.sinapipro.api.measurement.application.MeasurementService.ItemInput;
import com.sinapipro.api.measurement.domain.Measurement;
import com.sinapipro.api.measurement.domain.MeasurementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class MeasurementFlowIntegrationTest {

    @Autowired MeasurementService measurementService;
    @Autowired BudgetRepository budgetRepository;
    @Autowired CostCodeRepository costCodeRepository;
    @Autowired CostTransactionRepository costTransactionRepository;

    @Test
    @DisplayName("Approving measurement should generate ACTUAL cost transaction")
    void shouldGenerateCostTransactionOnApproval() {
        // Setup: create budget + cost code
        Budget budget = budgetRepository.save(new Budget("INT-MEAS-001", "Test Budget", "Client",
                new BigDecimal("100000"), BudgetStatus.DRAFT, LocalDate.now(), null, Map.of()));
        CostCode costCode = costCodeRepository.save(
                new CostCode(budget, null, "01.001", "Foundation", new BigDecimal("50000")));

        // Create measurement with item linked to cost code
        Measurement measurement = measurementService.create(budget.getId(), 1,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), new BigDecimal("0.05"),
                List.of(new ItemInput(costCode.getId(), null, "Concrete work", new BigDecimal("10"), new BigDecimal("500"))));

        assertThat(measurement.getStatus()).isEqualTo(MeasurementStatus.DRAFT);

        // Submit
        measurement = measurementService.submit(measurement.getId());
        assertThat(measurement.getStatus()).isEqualTo(MeasurementStatus.SUBMITTED);

        // Approve — should generate cost transaction
        measurement = measurementService.approve(measurement.getId());
        assertThat(measurement.getStatus()).isEqualTo(MeasurementStatus.APPROVED);

        // Verify cost transaction was created
        BigDecimal actualSum = costTransactionRepository.sumByCodeAndType(costCode.getId(), CostTransactionType.ACTUAL);
        assertThat(actualSum).isEqualByComparingTo("5000.00"); // 10 × 500
    }

    @Test
    @DisplayName("Measurement summary should only include approved measurements")
    void shouldCalculateSummaryCorrectly() {
        Budget budget = budgetRepository.save(new Budget("INT-MEAS-002", "Summary Test", "Client",
                new BigDecimal("200000"), BudgetStatus.APPROVED, LocalDate.now(), null, Map.of()));

        // Create and approve one measurement
        Measurement m1 = measurementService.create(budget.getId(), 1,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), new BigDecimal("0.10"),
                List.of(new ItemInput(null, null, "Item A", new BigDecimal("5"), new BigDecimal("1000"))));
        measurementService.submit(m1.getId());
        measurementService.approve(m1.getId());

        // Create draft measurement (should NOT be in summary)
        measurementService.create(budget.getId(), 2,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), new BigDecimal("0.10"),
                List.of(new ItemInput(null, null, "Item B", new BigDecimal("10"), new BigDecimal("2000"))));

        var summary = measurementService.summary(budget.getId());

        assertThat(summary.totalMeasurements()).isEqualTo(2);
        assertThat(summary.totalGross()).isEqualByComparingTo("5000"); // only m1
    }
}
