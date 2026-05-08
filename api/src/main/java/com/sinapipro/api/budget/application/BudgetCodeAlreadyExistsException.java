package com.sinapipro.api.budget.application;

public final class BudgetCodeAlreadyExistsException extends RuntimeException {
    public BudgetCodeAlreadyExistsException(String code) {
        super("Budget code already exists: " + code);
    }
}
