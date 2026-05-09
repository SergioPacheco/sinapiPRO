package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sinapipro.model.PlanoContas;
import com.sinapipro.repository.PlanoContasRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class PlanoContasService extends AbstractSimpleCrudService<PlanoContas, PlanoContasRepository> {

	private final PlanoContasRepository repository;

	public PlanoContasService(PlanoContasRepository repository) {
		super(repository, "Impossível apagar. Possui sub-contas ou lançamentos.", "Plano de contas não encontrado.");
		this.repository = repository;
	}

	public List<PlanoContas> findRaizes() {
		return repository.findByPaiIsNullOrderByNumeroAsc();
	}
}
