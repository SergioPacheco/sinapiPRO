package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.DiarioAcidente;
import com.sinapipro.repository.DiarioAcidentesRepository;
import com.sinapipro.service.support.AbstractNamedEntityListCrudService;

@Service
public class CadastroDiarioAcidenteService extends AbstractNamedEntityListCrudService<DiarioAcidente, DiarioAcidentesRepository> {

	public CadastroDiarioAcidenteService(DiarioAcidentesRepository repository) {
		super(
				repository,
				DiarioAcidente::getCodigo,
				DiarioAcidente::getNome,
				"DiarioAcidente já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"DiarioAcidente não encontrado(a).");
	}
}
