package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.DiarioClima;
import com.sinapipro.repository.DiarioClimasRepository;
import com.sinapipro.service.support.AbstractNamedEntityListCrudService;

@Service
public class CadastroDiarioClimaService extends AbstractNamedEntityListCrudService<DiarioClima, DiarioClimasRepository> {

	public CadastroDiarioClimaService(DiarioClimasRepository repository) {
		super(
				repository,
				DiarioClima::getCodigo,
				DiarioClima::getNome,
				"DiarioClima já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"DiarioClima não encontrado(a).");
	}
}
