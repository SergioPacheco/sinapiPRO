package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Equipamento;
import br.edu.ifrn.sinapiPRO.repository.EquipamentosRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class CadastroEquipamentoService extends AbstractSimpleCrudService<Equipamento, EquipamentosRepository> {

	public CadastroEquipamentoService(EquipamentosRepository repository) {
		super(repository, "Impossível apagar. Já está em uso.", "Equipamento não encontrado.");
	}
}
