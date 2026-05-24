package com.sinapipro.api.sinapi.application;

import module java.base;

import com.sinapipro.api.sinapi.domain.*;
import jakarta.persistence.EntityManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SinapiImportService {

    private final MaterialRepository materialRepository;
    private final CompositionRepository compositionRepository;
    private final EntityManager entityManager;

    public SinapiImportService(MaterialRepository materialRepository, CompositionRepository compositionRepository,
                               EntityManager entityManager) {
        this.materialRepository = materialRepository;
        this.compositionRepository = compositionRepository;
        this.entityManager = entityManager;
    }

    /**
     * Importa insumos do arquivo SINAPI_Preco_Ref_Insumos_{UF}_{AAAAMM}.xlsx
     */
    @Transactional
    public ImportResult importMaterials(InputStream xlsxStream, String state, LocalDate referenceMonth, boolean desonerated) {
        int created = 0, updated = 0, errors = 0;
        var errorMessages = new ArrayList<String>();

        try (var workbook = new XSSFWorkbook(xlsxStream)) {
            var sheet = workbook.getSheetAt(0);
            int headerRow = findHeaderRow(sheet, "CODIGO");
            if (headerRow < 0) {
                return new ImportResult("materials", 0, 0, 1, List.of("Cabeçalho não encontrado. Esperado coluna 'CODIGO'."));
            }

            var colMap = mapColumns(sheet.getRow(headerRow));
            int colCode = findCol(colMap, "CODIGO");
            int colDesc = findCol(colMap, "DESCRICAO DO INSUMO", "DESCRICAO");
            int colUnit = findCol(colMap, "UNIDADE DE MEDIDA", "UNIDADE");
            int colOrigin = findCol(colMap, "ORIGEM DO PRECO", "ORIGEM DE PRECO", "ORIGEM");
            int colPrice = findCol(colMap, "PRECO MEDIANO R$", "PRECO MEDIANO", "PRECO");

            for (int i = headerRow + 1; i <= sheet.getLastRowNum(); i++) {
                var row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    var code = getCellString(row, colCode).trim();
                    if (code.isEmpty()) continue;

                    var description = getCellString(row, colDesc).trim();
                    var unit = getCellString(row, colUnit).trim();
                    var origin = getCellString(row, colOrigin).trim();
                    var price = getCellDecimal(row, colPrice);

                    if (description.isEmpty() || price == null) continue;

                    var material = materialRepository.findBySinapiCode(code)
                            .orElseGet(() -> materialRepository.save(new Material(code, description, unit, origin)));

                    boolean priceExists = material.getPrices().stream()
                            .anyMatch(p -> p.getState().equals(state) && p.getReferenceMonth().equals(referenceMonth) && p.isDesonerated() == desonerated);
                    if (!priceExists) {
                        material.getPrices().add(new MaterialPrice(material, state, referenceMonth, price, desonerated));
                        materialRepository.save(material);
                        created++;
                    } else {
                        updated++;
                    }
                } catch (Exception e) {
                    errors++;
                    if (errorMessages.size() < 20) errorMessages.add("Linha " + (i + 1) + ": " + e.getMessage());
                }
                if (i % 500 == 0) { entityManager.flush(); entityManager.clear(); }
            }
        } catch (Exception e) {
            return new ImportResult("materials", created, updated, errors + 1, List.of("Erro ao ler arquivo: " + e.getMessage()));
        }
        return new ImportResult("materials", created, updated, errors, errorMessages);
    }

    /**
     * Importa composições do arquivo Sintético ou Analítico.
     * Sintético: cadastra composição + custo total (sem itens).
     * Analítico: cadastra composição + itens com coeficientes.
     */
    @Transactional
    public ImportResult importCompositions(InputStream xlsxStream, String state, LocalDate referenceMonth, boolean desonerated) {
        int created = 0, updated = 0, errors = 0;
        var errorMessages = new ArrayList<String>();

        try (var workbook = new XSSFWorkbook(xlsxStream)) {
            var sheet = workbook.getSheetAt(0);
            int headerRow = findHeaderRow(sheet, "CODIGO");
            if (headerRow < 0) {
                return new ImportResult("compositions", 0, 0, 1, List.of("Cabeçalho não encontrado."));
            }

            var colMap = mapColumns(sheet.getRow(headerRow));
            int colCompCode = findCol(colMap, "CODIGO DA COMPOSICAO", "CODIGO  DA COMPOSICAO", "CODIGO");
            int colCompDesc = findCol(colMap, "DESCRICAO DA COMPOSICAO", "DESCRICAO");
            int colCompUnit = findCol(colMap, "UNIDADE");
            int colClass = findCol(colMap, "DESCRICAO DA CLASSE");
            // Analytic-specific columns
            int colItemType = findCol(colMap, "TIPO ITEM");
            int colItemCode = findCol(colMap, "CODIGO ITEM");
            int colCoef = findCol(colMap, "COEFICIENTE");

            boolean isAnalytic = colItemType >= 0 && colItemCode >= 0 && colCoef >= 0;

            for (int i = headerRow + 1; i <= sheet.getLastRowNum(); i++) {
                var row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    var compCode = getCellString(row, colCompCode).trim();
                    if (compCode.isEmpty()) continue;

                    var compDesc = getCellString(row, colCompDesc).trim();
                    var compUnit = getCellString(row, colCompUnit).trim();
                    var groupName = colClass >= 0 ? getCellString(row, colClass).trim() : "";

                    if (isAnalytic) {
                        var itemType = getCellString(row, colItemType).trim();
                        var itemCode = getCellString(row, colItemCode).trim();

                        if (itemType.isEmpty() && itemCode.isEmpty()) {
                            compositionRepository.findBySinapiCode(compCode)
                                    .orElseGet(() -> compositionRepository.save(new Composition(compCode, compDesc, compUnit, groupName)));
                            created++;
                        } else if (!itemCode.isEmpty()) {
                            var coefficient = getCellDecimal(row, colCoef);
                            if (coefficient == null) continue;

                            var comp = compositionRepository.findBySinapiCode(compCode).orElse(null);
                            if (comp == null) continue;

                            var material = materialRepository.findBySinapiCode(itemCode).orElse(null);
                            if (material != null) {
                                boolean exists = comp.getItems().stream()
                                        .anyMatch(ci -> ci.getMaterial().getSinapiCode().equals(itemCode));
                                if (!exists) {
                                    comp.addItem(material, coefficient);
                                }
                            }
                        }
                    } else {
                        if (compDesc.isEmpty()) continue;
                        compositionRepository.findBySinapiCode(compCode)
                                .orElseGet(() -> compositionRepository.save(new Composition(compCode, compDesc, compUnit, groupName)));
                        created++;
                    }
                } catch (Exception e) {
                    errors++;
                    if (errorMessages.size() < 20) errorMessages.add("Linha " + (i + 1) + ": " + e.getMessage());
                }
                if (i % 500 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        } catch (Exception e) {
            return new ImportResult("compositions", created, updated, errors + 1, List.of("Erro ao ler arquivo: " + e.getMessage()));
        }
        return new ImportResult("compositions", created, updated, errors, errorMessages);
    }

    private int findHeaderRow(Sheet sheet, String keyword) {
        for (int i = 0; i <= Math.min(sheet.getLastRowNum(), 10); i++) {
            var row = sheet.getRow(i);
            if (row == null) continue;
            for (int j = 0; j < row.getLastCellNum(); j++) {
                if (getCellString(row, j).toUpperCase().trim().contains(keyword.toUpperCase())) return i;
            }
        }
        return -1;
    }

    private Map<String, Integer> mapColumns(Row headerRow) {
        var map = new HashMap<String, Integer>();
        if (headerRow == null) return map;
        for (int j = 0; j < headerRow.getLastCellNum(); j++) {
            var val = getCellString(headerRow, j).toUpperCase().trim();
            if (!val.isEmpty()) map.put(val, j);
        }
        return map;
    }

    private int findCol(Map<String, Integer> colMap, String... names) {
        for (var name : names) {
            var idx = colMap.get(name.toUpperCase());
            if (idx != null) return idx;
        }
        return -1;
    }

    private String getCellString(Row row, int col) {
        if (col < 0 || col >= row.getLastCellNum()) return "";
        var cell = row.getCell(col);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double d = cell.getNumericCellValue();
                yield (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private BigDecimal getCellDecimal(Row row, int col) {
        if (col < 0 || col >= row.getLastCellNum()) return null;
        var cell = row.getCell(col);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                var s = cell.getStringCellValue().trim().replace(".", "").replace(",", ".");
                yield s.isEmpty() ? null : new BigDecimal(s);
            }
            default -> null;
        };
    }

    public record ImportResult(String type, int created, int updated, int errors, List<String> errorMessages) {}

    /**
     * Importa ZIP completo da Caixa. Detecta UF, mês e desonerado pelo nome.
     * Importa automaticamente: Insumos + Composições Analítico.
     * Ignora: PDFs, Sintético, Notas.
     */
    public com.sinapipro.api.sinapi.api.CompositionController.ZipImportResult importZip(InputStream zipStream, String zipFilename) {
        // Detectar parâmetros pelo nome do ZIP
        String state = "SP";
        LocalDate referenceMonth = LocalDate.now().withDayOfMonth(1);
        boolean desonerated = false;

        if (zipFilename != null) {
            var ufMatch = java.util.regex.Pattern.compile("_([A-Z]{2})_(\\d{6})").matcher(zipFilename);
            if (ufMatch.find()) {
                state = ufMatch.group(1);
                var yyyymm = ufMatch.group(2);
                referenceMonth = LocalDate.of(Integer.parseInt(yyyymm.substring(0, 4)), Integer.parseInt(yyyymm.substring(4, 6)), 1);
            }
            desonerated = zipFilename.contains("Desonerado") && !zipFilename.contains("NaoDesonerado");
        }

        ImportResult materialsResult = new ImportResult("materials", 0, 0, 0, List.of());
        ImportResult compositionsResult = new ImportResult("compositions", 0, 0, 0, List.of());

        try (var zis = new java.util.zip.ZipInputStream(zipStream)) {
            java.util.zip.ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                var name = entry.getName();
                if (entry.isDirectory() || !name.endsWith(".xlsx")) { zis.closeEntry(); continue; }

                // Ler o conteúdo do entry em memória (ZipInputStream não suporta mark/reset)
                var baos = new java.io.ByteArrayOutputStream();
                zis.transferTo(baos);
                var bytes = baos.toByteArray();

                if (name.contains("Preco_Ref_Insumos")) {
                    materialsResult = importMaterials(new java.io.ByteArrayInputStream(bytes), state, referenceMonth, desonerated);
                } else if (name.contains("Composicoes_Analitico")) {
                    compositionsResult = importCompositions(new java.io.ByteArrayInputStream(bytes), state, referenceMonth, desonerated);
                }
                // Ignora: Sintetico, PDFs, Família, Notas
                zis.closeEntry();
            }
        } catch (Exception e) {
            return new com.sinapipro.api.sinapi.api.CompositionController.ZipImportResult(
                    state, referenceMonth.toString(), desonerated,
                    new ImportResult("materials", 0, 0, 1, List.of("Erro ao processar ZIP: " + e.getMessage())),
                    compositionsResult);
        }

        return new com.sinapipro.api.sinapi.api.CompositionController.ZipImportResult(
                state, referenceMonth.toString(), desonerated, materialsResult, compositionsResult);
    }
}
