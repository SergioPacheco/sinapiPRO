package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Indice;
import br.edu.ifrn.sinapiPRO.repository.IndicesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.IndiceFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroIndiceService extends AbstractNamedEntityCrudService<Indice, IndiceFilter, IndicesRepository> {

	public CadastroIndiceService(IndicesRepository repository) {
		super(
				repository,
				Indice::getCodigo,
				Indice::getNome,
				"Índice já cadastrado",
				"Impossível apagar. Já está em uso.",
				"Índice não encontrado.");
	}
}
