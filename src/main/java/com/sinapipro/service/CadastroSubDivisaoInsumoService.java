package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.SubDivisaoInsumo;
import com.sinapipro.repository.SubDivisoesInsumoRepository;
import com.sinapipro.repository.filter.SubDivisaoInsumoFilter;
import com.sinapipro.service.support.AbstractFilterableSimpleCrudService;

@Service
public class CadastroSubDivisaoInsumoService
		extends AbstractFilterableSimpleCrudService<SubDivisaoInsumo, SubDivisaoInsumoFilter, SubDivisoesInsumoRepository> {

	public CadastroSubDivisaoInsumoService(SubDivisoesInsumoRepository repository) {
		super(repository, "Impossível apagar. Já está em uso.", "Sub-divisão não encontrada.");
	}
}
