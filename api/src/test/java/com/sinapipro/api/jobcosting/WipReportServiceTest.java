package com.sinapipro.api.jobcosting;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.jobcosting.application.WipReportService;
import com.sinapipro.api.jobcosting.application.WipReportService.WipReport;
import com.sinapipro.api.jobcosting.application.WipReportService.WipStatus;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.measurement.domain.*;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WipReportServiceTest {

    @Mock CostTransactionRepository costTransactionRepository;
    @Mock MeasurementRepository measurementRepository;

    WipReportService service;

    @BeforeEach
    void setUp() {
        service = new WipReportService(costTransactionRepository, measurementRepository);
    }

    @Test
    @DisplayName("should detect over-billing when billed > actual cost")
    void shouldDetectOverBilling() {
        UUID budgetId = UUID.randomUUID();
        when(costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL))
                .thenReturn(new BigDecimal("80000"));

        Budget budget = mock(Budget.class);
        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), BigDecimal.ZERO);
        m.submit();
        m.approve("test-user");
        m.getItems().add(new MeasurementItem(m, (UUID) null, "Work", new BigDecimal("1"), new BigDecimal("100000")));

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of(m));

        WipReport report = service.calculate(budgetId);

        assertThat(report.status()).isEqualTo(WipStatus.OVER_BILLED);
        assertThat(report.wipVariance()).isEqualByComparingTo("20000");
        assertThat(report.billingRatio()).isGreaterThan(BigDecimal.ONE);
    }

    @Test
    @DisplayName("should detect under-billing when actual cost > billed")
    void shouldDetectUnderBilling() {
        UUID budgetId = UUID.randomUUID();
        when(costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL))
                .thenReturn(new BigDecimal("150000"));

        Budget budget = mock(Budget.class);
        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), BigDecimal.ZERO);
        m.submit();
        m.approve("test-user");
        m.getItems().add(new MeasurementItem(m, (UUID) null, "Work", new BigDecimal("1"), new BigDecimal("100000")));

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of(m));

        WipReport report = service.calculate(budgetId);

        assertThat(report.status()).isEqualTo(WipStatus.UNDER_BILLED);
        assertThat(report.wipVariance()).isNegative();
    }

    @Test
    @DisplayName("should return balanced when billed equals actual")
    void shouldReturnBalanced() {
        UUID budgetId = UUID.randomUUID();
        when(costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL))
                .thenReturn(new BigDecimal("50000"));

        Budget budget = mock(Budget.class);
        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), BigDecimal.ZERO);
        m.submit();
        m.approve("test-user");
        m.getItems().add(new MeasurementItem(m, (UUID) null, "Work", new BigDecimal("1"), new BigDecimal("50000")));

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of(m));

        WipReport report = service.calculate(budgetId);

        assertThat(report.status()).isEqualTo(WipStatus.BALANCED);
    }
}
