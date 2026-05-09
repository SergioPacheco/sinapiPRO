package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.BasePreco;
import com.sinapipro.repository.BasePrecosRepository;
import com.sinapipro.repository.filter.BasePrecoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

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
