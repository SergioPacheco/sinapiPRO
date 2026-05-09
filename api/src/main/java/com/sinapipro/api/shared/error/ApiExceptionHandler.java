package com.sinapipro.api.shared.error;

import module java.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException exception, HttpServletRequest request) {
        var status = switch (exception) {
            case DomainNotFoundException _     -> HttpStatus.NOT_FOUND;
            case DomainConflictException _     -> HttpStatus.CONFLICT;
            case DomainValidationException _   -> HttpStatus.UNPROCESSABLE_ENTITY;
            default                            -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        var code = switch (exception) {
            case DomainNotFoundException _     -> "resource-not-found";
            case DomainConflictException _     -> "resource-conflict";
            case DomainValidationException _   -> "validation-error";
            default                            -> "domain-error";
        };
        var problem = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        enrich(problem, code, request);
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

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        enrich(problem, "validation-error", request);
        problem.setProperty("violations", violations);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        enrich(problem, "constraint-violation", request);
        problem.setProperty("violations", exception.getConstraintViolations().stream()
                .map(v -> new Violation(v.getPropertyPath().toString(), v.getMessage()))
                .toList());
        return problem;
    }

    @ExceptionHandler({JwtException.class, AccessDeniedException.class})
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

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception exception, HttpServletRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected application error");
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
