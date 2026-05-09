package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.UnidadeMedida;
import com.sinapipro.repository.UnidadesMedidaRepository;
import com.sinapipro.repository.filter.UnidadeMedidaFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroUnidadeMedidaService extends AbstractNamedEntityCrudService<UnidadeMedida, UnidadeMedidaFilter, UnidadesMedidaRepository> {

	public CadastroUnidadeMedidaService(UnidadesMedidaRepository repository) {
		super(
				repository,
				UnidadeMedida::getCodigo,
				UnidadeMedida::getNome,
				"Unidade de medida já cadastrada",
				"Impossível apagar. Já está em uso.",
				"Unidade de medida não encontrada.");
	}
}
