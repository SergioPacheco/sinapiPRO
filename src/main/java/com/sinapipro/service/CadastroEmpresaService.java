package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Empresa;
import com.sinapipro.repository.EmpresasRepository;
import com.sinapipro.repository.filter.EmpresaFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroEmpresaService extends AbstractNamedEntityCrudService<Empresa, EmpresaFilter, EmpresasRepository> {

	public CadastroEmpresaService(EmpresasRepository repository) {
		super(
				repository,
				Empresa::getCodigo,
				Empresa::getNome,
				"Empresa já cadastrada",
				"Impossível apagar. Já está em uso.",
				"Empresa não encontrada.");
	}
}
