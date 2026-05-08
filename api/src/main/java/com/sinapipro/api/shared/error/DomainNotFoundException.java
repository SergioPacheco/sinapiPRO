package com.sinapipro.api.shared.error;

public class DomainNotFoundException extends RuntimeException {

    public DomainNotFoundException(String message) {
        super(message);
    }
}
