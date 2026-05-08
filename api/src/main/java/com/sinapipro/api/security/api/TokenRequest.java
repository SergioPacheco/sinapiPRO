package com.sinapipro.api.security.api;

import jakarta.validation.constraints.NotNull;

public record TokenRequest(
        @NotNull TokenGrantType grantType,
        String username,
        String password,
        String refreshToken
) {
}
