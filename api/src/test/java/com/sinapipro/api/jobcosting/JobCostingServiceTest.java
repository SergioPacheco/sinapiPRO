package com.sinapipro.api.jobcosting;

import com.sinapipro.api.jobcosting.application.JobCostingService;
import com.sinapipro.api.jobcosting.application.JobCostingService.BudgetCostSummary;
import com.sinapipro.api.jobcosting.application.JobCostingService.CostCodeSummary;
import com.sinapipro.api.jobcosting.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobCostingServiceTest {

    @Mock CostCodeRepository codeRepository;
    @Mock CostTransactionRepository transactionRepository;

    JobCostingService service;

    @BeforeEach
    void setUp() {
        service = new JobCostingService(codeRepository, transactionRepository);
    }

    @Test
    @DisplayName("should calculate variance as budgeted - actual - committed")
    void shouldCalculateVariance() {
        UUID codeId = UUID.randomUUID();
        CostCode code = costCode(codeId, "01.001", "Fundação", new BigDecimal("100000.00"));

        when(codeRepository.findById(codeId)).thenReturn(Optional.of(code));
        when(transactionRepository.sumByCodeAndType(codeId, CostTransactionType.ACTUAL)).thenReturn(new BigDecimal("35000.00"));
        when(transactionRepository.sumByCodeAndType(codeId, CostTransactionType.COMMITTED)).thenReturn(new BigDecimal("20000.00"));

        CostCodeSummary result = service.summarize(codeId);

        assertThat(result.budgeted()).isEqualByComparingTo("100000.00");
        assertThat(result.actual()).isEqualByComparingTo("35000.00");
        assertThat(result.committed()).isEqualByComparingTo("20000.00");
        assertThat(result.availableBalance()).isEqualByComparingTo("45000.00");
    }

    @Test
    @DisplayName("should return negative balance when over budget")
    void shouldReturnNegativeBalanceWhenOverBudget() {
        UUID codeId = UUID.randomUUID();
        CostCode code = costCode(codeId, "01.002", "Estrutura", new BigDecimal("50000.00"));

        when(codeRepository.findById(codeId)).thenReturn(Optional.of(code));
        when(transactionRepository.sumByCodeAndType(codeId, CostTransactionType.ACTUAL)).thenReturn(new BigDecimal("40000.00"));
        when(transactionRepository.sumByCodeAndType(codeId, CostTransactionType.COMMITTED)).thenReturn(new BigDecimal("15000.00"));

        CostCodeSummary result = service.summarize(codeId);

        assertThat(result.availableBalance()).isEqualByComparingTo("-5000.00");
    }

    @Test
    @DisplayName("should summarize all cost codes for a budget")
    void shouldSummarizeAllCodes() {
        UUID budgetId = UUID.randomUUID();
        UUID code1Id = UUID.randomUUID();
        UUID code2Id = UUID.randomUUID();

        CostCode code1 = costCode(code1Id, "01.001", "Fundação", new BigDecimal("100000.00"));
        CostCode code2 = costCode(code2Id, "01.002", "Estrutura", new BigDecimal("200000.00"));

        when(codeRepository.findByBudgetIdOrderByCode(budgetId)).thenReturn(List.of(code1, code2));
        when(codeRepository.findById(code1Id)).thenReturn(Optional.of(code1));
        when(codeRepository.findById(code2Id)).thenReturn(Optional.of(code2));
        when(transactionRepository.sumByCodeAndType(code1Id, CostTransactionType.ACTUAL)).thenReturn(new BigDecimal("10000.00"));
        when(transactionRepository.sumByCodeAndType(code1Id, CostTransactionType.COMMITTED)).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByCodeAndType(code2Id, CostTransactionType.ACTUAL)).thenReturn(new BigDecimal("50000.00"));
        when(transactionRepository.sumByCodeAndType(code2Id, CostTransactionType.COMMITTED)).thenReturn(new BigDecimal("30000.00"));

        List<CostCodeSummary> result = service.summarizeAll(budgetId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).availableBalance()).isEqualByComparingTo("90000.00");
        assertThat(result.get(1).availableBalance()).isEqualByComparingTo("120000.00");
    }

    @Test
    @DisplayName("should calculate budget-level summary totals")
    void shouldCalculateBudgetSummary() {
        UUID budgetId = UUID.randomUUID();
        CostCode code1 = costCode(UUID.randomUUID(), "01.001", "Fundação", new BigDecimal("100000.00"));
        CostCode code2 = costCode(UUID.randomUUID(), "02.001", "Alvenaria", new BigDecimal("150000.00"));

        when(codeRepository.findByBudgetIdOrderByCode(budgetId)).thenReturn(List.of(code1, code2));
        when(transactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL)).thenReturn(new BigDecimal("80000.00"));
        when(transactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.COMMITTED)).thenReturn(new BigDecimal("40000.00"));

        BudgetCostSummary result = service.budgetSummary(budgetId);

        assertThat(result.totalBudgeted()).isEqualByComparingTo("250000.00");
        assertThat(result.totalActual()).isEqualByComparingTo("80000.00");
        assertThat(result.totalCommitted()).isEqualByComparingTo("40000.00");
        assertThat(result.totalVariance()).isEqualByComparingTo("130000.00");
    }

    private CostCode costCode(UUID id, String code, String name, BigDecimal budgeted) {
        CostCode mock = org.mockito.Mockito.mock(CostCode.class);
        lenient().when(mock.getId()).thenReturn(id);
        lenient().when(mock.getCode()).thenReturn(code);
        lenient().when(mock.getName()).thenReturn(name);
        when(mock.getBudgetedAmount()).thenReturn(budgeted);
        return mock;
    }
}
