package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Tributo;
import com.sinapipro.repository.TributosRepository;
import com.sinapipro.repository.filter.TributoFilter;
import com.sinapipro.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class TributoService extends AbstractFilterableUniqueFieldCrudService<Tributo, TributoFilter, TributosRepository, String> {

	public TributoService(TributosRepository repository) {
		super(
				repository,
				Tributo::getCodigo,
				Tributo::getDescricao,
				repository::findByDescricaoIgnoreCase,
				"Tributo já cadastrado",
				"Impossível apagar tributo. Já está em uso.",
				"Tributo não encontrado.");
	}
}
