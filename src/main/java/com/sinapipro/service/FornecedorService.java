package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Fornecedor;
import com.sinapipro.repository.FornecedoresRepository;
import com.sinapipro.repository.filter.FornecedorFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class FornecedorService extends AbstractNamedEntityCrudService<Fornecedor, FornecedorFilter, FornecedoresRepository> {

	public FornecedorService(FornecedoresRepository repository) {
		super(
				repository,
				Fornecedor::getCodigo,
				Fornecedor::getNome,
				"Fornecedor já cadastrado",
				"Impossível apagar fornecedor. Já está em uso.",
				"Fornecedor não encontrado.");
	}
}
