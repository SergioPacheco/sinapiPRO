package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Funcionario;
import com.sinapipro.repository.FuncionariosRepository;
import com.sinapipro.repository.filter.FuncionarioFilter;
import com.sinapipro.service.support.AbstractFilterableSimpleCrudService;

@Service
public class CadastroFuncionarioService extends AbstractFilterableSimpleCrudService<Funcionario, FuncionarioFilter, FuncionariosRepository> {

	public CadastroFuncionarioService(FuncionariosRepository repository) {
		super(repository, "Impossível apagar. Já está em uso.", "Funcionário não encontrado.");
	}
}
