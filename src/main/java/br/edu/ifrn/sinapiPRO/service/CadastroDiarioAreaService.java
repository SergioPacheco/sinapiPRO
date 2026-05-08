package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.DiarioArea;
import br.edu.ifrn.sinapiPRO.repository.DiarioAreasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityListCrudService;

@Service
public class CadastroDiarioAreaService extends AbstractNamedEntityListCrudService<DiarioArea, DiarioAreasRepository> {

	public CadastroDiarioAreaService(DiarioAreasRepository repository) {
		super(
				repository,
				DiarioArea::getCodigo,
				DiarioArea::getNome,
				"DiarioArea já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"DiarioArea não encontrado(a).");
	}
}
