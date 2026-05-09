package com.sinapipro.service;

import java.util.AbstractMap.SimpleEntry;

import org.springframework.stereotype.Service;

import com.sinapipro.model.ComposicaoClasse;
import com.sinapipro.model.ComposicaoGrupo;
import com.sinapipro.repository.ComposicaoGruposRepository;
import com.sinapipro.repository.filter.ComposicaoGrupoFilter;
import com.sinapipro.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class ComposicaoGrupoService
		extends AbstractFilterableUniqueFieldCrudService<ComposicaoGrupo, ComposicaoGrupoFilter, ComposicaoGruposRepository, SimpleEntry<String, ComposicaoClasse>> {

	public ComposicaoGrupoService(ComposicaoGruposRepository repository) {
		super(
				repository,
				ComposicaoGrupo::getCodigo,
				grupo -> new SimpleEntry<>(grupo.getNome(), grupo.getComposicaoClasse()),
				chave -> repository.findByNomeAndComposicaoClasse(chave.getKey(), chave.getValue()),
				"Nome de Grupo já cadastrado",
				"Impossível apagar o grupo. Já está em uso.",
				"Grupo de composição não encontrado.");
	}
}
