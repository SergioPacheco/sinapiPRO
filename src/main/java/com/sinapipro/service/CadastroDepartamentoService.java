package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Departamento;
import com.sinapipro.repository.DepartamentosRepository;
import com.sinapipro.repository.filter.DepartamentoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroDepartamentoService extends AbstractNamedEntityCrudService<Departamento, DepartamentoFilter, DepartamentosRepository> {

	public CadastroDepartamentoService(DepartamentosRepository repository) {
		super(
				repository,
				Departamento::getCodigo,
				Departamento::getNome,
				"Departamento já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"Departamento não encontrado.");
	}
}
