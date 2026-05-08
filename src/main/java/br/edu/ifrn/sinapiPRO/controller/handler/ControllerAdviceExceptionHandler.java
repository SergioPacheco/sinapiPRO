package br.edu.ifrn.sinapiPRO.controller.handler;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

@ControllerAdvice
public class ControllerAdviceExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ControllerAdviceExceptionHandler.class);

	@ExceptionHandler(JaCadastradoException.class)
	public Object handleJaCadastrado(JaCadastradoException exception, HttpServletRequest request) {
		return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
	}

	@ExceptionHandler(ImpossivelExcluirEntidadeException.class)
	public Object handleImpossivelExcluir(ImpossivelExcluirEntidadeException exception, HttpServletRequest request) {
		return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public Object handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
		return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public Object handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidacao(MethodArgumentNotValidException exception, HttpServletRequest request) {
		List<String> errors = exception.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());

		ApiErrorResponse body = ApiErrorResponse.validation(
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Erro de validação",
				errors,
				request.getRequestURI());

		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(Exception.class)
	public Object handleException(Exception exception, HttpServletRequest request) {
		log.error("Erro interno na requisição {}: {}", request.getRequestURI(), exception.getMessage(), exception);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Contate o administrador.", request);
	}

	private Object buildResponse(HttpStatus status, String message, HttpServletRequest request) {
		if (status.is4xxClientError()) {
			log.warn("Erro na requisição {}: {}", request.getRequestURI(), message);
		}

		if (isJsonRequest(request)) {
			ApiErrorResponse body = ApiErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
			return ResponseEntity.status(status).body(body);
		}

		ModelAndView modelAndView = new ModelAndView(HttpStatus.NOT_FOUND.equals(status) ? "error/404" : "error/500");
		modelAndView.addObject("mensagem", message);
		modelAndView.addObject("uri", request.getRequestURI());
		return modelAndView;
	}

	private boolean isJsonRequest(HttpServletRequest request) {
		String accept = request.getHeader("Accept");
		String contentType = request.getContentType();
		String requestedWith = request.getHeader("X-Requested-With");

		return containsJson(accept)
				|| containsJson(contentType)
				|| "XMLHttpRequest".equalsIgnoreCase(requestedWith);
	}

	private boolean containsJson(String value) {
		return value != null && value.contains("application/json");
	}
}
