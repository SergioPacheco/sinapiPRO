package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        UUID projectId,
        String code,
        String title,
        String customerName,
        BigDecimal totalAmount,
        BudgetStatus status,
        boolean active,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate referenceDate,
        String state,
        String roundingMethod,
        Integer decimalPlaces,
        String itemMask,
        Map<String, Object> metadata,
        Instant createdAt,
        Instant updatedAt
) {
    public static BudgetResponse from(Budget budget) {
        return new BudgetResponse(
                budget.getId(), budget.getProjectId(), budget.getCode(), budget.getTitle(), budget.getCustomerName(),
                budget.getTotalAmount(), budget.getStatus(), budget.isActive(),
                budget.getStartDate(), budget.getEndDate(),
                budget.getReferenceDate(), budget.getState(), budget.getRoundingMethod(),
                budget.getDecimalPlaces(), budget.getItemMask(),
                budget.getMetadata(), budget.getCreatedAt(), budget.getUpdatedAt());
    }
}
