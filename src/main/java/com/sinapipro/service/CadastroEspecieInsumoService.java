package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.EspecieInsumo;
import com.sinapipro.repository.EspecieInsumosRepository;
import com.sinapipro.repository.filter.EspecieInsumoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroEspecieInsumoService extends AbstractNamedEntityCrudService<EspecieInsumo, EspecieInsumoFilter, EspecieInsumosRepository> {

	public CadastroEspecieInsumoService(EspecieInsumosRepository repository) {
		super(
				repository,
				EspecieInsumo::getCodigo,
				EspecieInsumo::getNome,
				"Espécie de insumo já cadastrada",
				"Impossível apagar espécie de insumo. Já está em uso.",
				"Espécie de insumo não encontrada.");
	}
}
