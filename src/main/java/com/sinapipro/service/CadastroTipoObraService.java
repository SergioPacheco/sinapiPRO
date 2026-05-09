package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.TipoObra;
import com.sinapipro.repository.TiposObraRepository;
import com.sinapipro.repository.filter.TipoObraFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroTipoObraService extends AbstractNamedEntityCrudService<TipoObra, TipoObraFilter, TiposObraRepository> {

	public CadastroTipoObraService(TiposObraRepository repository) {
		super(
				repository,
				TipoObra::getCodigo,
				TipoObra::getNome,
				"Tipo de obra já cadastrado",
				"Impossível apagar. Já está em uso.",
				"Tipo de obra não encontrado.");
	}
}
