package com.sinapipro.api.shared.error;

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

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DomainNotFoundException.class)
    ProblemDetail handleNotFound(DomainNotFoundException exception, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        enrich(problemDetail, "resource-not-found", request);
        return problemDetail;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    ProblemDetail handleValidation(Exception exception, HttpServletRequest request) {
        List<Violation> violations = switch (exception) {
            case MethodArgumentNotValidException methodArgumentNotValidException ->
                    methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                            .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                            .toList();
            case BindException bindException ->
                    bindException.getBindingResult().getFieldErrors().stream()
                            .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                            .toList();
            default -> List.of();
        };

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        enrich(problemDetail, "validation-error", request);
        problemDetail.setProperty("violations", violations);
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        enrich(problemDetail, "constraint-violation", request);
        problemDetail.setProperty("violations", exception.getConstraintViolations().stream()
                .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList());
        return problemDetail;
    }

    @ExceptionHandler({JwtException.class, AccessDeniedException.class})
    ProblemDetail handleSecurity(Exception exception, HttpServletRequest request) {
        HttpStatus status = exception instanceof AccessDeniedException ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        enrich(problemDetail, status == HttpStatus.FORBIDDEN ? "access-denied" : "unauthorized", request);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception exception, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected application error");
        enrich(problemDetail, "internal-error", request);
        return problemDetail;
    }

    private void enrich(ProblemDetail problemDetail, String code, HttpServletRequest request) {
        problemDetail.setType(URI.create("https://sinapipro.dev/problems/" + code));
        problemDetail.setTitle(code);
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("timestamp", OffsetDateTime.now());
    }

    private record Violation(String field, String message) {
    }
}
