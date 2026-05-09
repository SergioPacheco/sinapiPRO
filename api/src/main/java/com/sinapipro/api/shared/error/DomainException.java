package com.sinapipro.api.shared.error;

/**
 * Sealed exception hierarchy for domain errors.
 * Enables exhaustive pattern matching in exception handlers.
 */
public sealed class DomainException extends RuntimeException
        permits DomainNotFoundException, DomainConflictException, DomainValidationException {

    public DomainException(String message) {
        super(message);
    }
}
