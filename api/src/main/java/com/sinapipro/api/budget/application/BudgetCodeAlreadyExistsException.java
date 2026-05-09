package com.sinapipro.api.budget.application;

import com.sinapipro.api.shared.error.DomainConflictException;

public final class BudgetCodeAlreadyExistsException extends DomainConflictException {
    public BudgetCodeAlreadyExistsException(String code) {
        super("Budget code already exists: " + code);
    }
}
