package com.sinapipro.api.sinapi.application;

import module java.base;

import com.sinapipro.api.sinapi.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SinapiImportService {

    private final MaterialRepository materialRepository;
    private final CompositionRepository compositionRepository;

    public SinapiImportService(MaterialRepository materialRepository, CompositionRepository compositionRepository) {
        this.materialRepository = materialRepository;
        this.compositionRepository = compositionRepository;
    }

    @Transactional
    public ImportResult importMaterials(InputStream csvStream, String separator) {
        var lines = readLines(csvStream);
        var created = 0;
        var updated = 0;
        var errors = 0;
        var errorMessages = new ArrayList<String>();

        for (int i = 1; i < lines.size(); i++) {
            var line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                var parts = line.split(separator, -1);
                if (parts.length < 7) { errors++; errorMessages.add("Line " + (i+1) + ": insufficient columns"); continue; }

                var code = parts[0].trim();
                var description = parts[1].trim();
                var unit = parts[2].trim();
                var origin = parts[3].trim();
                var state = parts[4].trim();
                var refMonth = LocalDate.parse(parts[5].trim());
                var price = new BigDecimal(parts[6].trim().replace(",", "."));

                var material = materialRepository.findBySinapiCode(code)
                        .orElseGet(() -> materialRepository.save(new Material(code, description, unit, origin)));

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

    @Transactional
    public ImportResult importCompositions(InputStream csvStream, String separator) {
        var lines = readLines(csvStream);
        var created = 0;
        var updated = 0;
        var errors = 0;
        var errorMessages = new ArrayList<String>();

        for (int i = 1; i < lines.size(); i++) {
            var line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                var parts = line.split(separator, -1);
                if (parts.length < 6) { errors++; errorMessages.add("Line " + (i+1) + ": insufficient columns"); continue; }

                var compCode = parts[0].trim();
                var description = parts[1].trim();
                var unit = parts[2].trim();
                var group = parts[3].trim();
                var materialCode = parts[4].trim();
                var coefficient = new BigDecimal(parts[5].trim().replace(",", "."));

                var composition = compositionRepository.findBySinapiCode(compCode)
                        .orElseGet(() -> compositionRepository.save(new Composition(compCode, description, unit, group)));

                var material = materialRepository.findBySinapiCode(materialCode).orElse(null);
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
        try (var reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines().toList();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read CSV: " + e.getMessage(), e);
        }
    }

    public record ImportResult(String type, int created, int updated, int errors, List<String> errorMessages) {}
}
