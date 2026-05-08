package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.domain.BudgetStatus;

public record BudgetFilter(BudgetStatus status, String customerName) {
}
