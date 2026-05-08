package com.sinapipro.api.sinapi;

import com.sinapipro.api.sinapi.application.SinapiImportService;
import com.sinapipro.api.sinapi.application.SinapiImportService.ImportResult;
import com.sinapipro.api.sinapi.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SinapiImportServiceTest {

    @Mock MaterialRepository materialRepository;
    @Mock CompositionRepository compositionRepository;

    SinapiImportService service;

    @BeforeEach
    void setUp() {
        service = new SinapiImportService(materialRepository, compositionRepository);
    }

    @Test
    @DisplayName("should import materials from CSV")
    void shouldImportMaterials() {
        String csv = """
                code;description;unit;origin;state;reference_month;price
                00001;Cimento Portland;KG;SINAPI;RN;2026-01-01;0.72
                00002;Areia Media;M3;SINAPI;RN;2026-01-01;95.00
                """;

        Material m1 = new Material("00001", "Cimento Portland", "KG", "SINAPI");
        m1.getPrices(); // init list
        Material m2 = new Material("00002", "Areia Media", "M3", "SINAPI");

        when(materialRepository.findBySinapiCode("00001")).thenReturn(Optional.empty());
        when(materialRepository.findBySinapiCode("00002")).thenReturn(Optional.empty());
        when(materialRepository.save(any(Material.class))).thenAnswer(inv -> {
            Material m = inv.getArgument(0);
            // Simulate that prices list is mutable
            return m;
        });

        ImportResult result = service.importMaterials(toStream(csv), ";");

        assertThat(result.type()).isEqualTo("materials");
        assertThat(result.created()).isEqualTo(2);
        assertThat(result.errors()).isZero();
    }

    @Test
    @DisplayName("should skip duplicate prices")
    void shouldSkipDuplicatePrices() {
        String csv = """
                code;description;unit;origin;state;reference_month;price
                00001;Cimento;KG;SINAPI;RN;2026-01-01;0.72
                """;

        Material existing = new Material("00001", "Cimento", "KG", "SINAPI");
        // Simulate existing price
        existing.getPrices().add(new MaterialPrice(existing, "RN",
                java.time.LocalDate.of(2026, 1, 1), new java.math.BigDecimal("0.72")));

        when(materialRepository.findBySinapiCode("00001")).thenReturn(Optional.of(existing));

        ImportResult result = service.importMaterials(toStream(csv), ";");

        assertThat(result.updated()).isEqualTo(1); // skipped as duplicate
        assertThat(result.created()).isZero();
    }

    @Test
    @DisplayName("should report errors for malformed lines")
    void shouldReportErrors() {
        String csv = """
                code;description;unit;origin;state;reference_month;price
                00001;Cimento;KG
                """;

        ImportResult result = service.importMaterials(toStream(csv), ";");

        assertThat(result.errors()).isEqualTo(1);
        assertThat(result.errorMessages()).hasSize(1);
        assertThat(result.errorMessages().getFirst()).contains("Line 2");
    }

    @Test
    @DisplayName("should import compositions with items")
    void shouldImportCompositions() {
        String csv = """
                comp_code;description;unit;group;material_code;coefficient
                87316;CONCRETO FCK=25;M3;CONCRETO;00001;348.00
                """;

        Material material = new Material("00001", "Cimento", "KG", "SINAPI");
        Composition comp = new Composition("87316", "CONCRETO FCK=25", "M3", "CONCRETO");

        when(compositionRepository.findBySinapiCode("87316")).thenReturn(Optional.empty());
        when(compositionRepository.save(any())).thenReturn(comp);
        when(materialRepository.findBySinapiCode("00001")).thenReturn(Optional.of(material));

        ImportResult result = service.importCompositions(toStream(csv), ";");

        assertThat(result.type()).isEqualTo("compositions");
        assertThat(result.created()).isEqualTo(1);
    }

    @Test
    @DisplayName("should report error when material not found for composition")
    void shouldReportMissingMaterial() {
        String csv = """
                comp_code;description;unit;group;material_code;coefficient
                87316;CONCRETO;M3;CONCRETO;99999;100.00
                """;

        Composition comp = new Composition("87316", "CONCRETO", "M3", "CONCRETO");
        when(compositionRepository.findBySinapiCode("87316")).thenReturn(Optional.empty());
        when(compositionRepository.save(any())).thenReturn(comp);
        when(materialRepository.findBySinapiCode("99999")).thenReturn(Optional.empty());

        ImportResult result = service.importCompositions(toStream(csv), ";");

        assertThat(result.errors()).isEqualTo(1);
        assertThat(result.errorMessages().getFirst()).contains("material not found");
    }

    private InputStream toStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}
