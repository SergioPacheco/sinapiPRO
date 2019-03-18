package br.edu.ifrn.sinapiPRO.controller.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@ControllerAdvice
public class ControllerAdviceExceptionHandler {

	@ExceptionHandler(JaCadastradoException.class)
	public ResponseEntity<String> handleNomeEstadoJaCadastradoException(JaCadastradoException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
	
}
