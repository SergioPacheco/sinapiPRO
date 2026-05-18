package com.sinapipro.api.shared.error;

import org.springframework.http.HttpStatus;

public class DomainConflictException extends DomainException {

    public DomainConflictException(String message) {
        super(new SimpleErrorCode("resource-conflict", message, HttpStatus.CONFLICT));
    }
}
