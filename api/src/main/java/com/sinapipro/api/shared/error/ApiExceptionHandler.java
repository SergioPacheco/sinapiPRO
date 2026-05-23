package com.sinapipro.api.shared.error;

import module java.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Centralized exception handler — returns ProblemDetail (RFC 9457).
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .toList();
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Erro de validação");
        problem.setTitle("validation-error");
        problem.setProperty("violations", violations);
        return ResponseEntity.badRequest().body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg.contains("Cannot deserialize")) {
            msg = msg.replaceAll(".*Cannot deserialize value of type `[^`]+` from String \"([^\"]+)\".*", "Valor inválido: '$1'");
        } else if (msg.contains("Missing required") || msg.contains("missing creator property")) {
            var field = msg.replaceAll(".*property '([^']+)'.*", "$1");
            msg = "Campo obrigatório não informado: '" + field + "'";
        } else if (msg.length() > 200) {
            msg = msg.substring(0, 200);
        }
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, msg);
        problem.setTitle("invalid-request");
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException exception, HttpServletRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(exception.status(), exception.getMessage());
        enrich(problem, exception.code(), request);
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

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        enrich(problem, "invalid-argument", request);
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
