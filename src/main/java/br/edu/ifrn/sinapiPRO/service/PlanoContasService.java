package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.PlanoContas;
import br.edu.ifrn.sinapiPRO.repository.PlanoContasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class PlanoContasService extends AbstractSimpleCrudService<PlanoContas, PlanoContasRepository> {

	private final PlanoContasRepository repository;

	public PlanoContasService(PlanoContasRepository repository) {
		super(repository, "Impossível apagar. Possui sub-contas ou lançamentos.", "Plano de contas não encontrado.");
		this.repository = repository;
	}

	public List<PlanoContas> findRaizes() {
		return repository.findByPaiIsNullOrderByNumeroAsc();
	}
}
