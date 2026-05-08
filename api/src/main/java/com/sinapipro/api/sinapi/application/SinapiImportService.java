package com.sinapipro.api.sinapi.application;

import com.sinapipro.api.sinapi.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SinapiImportService {

    private final MaterialRepository materialRepository;
    private final CompositionRepository compositionRepository;

    public SinapiImportService(MaterialRepository materialRepository, CompositionRepository compositionRepository) {
        this.materialRepository = materialRepository;
        this.compositionRepository = compositionRepository;
    }

    /**
     * Import materials with prices from CSV.
     * Expected format: sinapi_code;description;unit;origin;state;reference_month;price
     */
    @Transactional
    public ImportResult importMaterials(InputStream csvStream, String separator) {
        List<String> lines = readLines(csvStream);
        int created = 0, updated = 0, errors = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) { // skip header
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                String[] parts = line.split(separator, -1);
                if (parts.length < 7) { errors++; errorMessages.add("Line " + (i+1) + ": insufficient columns"); continue; }

                String code = parts[0].trim();
                String description = parts[1].trim();
                String unit = parts[2].trim();
                String origin = parts[3].trim();
                String state = parts[4].trim();
                LocalDate refMonth = LocalDate.parse(parts[5].trim());
                BigDecimal price = new BigDecimal(parts[6].trim().replace(",", "."));

                Material material = materialRepository.findBySinapiCode(code)
                        .orElseGet(() -> materialRepository.save(new Material(code, description, unit, origin)));

                // Add price if not exists
                boolean priceExists = material.getPrices().stream()
                        .anyMatch(p -> p.getState().equals(state) && p.getReferenceMonth().equals(refMonth));
                if (!priceExists) {
                    material.getPrices().add(new MaterialPrice(material, state, refMonth, price));
                    materialRepository.save(material);
                    created++;
                } else {
                    updated++;
                }
            } catch (Exception e) {
                errors++;
                errorMessages.add("Line " + (i+1) + ": " + e.getMessage());
            }
        }
        return new ImportResult("materials", created, updated, errors, errorMessages);
    }

    /**
     * Import compositions from CSV.
     * Expected format: sinapi_code;description;unit;group;material_code;coefficient
     */
    @Transactional
    public ImportResult importCompositions(InputStream csvStream, String separator) {
        List<String> lines = readLines(csvStream);
        int created = 0, updated = 0, errors = 0;
        List<String> errorMessages = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                String[] parts = line.split(separator, -1);
                if (parts.length < 6) { errors++; errorMessages.add("Line " + (i+1) + ": insufficient columns"); continue; }

                String compCode = parts[0].trim();
                String description = parts[1].trim();
                String unit = parts[2].trim();
                String group = parts[3].trim();
                String materialCode = parts[4].trim();
                BigDecimal coefficient = new BigDecimal(parts[5].trim().replace(",", "."));

                Composition composition = compositionRepository.findBySinapiCode(compCode)
                        .orElseGet(() -> compositionRepository.save(new Composition(compCode, description, unit, group)));

                Material material = materialRepository.findBySinapiCode(materialCode).orElse(null);
                if (material == null) {
                    errors++;
                    errorMessages.add("Line " + (i+1) + ": material not found: " + materialCode);
                    continue;
                }

                boolean itemExists = composition.getItems().stream()
                        .anyMatch(ci -> ci.getMaterial().getSinapiCode().equals(materialCode));
                if (!itemExists) {
                    composition.addItem(material, coefficient);
                    compositionRepository.save(composition);
                    created++;
                } else {
                    updated++;
                }
            } catch (Exception e) {
                errors++;
                errorMessages.add("Line " + (i+1) + ": " + e.getMessage());
            }
        }
        return new ImportResult("compositions", created, updated, errors, errorMessages);
    }

    private List<String> readLines(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines().toList();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read CSV: " + e.getMessage(), e);
        }
    }

    public record ImportResult(String type, int created, int updated, int errors, List<String> errorMessages) {}
}
