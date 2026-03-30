package br.edu.ifrn.sinapiPRO.controller.handler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

/**
 * Handler global de exceções.
 *
 * Baseado em práticas do Spring Boot (Baeldung, Spring docs):
 * - Respostas padronizadas com timestamp, status, mensagem
 * - Logging de erros inesperados
 * - Separação entre erros de negócio (4xx) e erros de sistema (5xx)
 */
@ControllerAdvice
public class ControllerAdviceExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerAdviceExceptionHandler.class);

    // ---- Erros de negócio (400 Bad Request) ----

    @ExceptionHandler(JaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleJaCadastrado(JaCadastradoException e) {
        return erroNegocio(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(ImpossivelExcluirEntidadeException.class)
    public ResponseEntity<Map<String, Object>> handleImpossivelExcluir(ImpossivelExcluirEntidadeException e) {
        return erroNegocio(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Erros de validação Bean Validation (@Valid).
     * Retorna lista de campos com erro.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException e) {
        List<String> erros = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("erro", "Erro de validação");
        body.put("mensagens", erros);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Erros de negócio genéricos (RuntimeException com mensagem de negócio).
     * Usado pelos services de validação.
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException e,
            javax.servlet.http.HttpServletRequest request) {

        // Se é uma requisição AJAX/REST, retorna JSON
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            log.warn("Erro de negócio: {}", e.getMessage());
            return erroNegocio(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        // Para requisições de página, redireciona para página de erro
        log.error("Erro inesperado na requisição {}: {}", request.getRequestURI(), e.getMessage(), e);
        ModelAndView mv = new ModelAndView("error/500");
        mv.addObject("mensagem", e.getMessage());
        mv.addObject("uri", request.getRequestURI());
        return mv;
    }

    /**
     * Erros de sistema (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e,
            javax.servlet.http.HttpServletRequest request) {

        log.error("Erro interno na requisição {}: {}", request.getRequestURI(), e.getMessage(), e);

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", LocalDateTime.now().toString());
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("erro", "Erro interno do servidor");
            body.put("mensagem", "Ocorreu um erro inesperado. Contate o administrador.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        ModelAndView mv = new ModelAndView("error/500");
        mv.addObject("mensagem", "Ocorreu um erro inesperado. Contate o administrador.");
        mv.addObject("uri", request.getRequestURI());
        return mv;
    }

    private ResponseEntity<Map<String, Object>> erroNegocio(HttpStatus status, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("erro", status.getReasonPhrase());
        body.put("mensagem", mensagem);
        return ResponseEntity.status(status).body(body);
    }
}
