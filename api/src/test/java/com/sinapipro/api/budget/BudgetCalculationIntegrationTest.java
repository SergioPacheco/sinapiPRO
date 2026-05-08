package com.sinapipro.api.budget;

import com.sinapipro.api.TestcontainersConfiguration;
import com.sinapipro.api.budget.application.BudgetCalculationService;
import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.sinapi.domain.Composition;
import com.sinapipro.api.sinapi.domain.CompositionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class BudgetCalculationIntegrationTest {

    @Autowired BudgetRepository budgetRepository;
    @Autowired BudgetStageRepository stageRepository;
    @Autowired BudgetItemRepository itemRepository;
    @Autowired BdiConfigRepository bdiConfigRepository;
    @Autowired CompositionRepository compositionRepository;
    @Autowired BudgetCalculationService calculationService;

    @Test
    @DisplayName("Should calculate budget summary with BDI correctly")
    void shouldCalculateSummaryWithBdi() {
        // Setup
        Budget budget = budgetRepository.save(new Budget("INT-BDI-001", "BDI Test", "Client",
                new BigDecimal("100000"), BudgetStatus.DRAFT, LocalDate.now(), null, Map.of()));

        Composition comp = compositionRepository.save(new Composition("99999", "Test Composition", "M3", "TEST"));

        BudgetStage stage = stageRepository.save(new BudgetStage(budget, null, "Foundation", 1));

        // Item: qty=10, unitCost=1000, bdiPct=0.25 → direct=10000
        itemRepository.save(new BudgetItem(stage, comp, new BigDecimal("10"), new BigDecimal("1000"), new BigDecimal("0.25")));
        // Item: qty=5, unitCost=2000, bdiPct=0.25 → direct=10000
        itemRepository.save(new BudgetItem(stage, comp, new BigDecimal("5"), new BigDecimal("2000"), new BigDecimal("0.25")));

        // BDI: 5% admin + 8% profit + 10% taxes + 5% social + 1% financial + 1% risks = 30%
        bdiConfigRepository.save(new BdiConfig(budget,
                new BigDecimal("0.0500"), new BigDecimal("0.0800"), new BigDecimal("0.1000"),
                new BigDecimal("0.0500"), new BigDecimal("0.0100"), new BigDecimal("0.0100")));

        // Calculate
        var summary = calculationService.calculateSummary(budget.getId());

        // Direct cost = 10000 + 10000 = 20000
        assertThat(summary.directCost()).isEqualByComparingTo("20000");
        // BDI = 30%
        assertThat(summary.bdiPct()).isEqualByComparingTo("0.3000");
        // BDI amount = 20000 × 0.30 = 6000
        assertThat(summary.bdiAmount()).isEqualByComparingTo("6000.00");
        // Total = 20000 + 6000 = 26000
        assertThat(summary.totalWithBdi()).isEqualByComparingTo("26000.00");
    }
}
