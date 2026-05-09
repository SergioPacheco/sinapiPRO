package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Cargo;
import com.sinapipro.repository.CargosRepository;
import com.sinapipro.repository.filter.CargoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroCargoService extends AbstractNamedEntityCrudService<Cargo, CargoFilter, CargosRepository> {

	public CadastroCargoService(CargosRepository repository) {
		super(
				repository,
				Cargo::getCodigo,
				Cargo::getNome,
				"Cargo já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"Cargo não encontrado.");
	}
}
