package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.application.AbcCurveService;
import com.sinapipro.api.budget.application.BudgetCalculationService;
import com.sinapipro.api.report.BudgetReportService;
import com.sinapipro.api.report.ExcelExportService;
import com.sinapipro.api.budget.application.PriceAdjustmentService;
import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.config.settings.AppSettingsRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.application.CompositionCostService;
import com.sinapipro.api.sinapi.domain.CompositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetDetailControllerTest {

    @Mock BudgetRepository budgetRepository;
    @Mock BudgetStageRepository stageRepository;
    @Mock BudgetItemRepository itemRepository;
    @Mock BdiConfigRepository bdiConfigRepository;
    @Mock BudgetItemMemoRepository memoRepository;
    @Mock BudgetProposalRepository proposalRepository;
    @Mock BudgetItemTagRepository tagRepository;
    @Mock SocialChargesConfigRepository socialChargesRepository;
    @Mock CompositionRepository compositionRepository;
    @Mock BudgetCalculationService calculationService;
    @Mock AbcCurveService abcCurveService;
    @Mock PriceAdjustmentService priceAdjustmentService;
    @Mock BudgetReportService budgetReportService;
    @Mock ExcelExportService excelExportService;
    @Mock CompositionCostService compositionCostService;
    @Mock AppSettingsRepository settingsRepository;

    BudgetDetailController controller;

    @BeforeEach
    void setUp() {
        controller = new BudgetDetailController(
                budgetRepository, stageRepository, itemRepository, bdiConfigRepository, memoRepository,
                proposalRepository, tagRepository, socialChargesRepository,
                compositionRepository, calculationService, abcCurveService, priceAdjustmentService,
                budgetReportService, excelExportService, compositionCostService, settingsRepository
        );
    }

    @Test
    @DisplayName("should save and fetch budget item memo")
    void shouldSaveAndGetItemMemo() {
        UUID budgetId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        mockItemInBudget(itemId, budgetId);

        when(memoRepository.findByBudgetItemId(itemId)).thenReturn(Optional.empty());
        when(memoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var request = new BudgetDetailController.ItemMemoRequest(
                List.of(
                        new BudgetDetailController.MemoLineRequest("Trecho 1", "1+1", new BigDecimal("2")),
                        new BudgetDetailController.MemoLineRequest("Trecho 2", "2+2", new BigDecimal("4"))
                ),
                null,
                "Memória inicial"
        );

        var saved = controller.saveItemMemo(budgetId, itemId, request);
        assertThat(saved.result()).isEqualByComparingTo("6");
        assertThat(saved.lines()).hasSize(2);

        BudgetItemMemo memo = new BudgetItemMemo();
        memo.setBudgetItemId(itemId);
        memo.setLines(List.of(new BudgetItemMemo.MemoLine("Trecho 1", "1+1", new BigDecimal("2"))));
        memo.setResult(new BigDecimal("2"));
        memo.setNotes("ok");
        when(memoRepository.findByBudgetItemId(itemId)).thenReturn(Optional.of(memo));

        ResponseEntity<BudgetDetailController.ItemMemoResponse> response = controller.getItemMemo(budgetId, itemId);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().result()).isEqualByComparingTo("2");
    }

    @Test
    @DisplayName("should block memo access when item is from another budget")
    void shouldBlockCrossBudgetMemoAccess() {
        UUID budgetId = UUID.randomUUID();
        UUID anotherBudgetId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        mockItemInBudget(itemId, anotherBudgetId);

        assertThatThrownBy(() -> controller.getItemMemo(budgetId, itemId))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Budget item not found in budget");
    }

    @Test
    @DisplayName("should block stage item listing when stage is from another budget")
    void shouldBlockCrossBudgetStageItemListing() {
        UUID budgetId = UUID.randomUUID();
        UUID anotherBudgetId = UUID.randomUUID();
        UUID stageId = UUID.randomUUID();
        mockStageInBudget(stageId, anotherBudgetId);

        assertThatThrownBy(() -> controller.listItems(budgetId, stageId))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Stage not found in budget");
    }

    @Test
    @DisplayName("should block item creation when stage is from another budget")
    void shouldBlockCrossBudgetItemCreation() {
        UUID budgetId = UUID.randomUUID();
        UUID anotherBudgetId = UUID.randomUUID();
        UUID stageId = UUID.randomUUID();
        UUID compositionId = UUID.randomUUID();
        mockEditableBudget(budgetId);
        mockStageInBudget(stageId, anotherBudgetId);

        assertThatThrownBy(() -> controller.createItem(
                budgetId,
                stageId,
                new BudgetDetailController.CreateItemRequest(compositionId, BigDecimal.ONE, new BigDecimal("10"), BigDecimal.ZERO)
        )).isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Stage not found in budget");

        verify(compositionRepository, never()).findById(any());
    }

    @Test
    @DisplayName("should allow base date update for matching budget")
    void shouldUpdateBaseDate() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        BudgetItem item = mock(BudgetItem.class);
        var composition = mock(com.sinapipro.api.sinapi.domain.Composition.class);
        var costResult = mock(com.sinapipro.api.sinapi.application.CompositionCostResult.class);

        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(itemRepository.findAllByBudgetId(budgetId)).thenReturn(List.of(item));
        when(item.getComposition()).thenReturn(composition);
        when(composition.getId()).thenReturn(UUID.randomUUID());
        when(item.getQuantity()).thenReturn(new BigDecimal("2"));
        when(item.getBdiPct()).thenReturn(new BigDecimal("0.1"));
        when(costResult.totalUnitCost()).thenReturn(new BigDecimal("15"));
        when(compositionCostService.calculateCost(any(), any(), any(LocalDate.class))).thenReturn(costResult);
        when(itemRepository.save(any())).thenReturn(item);

        var response = controller.updateBaseDate(
                budgetId,
                new BudgetDetailController.UpdateBaseDateRequest(LocalDate.of(2026, 5, 1), "SP")
        );

        assertThat(response.updatedPrices()).isEqualTo(1);
        assertThat(response.divergentPrices()).isEqualTo(0);
        assertThat(response.totalItems()).isEqualTo(1);
        verify(budgetRepository).save(budget);
        verify(item).update(new BigDecimal("2"), new BigDecimal("15"), new BigDecimal("0.1"));
    }

    @Test
    @DisplayName("should save and fetch bdi config by item type")
    void shouldSaveAndFetchBdiByItemType() {
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        when(bdiConfigRepository.findByBudgetIdAndItemType(budgetId, "LABOR")).thenReturn(Optional.empty());
        when(bdiConfigRepository.save(any(BdiConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var saved = controller.setBdi(budgetId, new BudgetDetailController.BdiRequest(
                "LABOR",
                new BigDecimal("0.01"),
                new BigDecimal("0.02"),
                new BigDecimal("0.03"),
                new BigDecimal("0.04"),
                new BigDecimal("0.05"),
                new BigDecimal("0.06")
        ));

        assertThat(saved.itemType()).isEqualTo("LABOR");
        assertThat(saved.totalBdi()).isEqualByComparingTo("0.2100");

        BdiConfig config = mock(BdiConfig.class);
        when(config.getItemType()).thenReturn("LABOR");
        when(config.getAdministration()).thenReturn(new BigDecimal("0.01"));
        when(config.getProfit()).thenReturn(new BigDecimal("0.02"));
        when(config.getTaxes()).thenReturn(new BigDecimal("0.03"));
        when(config.getSocialCharges()).thenReturn(new BigDecimal("0.04"));
        when(config.getFinancialExpenses()).thenReturn(new BigDecimal("0.05"));
        when(config.getRisks()).thenReturn(new BigDecimal("0.06"));
        when(config.getTotalBdi()).thenReturn(new BigDecimal("0.21"));
        when(bdiConfigRepository.findByBudgetIdAndItemType(budgetId, "LABOR")).thenReturn(Optional.of(config));

        var fetched = controller.getBdi(budgetId, "LABOR");
        assertThat(fetched.itemType()).isEqualTo("LABOR");
        assertThat(fetched.totalBdi()).isEqualByComparingTo("0.21");
    }

    private void mockItemInBudget(UUID itemId, UUID budgetId) {
        BudgetItem item = mock(BudgetItem.class);
        BudgetStage stage = mock(BudgetStage.class);
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(budgetId);
        when(stage.getBudget()).thenReturn(budget);
        when(item.getStage()).thenReturn(stage);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
    }

    private void mockEditableBudget(UUID budgetId) {
        Budget budget = mock(Budget.class);
        when(budget.getStatus()).thenReturn(BudgetStatus.DRAFT);
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
    }

    private void mockStageInBudget(UUID stageId, UUID budgetId) {
        BudgetStage stage = mock(BudgetStage.class);
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(budgetId);
        when(stage.getBudget()).thenReturn(budget);
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
    }
}
