package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.BasePrecoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class BasePrecoService extends AbstractNamedEntityCrudService<BasePreco, BasePrecoFilter, BasePrecosRepository> {

	public BasePrecoService(BasePrecosRepository repository) {
		super(
				repository,
				BasePreco::getCodigo,
				BasePreco::getNome,
				"Nome da base já cadastrada",
				"Impossível apagar Base. Já foi usado em algum orçamento.",
				"Base de preço não encontrada.");
	}
}
