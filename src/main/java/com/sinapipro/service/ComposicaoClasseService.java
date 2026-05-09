package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.ComposicaoClasse;
import com.sinapipro.repository.ComposicaoClassesRepository;
import com.sinapipro.repository.filter.ComposicaoClasseFilter;
import com.sinapipro.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class ComposicaoClasseService extends AbstractFilterableUniqueFieldCrudService<ComposicaoClasse, ComposicaoClasseFilter, ComposicaoClassesRepository, String> {

	public ComposicaoClasseService(ComposicaoClassesRepository repository) {
		super(
				repository,
				ComposicaoClasse::getCodigo,
				ComposicaoClasse::getSigla,
				repository::findBySiglaIgnoreCase,
				"Sigla da classe já cadastrada",
				"Impossível apagar Insumo. Já foi usado em algum orçamento.",
				"Classe de composição não encontrada.");
	}
}
