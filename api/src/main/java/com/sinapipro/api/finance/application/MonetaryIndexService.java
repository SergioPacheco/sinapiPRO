package com.sinapipro.api.finance.application;

import com.sinapipro.api.commercial.domain.*;
import com.sinapipro.api.contract.domain.ChangeOrder;
import com.sinapipro.api.contract.domain.Contract;
import com.sinapipro.api.contract.domain.ContractRepository;
import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Sprint 13 — Índices Econômicos e Reajustes.
 */
@Service
@Transactional
public class MonetaryIndexService {

    private final MonetaryIndexValueRepository indexValueRepo;
    private final SaleInstallmentRepository saleInstallmentRepo;
    private final ContractRepository contractRepo;
    private final RestClient restClient;

    public MonetaryIndexService(MonetaryIndexValueRepository indexValueRepo,
                                 SaleInstallmentRepository saleInstallmentRepo,
                                 ContractRepository contractRepo) {
        this.indexValueRepo = indexValueRepo;
        this.saleInstallmentRepo = saleInstallmentRepo;
        this.contractRepo = contractRepo;
        this.restClient = RestClient.create("https://servicodados.ibge.gov.br/api/v3");
    }

    /** 13.1 — Cadastrar valor mensal de índice */
    public MonetaryIndexValue addIndexValue(UUID indexId, LocalDate referenceMonth, BigDecimal value, BigDecimal accumulated) {
        return indexValueRepo.save(new MonetaryIndexValue(indexId, referenceMonth, value, accumulated));
    }

    /** 13.2 — Reajuste automático de parcelas de venda por índice */
    public int adjustSaleInstallments(UUID contractId, UUID indexId, LocalDate baseMonth, LocalDate currentMonth) {
        var baseValue = indexValueRepo.findByIndexIdAndReferenceMonth(indexId, baseMonth);
        var currentValue = indexValueRepo.findByIndexIdAndReferenceMonth(indexId, currentMonth);
        if (baseValue.isEmpty() || currentValue.isEmpty()) {
            throw new IllegalArgumentException("Index values not found for the specified months");
        }

        var factor = currentValue.get().getAccumulated()
                .divide(baseValue.get().getAccumulated(), 6, RoundingMode.HALF_UP);

        var installments = saleInstallmentRepo.findByContractIdAndStatus(contractId, "FUTURE");
        installments.forEach(i -> i.adjust(factor));
        saleInstallmentRepo.saveAll(installments);
        return installments.size();
    }

    /** 13.4 — Simulação de reajuste (what-if) sem persistir */
    public SimulationResult simulate(UUID contractId, UUID indexId, LocalDate baseMonth, LocalDate currentMonth) {
        var baseValue = indexValueRepo.findByIndexIdAndReferenceMonth(indexId, baseMonth);
        var currentValue = indexValueRepo.findByIndexIdAndReferenceMonth(indexId, currentMonth);
        if (baseValue.isEmpty() || currentValue.isEmpty()) {
            return new SimulationResult(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }

        var factor = currentValue.get().getAccumulated()
                .divide(baseValue.get().getAccumulated(), 6, RoundingMode.HALF_UP);

        var installments = saleInstallmentRepo.findByContractIdAndStatus(contractId, "FUTURE");
        var originalTotal = installments.stream().map(SaleInstallment::getOriginalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        var adjustedTotal = originalTotal.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        var difference = adjustedTotal.subtract(originalTotal);

        return new SimulationResult(factor, originalTotal, adjustedTotal, installments.size());
    }

    /** 13.5 — Importação de índices (batch) */
    public int importValues(UUID indexId, List<IndexEntry> entries) {
        var values = entries.stream()
                .map(e -> new MonetaryIndexValue(indexId, e.referenceMonth(), e.value(), e.accumulated()))
                .toList();
        indexValueRepo.saveAll(values);
        return values.size();
    }

    public record IndexEntry(LocalDate referenceMonth, BigDecimal value, BigDecimal accumulated) {}
    public record SimulationResult(BigDecimal factor, BigDecimal originalTotal, BigDecimal adjustedTotal, int installmentCount) {}

    /** 13.3 — Reajuste de contratos de empreitada por índice */
    public ContractAdjustmentResult adjustContract(UUID contractId, UUID indexId, LocalDate baseMonth, LocalDate currentMonth) {
        var baseValue = indexValueRepo.findByIndexIdAndReferenceMonth(indexId, baseMonth);
        var currentValue = indexValueRepo.findByIndexIdAndReferenceMonth(indexId, currentMonth);
        if (baseValue.isEmpty() || currentValue.isEmpty()) {
            throw new IllegalArgumentException("Index values not found for the specified months");
        }

        var factor = currentValue.get().getAccumulated()
                .divide(baseValue.get().getAccumulated(), 6, RoundingMode.HALF_UP);

        var contract = contractRepo.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        var originalValue = contract.getUpdatedValue();
        var adjustedValue = originalValue.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        var difference = adjustedValue.subtract(originalValue);

        // Criar aditivo de reajuste
        var nextNumber = contract.getChangeOrders().size() + 1;
        var changeOrder = new ChangeOrder(contract, nextNumber,
                "Reajuste índice " + currentMonth + " (fator " + factor + ")", difference, "Reajuste contratual por índice econômico");
        changeOrder.approve();
        contract.getChangeOrders().add(changeOrder);
        contractRepo.save(contract);

        return new ContractAdjustmentResult(contractId, factor, originalValue, adjustedValue, difference);
    }

    /** 13.5 — Importação automática de índices via API IBGE/SIDRA */
    public int importFromIbge(UUID indexId, String ibgeSeriesCode, LocalDate from, LocalDate to) {
        // IBGE SIDRA API: /agregados/{cod}/periodos/{periodos}/variaveis/{var}
        // Simplified: fetch JSON and parse
        try {
            var fromStr = from.getYear() + String.format("%02d", from.getMonthValue());
            var toStr = to.getYear() + String.format("%02d", to.getMonthValue());
            var url = "/agregados/" + ibgeSeriesCode + "/periodos/" + fromStr + "-" + toStr + "/variaveis/63?localidades=N1[all]";

            var response = restClient.get().uri(url).retrieve().body(String.class);
            if (response == null || response.isBlank()) return 0;

            // Parse IBGE JSON response (simplified — production would use Jackson)
            var entries = parseIbgeResponse(response, indexId);
            indexValueRepo.saveAll(entries);
            return entries.size();
        } catch (Exception e) {
            // Graceful degradation: log and return 0
            return 0;
        }
    }

    private List<MonetaryIndexValue> parseIbgeResponse(String json, UUID indexId) {
        // IBGE returns: [{"id":"63","variavel":"...","resultados":[{"series":[{"localidade":...,"serie":{"202501":"0.64",...}}]}]}]
        var entries = new java.util.ArrayList<MonetaryIndexValue>();
        // Simple regex-based extraction for month/value pairs
        var pattern = java.util.regex.Pattern.compile("\"(\\d{6})\"\\s*:\\s*\"([\\d.,]+)\"");
        var matcher = pattern.matcher(json);
        BigDecimal accumulated = BigDecimal.ONE;
        while (matcher.find()) {
            var yearMonth = matcher.group(1);
            var valueStr = matcher.group(2).replace(",", ".");
            try {
                var value = new BigDecimal(valueStr);
                var year = Integer.parseInt(yearMonth.substring(0, 4));
                var month = Integer.parseInt(yearMonth.substring(4, 6));
                var refMonth = LocalDate.of(year, month, 1);
                accumulated = accumulated.multiply(BigDecimal.ONE.add(value.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)));
                entries.add(new MonetaryIndexValue(indexId, refMonth, value, accumulated.setScale(6, RoundingMode.HALF_UP)));
            } catch (NumberFormatException ignored) {}
        }
        return entries;
    }

    public record ContractAdjustmentResult(UUID contractId, BigDecimal factor, BigDecimal originalValue,
                                            BigDecimal adjustedValue, BigDecimal difference) {}
}
