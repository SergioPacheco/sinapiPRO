package com.sinapipro.api.security.api;

import com.sinapipro.api.security.application.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenService jwtTokenService;

    public AuthController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    TokenResponse token(@Valid @RequestBody TokenRequest request) {
        return switch (request.grantType()) {
            case PASSWORD -> jwtTokenService.issueFromPassword(request.username(), request.password());
            case REFRESH_TOKEN -> jwtTokenService.issueFromRefreshToken(request.refreshToken());
        };
    }
}
