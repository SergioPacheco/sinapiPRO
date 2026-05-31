package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.domain.BudgetStatus;

import java.util.UUID;

public record BudgetFilter(UUID projectId, BudgetStatus status, String customerName) {
    public BudgetFilter(BudgetStatus status, String customerName) {
        this(null, status, customerName);
    }
}
