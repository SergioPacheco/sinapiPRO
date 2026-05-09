package com.sinapipro.api.shared.error;

public non-sealed class DomainConflictException extends DomainException {

    public DomainConflictException(String message) {
        super(message);
    }
}
