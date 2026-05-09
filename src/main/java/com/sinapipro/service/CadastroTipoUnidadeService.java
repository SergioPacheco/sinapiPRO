package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.TipoUnidade;
import com.sinapipro.repository.TipoUnidadesRepository;
import com.sinapipro.repository.filter.TipoUnidadeFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroTipoUnidadeService extends AbstractNamedEntityCrudService<TipoUnidade, TipoUnidadeFilter, TipoUnidadesRepository> {

	public CadastroTipoUnidadeService(TipoUnidadesRepository repository) {
		super(
				repository,
				TipoUnidade::getCodigo,
				TipoUnidade::getNome,
				"Tipo de unidade já cadastrado",
				"Impossível apagar tipo de unidade. Já está em uso.",
				"Tipo de unidade não encontrado.");
	}
}
