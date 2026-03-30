package br.edu.ifrn.sinapiPRO.controller;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.edu.ifrn.sinapiPRO.model.Orcamento;

@Component
public class OrcamentoValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Orcamento.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		// ValidationUtils.rejectIfEmpty(errors, "cliente.codigo", "", "Selecione um cliente na pesquisa rápida");
		Orcamento orcamento = (Orcamento) target;
		validarSeInformouItens(errors, orcamento);
	}
	
	private void validarSeInformouItens(Errors errors, Orcamento orcamento) {
		if (orcamento.getItens().isEmpty()) {
			errors.reject("", "Adicione pelo menos um item no orçamento");
		}
	}
	
	
	
}
