package com.sinapipro.api.shared.error;

import org.springframework.http.HttpStatus;

/**
 * Simple inline error code for generic exceptions.
 * Modules should prefer their own enum implementing DomainException.ErrorCode.
 */
public record SimpleErrorCode(String code, String message, HttpStatus status) implements DomainException.ErrorCode {}
