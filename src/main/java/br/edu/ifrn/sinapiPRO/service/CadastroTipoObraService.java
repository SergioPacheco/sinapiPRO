package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.TipoObra;
import br.edu.ifrn.sinapiPRO.repository.TiposObraRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoObraFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroTipoObraService extends AbstractNamedEntityCrudService<TipoObra, TipoObraFilter, TiposObraRepository> {

	public CadastroTipoObraService(TiposObraRepository repository) {
		super(
				repository,
				TipoObra::getCodigo,
				TipoObra::getNome,
				"Tipo de obra já cadastrado",
				"Impossível apagar. Já está em uso.",
				"Tipo de obra não encontrado.");
	}
}
