package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.DiarioClima;
import br.edu.ifrn.sinapiPRO.repository.DiarioClimasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityListCrudService;

@Service
public class CadastroDiarioClimaService extends AbstractNamedEntityListCrudService<DiarioClima, DiarioClimasRepository> {

	public CadastroDiarioClimaService(DiarioClimasRepository repository) {
		super(
				repository,
				DiarioClima::getCodigo,
				DiarioClima::getNome,
				"DiarioClima já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"DiarioClima não encontrado(a).");
	}
}
