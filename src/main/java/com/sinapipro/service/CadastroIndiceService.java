package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Indice;
import com.sinapipro.repository.IndicesRepository;
import com.sinapipro.repository.filter.IndiceFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroIndiceService extends AbstractNamedEntityCrudService<Indice, IndiceFilter, IndicesRepository> {

	public CadastroIndiceService(IndicesRepository repository) {
		super(
				repository,
				Indice::getCodigo,
				Indice::getNome,
				"Índice já cadastrado",
				"Impossível apagar. Já está em uso.",
				"Índice não encontrado.");
	}
}
