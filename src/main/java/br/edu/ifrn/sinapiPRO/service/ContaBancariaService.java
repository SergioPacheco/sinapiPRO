package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.ContaBancaria;
import br.edu.ifrn.sinapiPRO.repository.ContasBancariasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

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
