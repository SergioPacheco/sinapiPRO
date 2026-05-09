package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Estado;
import com.sinapipro.repository.EstadosRepository;
import com.sinapipro.repository.filter.EstadoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class EstadoService extends AbstractNamedEntityCrudService<Estado, EstadoFilter, EstadosRepository> {

	public EstadoService(EstadosRepository repository) {
		super(
				repository,
				Estado::getCodigo,
				Estado::getNome,
				"Nome do estado já cadastrado",
				"Impossível apagar o estado. Já está em uso.",
				"Estado não encontrado.");
	}
}
