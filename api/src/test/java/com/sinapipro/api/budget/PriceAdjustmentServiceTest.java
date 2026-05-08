package com.sinapipro.api.budget;

import com.sinapipro.api.budget.application.PriceAdjustmentService;
import com.sinapipro.api.budget.application.PriceAdjustmentService.AdjustmentResult;
import com.sinapipro.api.budget.application.PriceAdjustmentService.AdjustmentType;
import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetStage;
import com.sinapipro.api.sinapi.domain.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceAdjustmentServiceTest {

    @Mock BudgetItemRepository itemRepository;
    @Mock MaterialRepository materialRepository;

    PriceAdjustmentService service;

    @BeforeEach
    void setUp() {
        service = new PriceAdjustmentService(itemRepository, materialRepository);
    }

    @Test
    @DisplayName("should adjust all items by percentage")
    void shouldAdjustByPercentage() {
        UUID budgetId = UUID.randomUUID();
        BudgetItem item = createItem(new BigDecimal("100.0000"));

        when(itemRepository.findAllByBudgetId(budgetId)).thenReturn(List.of(item));
        when(itemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        AdjustmentResult result = service.adjustByPercentage(budgetId, new BigDecimal("10")); // +10%

        assertThat(result.type()).isEqualTo(AdjustmentType.PERCENTAGE);
        assertThat(result.itemsAdjusted()).isEqualTo(1);
        assertThat(result.items().getFirst().newUnitCost()).isEqualByComparingTo("110.0000");
    }

    @Test
    @DisplayName("should adjust by negative percentage (decrease)")
    void shouldAdjustByNegativePercentage() {
        UUID budgetId = UUID.randomUUID();
        BudgetItem item = createItem(new BigDecimal("200.0000"));

        when(itemRepository.findAllByBudgetId(budgetId)).thenReturn(List.of(item));
        when(itemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        AdjustmentResult result = service.adjustByPercentage(budgetId, new BigDecimal("-20")); // -20%

        assertThat(result.items().getFirst().newUnitCost()).isEqualByComparingTo("160.0000");
    }

    @Test
    @DisplayName("should adjust all items by fixed value")
    void shouldAdjustByValue() {
        UUID budgetId = UUID.randomUUID();
        BudgetItem item = createItem(new BigDecimal("100.0000"));

        when(itemRepository.findAllByBudgetId(budgetId)).thenReturn(List.of(item));
        when(itemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        AdjustmentResult result = service.adjustByValue(budgetId, new BigDecimal("25.50"));

        assertThat(result.type()).isEqualTo(AdjustmentType.VALUE);
        assertThat(result.items().getFirst().newUnitCost()).isEqualByComparingTo("125.5000");
    }

    @Test
    @DisplayName("should not allow negative unit cost when adjusting by value")
    void shouldClampToZero() {
        UUID budgetId = UUID.randomUUID();
        BudgetItem item = createItem(new BigDecimal("50.0000"));

        when(itemRepository.findAllByBudgetId(budgetId)).thenReturn(List.of(item));
        when(itemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        AdjustmentResult result = service.adjustByValue(budgetId, new BigDecimal("-100"));

        assertThat(result.items().getFirst().newUnitCost()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("should recalculate from SINAPI reference prices")
    void shouldAdjustBySinapiReference() {
        UUID budgetId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();

        Material material = mock(Material.class);
        when(material.getId()).thenReturn(materialId);

        CompositionItem ci = mock(CompositionItem.class);
        when(ci.getMaterial()).thenReturn(material);
        when(ci.getCoefficient()).thenReturn(new BigDecimal("2.5"));

        Composition composition = mock(Composition.class);
        when(composition.getItems()).thenReturn(List.of(ci));
        when(composition.getDescription()).thenReturn("Test Comp");

        BudgetStage stage = mock(BudgetStage.class);
        BudgetItem item = new BudgetItem(stage, composition, new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("0.25"));

        when(itemRepository.findAllByBudgetId(budgetId)).thenReturn(List.of(item));
        when(itemRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        MaterialPrice mp = mock(MaterialPrice.class);
        when(mp.getMaterial()).thenReturn(material);
        when(mp.getPrice()).thenReturn(new BigDecimal("80.00"));
        when(materialRepository.findPricesBatch(anyList(), eq("RN"), eq(LocalDate.of(2026, 3, 1))))
                .thenReturn(List.of(mp));

        AdjustmentResult result = service.adjustBySinapiReference(budgetId, "RN", LocalDate.of(2026, 3, 1));

        assertThat(result.type()).isEqualTo(AdjustmentType.SINAPI);
        // New cost = 2.5 × 80 = 200
        assertThat(result.items().getFirst().newUnitCost()).isEqualByComparingTo("200.0000");
    }

    private BudgetItem createItem(BigDecimal unitCost) {
        BudgetStage stage = mock(BudgetStage.class);
        Composition composition = mock(Composition.class);
        when(composition.getDescription()).thenReturn("Test");
        lenient().when(composition.getItems()).thenReturn(List.of());
        return new BudgetItem(stage, composition, new BigDecimal("10"), unitCost, new BigDecimal("0.25"));
    }
}
