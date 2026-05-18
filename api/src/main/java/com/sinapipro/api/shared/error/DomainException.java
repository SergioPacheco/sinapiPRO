package com.sinapipro.api.shared.error;

import org.springframework.http.HttpStatus;

/**
 * Base exception for all domain/business errors.
 * Each module creates its own subclass (e.g., BudgetException, MeasurementException).
 * Inspired by SGN3's BusinessException pattern: one exception class per module,
 * with typed error codes that map to user-facing messages.
 */
public abstract class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    protected DomainException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    protected DomainException(ErrorCode errorCode, Object... args) {
        super(errorCode.message().formatted(args));
        this.errorCode = errorCode;
    }

    protected DomainException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() { return errorCode; }
    public HttpStatus status() { return errorCode.status(); }
    public String code() { return errorCode.code(); }

    /**
     * Contract for module error codes. Each module implements this as an enum.
     */
    public interface ErrorCode {
        String code();
        String message();
        HttpStatus status();
    }
}
