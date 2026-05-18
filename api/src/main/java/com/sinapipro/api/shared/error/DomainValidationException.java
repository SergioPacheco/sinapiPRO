package com.sinapipro.api.shared.error;

import org.springframework.http.HttpStatus;

public class DomainValidationException extends DomainException {

    public DomainValidationException(String message) {
        super(new SimpleErrorCode("validation-error", message, HttpStatus.UNPROCESSABLE_ENTITY));
    }
}
