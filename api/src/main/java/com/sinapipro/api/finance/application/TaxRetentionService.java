package com.sinapipro.api.finance.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class TaxRetentionService {

    // Alíquotas padrão (configuráveis futuramente via tabela)
    private static final Map<TaxType, BigDecimal> DEFAULT_RATES = Map.of(
            TaxType.ISS, new BigDecimal("5.0000"),
            TaxType.INSS, new BigDecimal("11.0000"),
            TaxType.IR, new BigDecimal("1.5000"),
            TaxType.PIS, new BigDecimal("0.6500"),
            TaxType.COFINS, new BigDecimal("3.0000")
    );

    // Valor mínimo para retenção de IR (R$ 10,00 conforme legislação)
    private static final BigDecimal IR_MINIMUM = new BigDecimal("10.00");

    private final TaxRetentionRepository retentionRepository;
    private final PayableRepository payableRepository;

    public TaxRetentionService(TaxRetentionRepository retentionRepository,
                               PayableRepository payableRepository) {
        this.retentionRepository = retentionRepository;
        this.payableRepository = payableRepository;
    }

    /**
     * Calcula e persiste retenções para um payable com base nos impostos aplicáveis.
     */
    public List<TaxRetention> calculateRetentions(UUID payableId, List<TaxType> applicableTaxes) {
        var payable = payableRepository.findById(payableId)
                .orElseThrow(() -> new IllegalArgumentException("Payable not found: " + payableId));

        var existing = retentionRepository.findByPayableId(payableId);
        if (!existing.isEmpty()) {
            throw new IllegalStateException("Retentions already calculated for payable: " + payableId);
        }

        var baseAmount = payable.getAmount();
        var retentions = new ArrayList<TaxRetention>();

        for (var taxType : applicableTaxes) {
            var rate = DEFAULT_RATES.getOrDefault(taxType, BigDecimal.ZERO);
            var retention = new TaxRetention(payableId, taxType, baseAmount, rate);

            // IR: não reter se valor < mínimo
            if (taxType == TaxType.IR && retention.getAmount().compareTo(IR_MINIMUM) < 0) {
                continue;
            }

            retentions.add(retention);
        }

        return retentionRepository.saveAll(retentions);
    }

    /**
     * Calcula retenções com alíquotas customizadas.
     */
    public List<TaxRetention> calculateRetentions(UUID payableId, Map<TaxType, BigDecimal> customRates) {
        var payable = payableRepository.findById(payableId)
                .orElseThrow(() -> new IllegalArgumentException("Payable not found: " + payableId));

        var baseAmount = payable.getAmount();
        var retentions = new ArrayList<TaxRetention>();

        for (var entry : customRates.entrySet()) {
            retentions.add(new TaxRetention(payableId, entry.getKey(), baseAmount, entry.getValue()));
        }

        return retentionRepository.saveAll(retentions);
    }

    public List<TaxRetention> findByPayable(UUID payableId) {
        return retentionRepository.findByPayableId(payableId);
    }

    /**
     * Retorna o valor líquido (total - retenções) de um payable.
     */
    public BigDecimal getNetAmount(UUID payableId) {
        var payable = payableRepository.findById(payableId)
                .orElseThrow(() -> new IllegalArgumentException("Payable not found: " + payableId));
        var totalRetentions = retentionRepository.findByPayableId(payableId).stream()
                .map(TaxRetention::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return payable.getAmount().subtract(totalRetentions);
    }
}
