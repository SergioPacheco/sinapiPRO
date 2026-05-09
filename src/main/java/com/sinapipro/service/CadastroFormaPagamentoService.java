package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.FormaPagamento;
import com.sinapipro.repository.FormasPagamentoRepository;
import com.sinapipro.repository.filter.FormaPagamentoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroFormaPagamentoService extends AbstractNamedEntityCrudService<FormaPagamento, FormaPagamentoFilter, FormasPagamentoRepository> {

	public CadastroFormaPagamentoService(FormasPagamentoRepository repository) {
		super(
				repository,
				FormaPagamento::getCodigo,
				FormaPagamento::getNome,
				"Forma de pagamento já cadastrada",
				"Impossível apagar. Já está em uso.",
				"Forma de pagamento não encontrada.");
	}
}
