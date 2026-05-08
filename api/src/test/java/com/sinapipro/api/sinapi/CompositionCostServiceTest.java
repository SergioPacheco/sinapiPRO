package com.sinapipro.api.sinapi;

import com.sinapipro.api.sinapi.application.CompositionCostResult;
import com.sinapipro.api.sinapi.application.CompositionCostService;
import com.sinapipro.api.sinapi.domain.*;
import com.sinapipro.api.shared.observability.BusinessObservationService;
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
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompositionCostServiceTest {

    @Mock CompositionRepository compositionRepository;
    @Mock MaterialRepository materialRepository;
    @Mock BusinessObservationService observationService;

    CompositionCostService service;

    @BeforeEach
    void setUp() {
        service = new CompositionCostService(compositionRepository, materialRepository, observationService);
        lenient().when(observationService.observe(anyString(), anyString(), any()))
                .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(2)).get());
    }

    @Test
    @DisplayName("should calculate unit cost as sum of (coefficient × price) for each item")
    void shouldCalculateUnitCost() throws Exception {
        UUID compositionId = UUID.randomUUID();
        UUID cimentoId = UUID.randomUUID();
        UUID areiaId = UUID.randomUUID();
        Material cimento = new Material("00000370", "CIMENTO PORTLAND", "KG", "SINAPI");
        setId(cimento, cimentoId);
        Material areia = new Material("00000367", "AREIA MEDIA", "M3", "SINAPI");
        setId(areia, areiaId);

        Composition composition = new Composition("87316", "CONCRETO FCK=25MPA", "M3", "CONCRETO");
        composition.addItem(cimento, new BigDecimal("348.000000"));
        composition.addItem(areia, new BigDecimal("0.670000"));

        when(compositionRepository.findById(compositionId)).thenReturn(Optional.of(composition));

        // Simulate prices: cimento=0.72, areia=95.00
        MaterialPrice priceCimento = new MaterialPrice(cimento, "RN", LocalDate.of(2026, 1, 1), new BigDecimal("0.7200"));
        MaterialPrice priceAreia = new MaterialPrice(areia, "RN", LocalDate.of(2026, 1, 1), new BigDecimal("95.0000"));
        when(materialRepository.findPricesBatch(anyList(), eq("RN"), eq(LocalDate.of(2026, 1, 1))))
                .thenReturn(List.of(priceCimento, priceAreia));

        CompositionCostResult result = service.calculateCost(compositionId, "RN", LocalDate.of(2026, 1, 1));

        // 348 × 0.72 = 250.56 + 0.67 × 95 = 63.65 = 314.21
        assertThat(result.totalUnitCost()).isEqualByComparingTo(new BigDecimal("314.2100"));
        assertThat(result.items()).hasSize(2);
        assertThat(result.state()).isEqualTo("RN");
        assertThat(result.sinapiCode()).isEqualTo("87316");
    }

    @Test
    @DisplayName("should return zero cost when no prices available")
    void shouldReturnZeroWhenNoPrices() throws Exception {
        UUID compositionId = UUID.randomUUID();
        Material cimento = new Material("00000370", "CIMENTO", "KG", "SINAPI");
        setId(cimento, UUID.randomUUID());
        Composition composition = new Composition("87316", "CONCRETO", "M3", "CONCRETO");
        composition.addItem(cimento, new BigDecimal("348.000000"));

        when(compositionRepository.findById(compositionId)).thenReturn(Optional.of(composition));
        when(materialRepository.findPricesBatch(anyList(), eq("AC"), eq(LocalDate.of(2026, 1, 1))))
                .thenReturn(List.of());

        CompositionCostResult result = service.calculateCost(compositionId, "AC", LocalDate.of(2026, 1, 1));

        assertThat(result.totalUnitCost()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private void setId(Object entity, UUID id) throws Exception {
        var field = entity.getClass().getSuperclass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
