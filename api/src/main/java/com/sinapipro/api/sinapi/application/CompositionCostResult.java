package com.sinapipro.api.sinapi.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CompositionCostResult(
        String sinapiCode,
        String description,
        String unit,
        String state,
        LocalDate referenceMonth,
        BigDecimal totalUnitCost,
        List<ItemCost> items
) {
    public record ItemCost(
            String materialCode,
            String materialDescription,
            String materialUnit,
            BigDecimal coefficient,
            BigDecimal unitPrice,
            BigDecimal cost
    ) {}
}
