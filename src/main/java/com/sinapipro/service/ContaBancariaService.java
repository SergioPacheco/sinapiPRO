package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sinapipro.model.ContaBancaria;
import com.sinapipro.repository.ContasBancariasRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class ContaBancariaService extends AbstractSimpleCrudService<ContaBancaria, ContasBancariasRepository> {

	private final ContasBancariasRepository repository;

	public ContaBancariaService(ContasBancariasRepository repository) {
		super(repository, "Impossível apagar. Possui movimentos vinculados.", "Conta bancária não encontrada.");
		this.repository = repository;
	}

	public List<ContaBancaria> findAtivas() {
		return repository.findByAtivaTrue();
	}
}
