package com.sinapipro.api.finance.application;

import com.sinapipro.api.commercial.domain.*;
import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public MonetaryIndexService(MonetaryIndexValueRepository indexValueRepo,
                                 SaleInstallmentRepository saleInstallmentRepo) {
        this.indexValueRepo = indexValueRepo;
        this.saleInstallmentRepo = saleInstallmentRepo;
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
}
