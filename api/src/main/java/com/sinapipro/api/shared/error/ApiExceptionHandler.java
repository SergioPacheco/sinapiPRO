package com.sinapipro.api.shared.error;

import module java.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized exception handler — returns ProblemDetail (RFC 9457).
 * Maps DomainException subclasses (module-specific) to proper HTTP responses.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException exception, HttpServletRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(exception.status(), exception.getMessage());
        enrich(problem, exception.code(), request);
        return problem;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    ProblemDetail handleValidation(Exception exception, HttpServletRequest request) {
        var violations = switch (exception) {
            case MethodArgumentNotValidException e ->
                    e.getBindingResult().getFieldErrors().stream()
                            .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                            .toList();
            case BindException e ->
                    e.getBindingResult().getFieldErrors().stream()
                            .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                            .toList();
            default -> List.<Violation>of();
        };

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Erro de validação");
        enrich(problem, "validation-error", request);
        problem.setProperty("violations", violations);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Erro de validação");
        enrich(problem, "constraint-violation", request);
        problem.setProperty("violations", exception.getConstraintViolations().stream()
                .map(v -> new Violation(v.getPropertyPath().toString(), v.getMessage()))
                .toList());
        return problem;
    }

    @ExceptionHandler({JwtException.class, AccessDeniedException.class, org.springframework.security.core.AuthenticationException.class})
    ProblemDetail handleSecurity(Exception exception, HttpServletRequest request) {
        var status = switch (exception) {
            case AccessDeniedException _ -> HttpStatus.FORBIDDEN;
            default                      -> HttpStatus.UNAUTHORIZED;
        };
        var code = switch (exception) {
            case AccessDeniedException _ -> "access-denied";
            default                      -> "unauthorized";
        };
        var problem = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        enrich(problem, code, request);
        return problem;
    }

    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail handleIllegalState(IllegalStateException exception, HttpServletRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        enrich(problem, "illegal-state", request);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("[{}] {} at {}", exception.getClass().getSimpleName(), exception.getMessage(), request.getRequestURI(), exception);
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        enrich(problem, "internal-error", request);
        return problem;
    }

    private void enrich(ProblemDetail problem, String code, HttpServletRequest request) {
        problem.setType(URI.create("https://sinapipro.dev/problems/" + code));
        problem.setTitle(code);
        problem.setProperty("path", request.getRequestURI());
        problem.setProperty("timestamp", OffsetDateTime.now());
    }

    private record Violation(String field, String message) {}
}
