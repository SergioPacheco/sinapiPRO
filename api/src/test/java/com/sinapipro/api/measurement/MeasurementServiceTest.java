package com.sinapipro.api.measurement;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.measurement.application.MeasurementService;
import com.sinapipro.api.measurement.application.MeasurementService.*;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

    @Mock MeasurementRepository measurementRepository;
    @Mock BudgetRepository budgetRepository;
    @Mock BudgetItemRepository budgetItemRepository;
    @Mock CostCodeRepository costCodeRepository;
    @Mock CostTransactionRepository costTransactionRepository;
    @Mock InvoiceRepository invoiceRepository;
    @Mock com.sinapipro.api.finance.domain.ReceivableRepository receivableRepository;

    MeasurementService service;

    @BeforeEach
    void setUp() {
        service = new MeasurementService(measurementRepository, budgetRepository, budgetItemRepository, costCodeRepository,
                costTransactionRepository, invoiceRepository, receivableRepository);
    }

    @Test
    @DisplayName("should transition from DRAFT to SUBMITTED")
    void shouldSubmit() {
        UUID id = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), new BigDecimal("0.05"));

        when(measurementRepository.findById(id)).thenReturn(Optional.of(m));
        when(measurementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Measurement result = service.submit(id);

        assertThat(result.getStatus()).isEqualTo(MeasurementStatus.SUBMITTED);
    }

    @Test
    @DisplayName("should transition from SUBMITTED to APPROVED and generate cost transactions")
    void shouldApproveAndGenerateCostTransactions() {
        UUID id = UUID.randomUUID();
        UUID costCodeId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(budgetId);
        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), new BigDecimal("0.05"));
        m.submit(); // move to SUBMITTED
        m.getItems().add(new MeasurementItem(m, costCodeId, "Concrete", new BigDecimal("10"), new BigDecimal("100")));

        CostCode costCode = mock(CostCode.class);
        when(measurementRepository.findById(id)).thenReturn(Optional.of(m));
        when(measurementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(costCodeRepository.findById(costCodeId)).thenReturn(Optional.of(costCode));
        when(costTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.existsByNumber(any())).thenReturn(false);
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Measurement result = service.approve(id);

        assertThat(result.getStatus()).isEqualTo(MeasurementStatus.APPROVED);
        verify(costTransactionRepository).save(any());
        verify(invoiceRepository).save(any()); // Progress billing
    }

    @Test
    @DisplayName("should throw when trying to approve a DRAFT measurement")
    void shouldThrowWhenApprovingDraft() {
        UUID id = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), BigDecimal.ZERO);

        when(measurementRepository.findById(id)).thenReturn(Optional.of(m));

        assertThatThrownBy(() -> service.approve(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SUBMITTED");
    }

    @Test
    @DisplayName("should calculate summary with only approved/paid measurements")
    void shouldCalculateSummary() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);

        Measurement approved = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), new BigDecimal("0.10"));
        approved.submit();
        approved.approve();
        approved.getItems().add(new MeasurementItem(approved, (UUID) null, "Item 1", new BigDecimal("5"), new BigDecimal("200")));

        Measurement draft = new Measurement(budget, 2, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), new BigDecimal("0.10"));
        draft.getItems().add(new MeasurementItem(draft, (UUID) null, "Item 2", new BigDecimal("10"), new BigDecimal("100")));

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of(draft, approved));

        MeasurementSummary summary = service.summary(budgetId);

        // Only approved: 5 × 200 = 1000 gross, net = 1000 - 100 = 900
        assertThat(summary.totalGross()).isEqualByComparingTo("1000");
        assertThat(summary.totalNet()).isEqualByComparingTo("900.00");
        assertThat(summary.totalMeasurements()).isEqualTo(2);
    }

    @Test
    @DisplayName("should calculate balance against contracted total")
    void shouldCalculateBalance() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);

        Measurement m = new Measurement(budget, 1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), BigDecimal.ZERO);
        m.submit();
        m.approve();
        m.getItems().add(new MeasurementItem(m, (UUID) null, "Item", new BigDecimal("10"), new BigDecimal("100")));

        when(measurementRepository.findByBudgetIdOrderByNumberDesc(budgetId)).thenReturn(List.of(m));

        BalanceResult balance = service.balance(budgetId, new BigDecimal("5000"));

        assertThat(balance.measured()).isEqualByComparingTo("1000");
        assertThat(balance.remaining()).isEqualByComparingTo("4000");
        assertThat(balance.percentMeasured()).isEqualByComparingTo("20.00");
    }
}
