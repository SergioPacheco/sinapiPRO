package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.TipoUnidade;
import br.edu.ifrn.sinapiPRO.repository.TipoUnidadesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUnidadeFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroTipoUnidadeService extends AbstractNamedEntityCrudService<TipoUnidade, TipoUnidadeFilter, TipoUnidadesRepository> {

	public CadastroTipoUnidadeService(TipoUnidadesRepository repository) {
		super(
				repository,
				TipoUnidade::getCodigo,
				TipoUnidade::getNome,
				"Tipo de unidade já cadastrado",
				"Impossível apagar tipo de unidade. Já está em uso.",
				"Tipo de unidade não encontrado.");
	}
}
