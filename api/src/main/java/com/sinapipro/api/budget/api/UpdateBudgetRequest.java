package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.domain.BudgetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record UpdateBudgetRequest(
        @NotBlank @Size(max = 140) String title,
        @NotBlank @Size(max = 140) String customerName,
        @NotNull @PositiveOrZero BigDecimal totalAmount,
        @NotNull BudgetStatus status,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        Map<String, Object> metadata,
        LocalDate referenceDate,
        String state,
        String roundingMethod,
        Integer decimalPlaces,
        String itemMask
) {
    public UpdateBudgetRequest(String title, String customerName, BigDecimal totalAmount,
                               BudgetStatus status, LocalDate startDate, LocalDate endDate,
                               Map<String, Object> metadata) {
        this(title, customerName, totalAmount, status, startDate, endDate, metadata,
                null, null, null, null, null);
    }
}
