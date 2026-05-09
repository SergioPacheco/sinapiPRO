package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.DivisaoInsumo;
import com.sinapipro.repository.DivisoesInsumoRepository;
import com.sinapipro.repository.filter.DivisaoInsumoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroDivisaoInsumoService extends AbstractNamedEntityCrudService<DivisaoInsumo, DivisaoInsumoFilter, DivisoesInsumoRepository> {

	public CadastroDivisaoInsumoService(DivisoesInsumoRepository repository) {
		super(
				repository,
				DivisaoInsumo::getCodigo,
				DivisaoInsumo::getNome,
				"Divisão já cadastrada",
				"Impossível apagar. Já está em uso.",
				"Divisão de insumo não encontrada.");
	}
}
