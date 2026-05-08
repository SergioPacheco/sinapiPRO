package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.FormaPagamento;
import br.edu.ifrn.sinapiPRO.repository.FormasPagamentoRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.FormaPagamentoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

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
