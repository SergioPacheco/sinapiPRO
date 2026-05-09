package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Funcao;
import com.sinapipro.repository.FuncoesRepository;
import com.sinapipro.repository.filter.FuncaoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroFuncaoService extends AbstractNamedEntityCrudService<Funcao, FuncaoFilter, FuncoesRepository> {

	public CadastroFuncaoService(FuncoesRepository repository) {
		super(
				repository,
				Funcao::getCodigo,
				Funcao::getNome,
				"Funcao já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"Funcao não encontrada.");
	}
}
