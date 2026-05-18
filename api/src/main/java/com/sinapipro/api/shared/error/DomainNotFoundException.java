package com.sinapipro.api.shared.error;

import org.springframework.http.HttpStatus;

/**
 * Generic not-found exception for modules that don't yet have their own exception class.
 */
public class DomainNotFoundException extends DomainException {

    public DomainNotFoundException(String resourceDescription) {
        super(new SimpleErrorCode("resource-not-found", resourceDescription, HttpStatus.NOT_FOUND));
    }
}
