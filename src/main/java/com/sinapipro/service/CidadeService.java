package com.sinapipro.service;

import java.util.AbstractMap.SimpleEntry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Cidade;
import com.sinapipro.model.Estado;
import com.sinapipro.repository.CidadesRepository;
import com.sinapipro.repository.filter.CidadeFilter;
import com.sinapipro.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class CidadeService extends AbstractFilterableUniqueFieldCrudService<Cidade, CidadeFilter, CidadesRepository, SimpleEntry<String, Estado>> {

	private final CidadesRepository repository;

	public CidadeService(CidadesRepository repository) {
		super(
				repository,
				Cidade::getCodigo,
				cidade -> new SimpleEntry<>(cidade.getNome(), cidade.getEstado()),
				chave -> repository.findByNomeAndEstado(chave.getKey(), chave.getValue()),
				"Nome da cidade já cadastrado",
				"Impossível apagar a cidade. Já foi usado em algum cadastro de orçamento.",
				"Cidade não encontrada.");
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public Cidade buscarComEstado(Long codigo) {
		return repository.buscarComEstado(codigo);
	}
}
