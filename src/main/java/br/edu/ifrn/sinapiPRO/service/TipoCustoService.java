package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.TipoCusto;
import br.edu.ifrn.sinapiPRO.repository.TipoCustosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoCustoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class TipoCustoService extends AbstractNamedEntityCrudService<TipoCusto, TipoCustoFilter, TipoCustosRepository> {

	public TipoCustoService(TipoCustosRepository repository) {
		super(
				repository,
				TipoCusto::getCodigo,
				TipoCusto::getNome,
				"Tipo de custo já cadastrado",
				"Impossível apagar tipo de custo. Já está em uso.",
				"Tipo de custo não encontrado.");
	}
}
