package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Equipamento;
import com.sinapipro.repository.EquipamentosRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class CadastroEquipamentoService extends AbstractSimpleCrudService<Equipamento, EquipamentosRepository> {

	public CadastroEquipamentoService(EquipamentosRepository repository) {
		super(repository, "Impossível apagar. Já está em uso.", "Equipamento não encontrado.");
	}
}
