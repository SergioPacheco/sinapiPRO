package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.DiarioArea;
import com.sinapipro.repository.DiarioAreasRepository;
import com.sinapipro.service.support.AbstractNamedEntityListCrudService;

@Service
public class CadastroDiarioAreaService extends AbstractNamedEntityListCrudService<DiarioArea, DiarioAreasRepository> {

	public CadastroDiarioAreaService(DiarioAreasRepository repository) {
		super(
				repository,
				DiarioArea::getCodigo,
				DiarioArea::getNome,
				"DiarioArea já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"DiarioArea não encontrado(a).");
	}
}
