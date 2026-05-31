package com.sinapipro.api.sinapi;

import com.sinapipro.api.sinapi.application.SinapiImportService;
import com.sinapipro.api.sinapi.application.SinapiImportService.ImportResult;
import com.sinapipro.api.sinapi.domain.*;
import jakarta.persistence.EntityManager;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SinapiImportServiceTest {

    @Mock MaterialRepository materialRepository;
    @Mock CompositionRepository compositionRepository;
    @Mock EntityManager entityManager;

    SinapiImportService service;

    @BeforeEach
    void setUp() {
        service = new SinapiImportService(materialRepository, compositionRepository, entityManager);
    }

    @Test
    @DisplayName("should import materials from xlsx")
    void shouldImportMaterials() throws Exception {
        var xlsx = createMaterialsXlsx(new String[][]{
                {"00001", "Cimento Portland", "KG", "CAIXA", "0.72"},
                {"00002", "Areia Media", "M3", "CAIXA", "95.00"},
        });

        when(materialRepository.findBySinapiCode(any())).thenReturn(Optional.empty());
        when(materialRepository.save(any(Material.class))).thenAnswer(inv -> inv.getArgument(0));

        ImportResult result = service.importMaterials(xlsx, "RN", LocalDate.of(2026, 1, 1), false);

        assertThat(result.type()).isEqualTo("materials");
        assertThat(result.created()).isEqualTo(2);
        assertThat(result.errors()).isZero();
    }

    @Test
    @DisplayName("should skip duplicate prices")
    void shouldSkipDuplicatePrices() throws Exception {
        var xlsx = createMaterialsXlsx(new String[][]{
                {"00001", "Cimento", "KG", "CAIXA", "0.72"},
        });

        Material existing = new Material("00001", "Cimento", "KG", "CAIXA");
        existing.getPrices().add(new MaterialPrice(existing, "RN", LocalDate.of(2026, 1, 1), new BigDecimal("0.72"), false));
        when(materialRepository.findBySinapiCode("00001")).thenReturn(Optional.of(existing));

        ImportResult result = service.importMaterials(xlsx, "RN", LocalDate.of(2026, 1, 1), false);

        assertThat(result.updated()).isEqualTo(1);
        assertThat(result.created()).isZero();
    }

    @Test
    @DisplayName("should report error when header not found")
    void shouldReportMissingHeader() throws Exception {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet();
        var row = sheet.createRow(0);
        row.createCell(0).setCellValue("WRONG_HEADER");
        var xlsx = toInputStream(workbook);

        ImportResult result = service.importMaterials(xlsx, "RN", LocalDate.of(2026, 1, 1), false);

        assertThat(result.errors()).isEqualTo(1);
        assertThat(result.errorMessages().getFirst()).contains("Cabeçalho não encontrado");
    }

    @Test
    @DisplayName("should import compositions from xlsx")
    void shouldImportCompositions() throws Exception {
        var xlsx = createCompositionsXlsx(new String[][]{
                {"87316", "CONCRETO FCK=25", "M3", "CONCRETO", "", "", ""},
                {"87316", "CONCRETO FCK=25", "M3", "CONCRETO", "INSUMO", "00001", "348.00"},
        });

        Material material = new Material("00001", "Cimento", "KG", "CAIXA");
        Composition comp = new Composition("87316", "CONCRETO FCK=25", "M3", "");

        when(compositionRepository.findBySinapiCode("87316")).thenReturn(Optional.empty(), Optional.of(comp));
        when(compositionRepository.save(any())).thenReturn(comp);
        when(materialRepository.findBySinapiCode("00001")).thenReturn(Optional.of(material));

        ImportResult result = service.importCompositions(xlsx, "RN", LocalDate.of(2026, 1, 1), false);

        assertThat(result.type()).isEqualTo("compositions");
        assertThat(result.created()).isEqualTo(1);
    }

    @Test
    @DisplayName("should handle missing material in composition gracefully")
    void shouldHandleMissingMaterial() throws Exception {
        var xlsx = createCompositionsXlsx(new String[][]{
                {"87316", "CONCRETO", "M3", "CONCRETO", "", "", ""},
                {"87316", "CONCRETO", "M3", "CONCRETO", "INSUMO", "99999", "100.00"},
        });

        Composition comp = new Composition("87316", "CONCRETO", "M3", "");
        when(compositionRepository.findBySinapiCode("87316")).thenReturn(Optional.empty(), Optional.of(comp));
        when(compositionRepository.save(any())).thenReturn(comp);
        when(materialRepository.findBySinapiCode("99999")).thenReturn(Optional.empty());

        ImportResult result = service.importCompositions(xlsx, "RN", LocalDate.of(2026, 1, 1), false);

        // Still counts as created (composition exists, just missing one material link)
        assertThat(result.created()).isEqualTo(1);
    }

    private InputStream createMaterialsXlsx(String[][] rows) throws Exception {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet();
        var header = sheet.createRow(0);
        header.createCell(0).setCellValue("CODIGO");
        header.createCell(1).setCellValue("DESCRICAO DO INSUMO");
        header.createCell(2).setCellValue("UNIDADE");
        header.createCell(3).setCellValue("ORIGEM DE PRECO");
        header.createCell(4).setCellValue("PRECO MEDIANO");

        for (int i = 0; i < rows.length; i++) {
            var row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(rows[i][0]);
            row.createCell(1).setCellValue(rows[i][1]);
            row.createCell(2).setCellValue(rows[i][2]);
            row.createCell(3).setCellValue(rows[i][3]);
            row.createCell(4).setCellValue(Double.parseDouble(rows[i][4]));
        }
        return toInputStream(workbook);
    }

    private InputStream createCompositionsXlsx(String[][] rows) throws Exception {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet();
        var header = sheet.createRow(0);
        header.createCell(0).setCellValue("CODIGO DA COMPOSICAO");
        header.createCell(1).setCellValue("DESCRICAO DA COMPOSICAO");
        header.createCell(2).setCellValue("UNIDADE");
        header.createCell(3).setCellValue("DESCRICAO DA CLASSE");
        header.createCell(4).setCellValue("TIPO ITEM");
        header.createCell(5).setCellValue("CODIGO ITEM");
        header.createCell(6).setCellValue("COEFICIENTE");

        for (int i = 0; i < rows.length; i++) {
            var row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(rows[i][0]);
            row.createCell(1).setCellValue(rows[i][1]);
            row.createCell(2).setCellValue(rows[i][2]);
            row.createCell(3).setCellValue(rows[i][3]);
            row.createCell(4).setCellValue(rows[i][4]);
            row.createCell(5).setCellValue(rows[i][5]);
            if (!rows[i][6].isBlank()) {
                row.createCell(6).setCellValue(Double.parseDouble(rows[i][6]));
            }
        }
        return toInputStream(workbook);
    }

    private InputStream toInputStream(XSSFWorkbook workbook) throws Exception {
        var out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
