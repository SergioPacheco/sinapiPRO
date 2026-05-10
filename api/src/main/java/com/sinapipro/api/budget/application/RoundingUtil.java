package com.sinapipro.api.budget.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilitário de arredondamento conforme normas brasileiras.
 * - TRUNCATE: obrigatório para obras públicas (Cartilha TCU)
 * - ROUND_ABNT: regra do 5 (banker's rounding) — NBR 5891
 * - ROUND_SIMPLE: arredondamento comercial (≥5 para cima)
 */
public final class RoundingUtil {

    private RoundingUtil() {}

    public static BigDecimal apply(BigDecimal value, String method, int decimalPlaces) {
        if (value == null) return BigDecimal.ZERO;
        return switch (method != null ? method : "TRUNCATE") {
            case "TRUNCATE" -> value.setScale(decimalPlaces, RoundingMode.DOWN);
            case "ROUND_ABNT" -> value.setScale(decimalPlaces, RoundingMode.HALF_EVEN);
            case "ROUND_SIMPLE" -> value.setScale(decimalPlaces, RoundingMode.HALF_UP);
            default -> value.setScale(decimalPlaces, RoundingMode.DOWN);
        };
    }
}
