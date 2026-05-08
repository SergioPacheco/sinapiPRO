package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.domain.BudgetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record UpdateBudgetRequest(
        @NotBlank @Size(max = 140) String title,
        @NotBlank @Size(max = 140) String customerName,
        @NotNull @Positive BigDecimal totalAmount,
        @NotNull BudgetStatus status,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        Map<String, Object> metadata
) {}
