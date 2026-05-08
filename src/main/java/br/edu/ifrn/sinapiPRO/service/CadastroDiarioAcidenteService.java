package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.DiarioAcidente;
import br.edu.ifrn.sinapiPRO.repository.DiarioAcidentesRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityListCrudService;

@Service
public class CadastroDiarioAcidenteService extends AbstractNamedEntityListCrudService<DiarioAcidente, DiarioAcidentesRepository> {

	public CadastroDiarioAcidenteService(DiarioAcidentesRepository repository) {
		super(
				repository,
				DiarioAcidente::getCodigo,
				DiarioAcidente::getNome,
				"DiarioAcidente já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"DiarioAcidente não encontrado(a).");
	}
}
