package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.BaseInsumo;
import com.sinapipro.repository.BaseInsumosRepository;
import com.sinapipro.repository.filter.BaseInsumoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class BaseInsumoService extends AbstractNamedEntityCrudService<BaseInsumo, BaseInsumoFilter, BaseInsumosRepository> {

	public BaseInsumoService(BaseInsumosRepository repository) {
		super(
				repository,
				BaseInsumo::getCodigo,
				BaseInsumo::getNome,
				"Nome da Base Já Cadastrada",
				"Impossível apagar base. Já foi usado em alguma cerveja.",
				"Base de insumo não encontrada.");
	}
}
