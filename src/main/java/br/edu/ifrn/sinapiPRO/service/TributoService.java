package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Tributo;
import br.edu.ifrn.sinapiPRO.repository.TributosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TributoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class TributoService extends AbstractFilterableUniqueFieldCrudService<Tributo, TributoFilter, TributosRepository, String> {

	public TributoService(TributosRepository repository) {
		super(
				repository,
				Tributo::getCodigo,
				Tributo::getDescricao,
				repository::findByDescricaoIgnoreCase,
				"Tributo já cadastrado",
				"Impossível apagar tributo. Já está em uso.",
				"Tributo não encontrado.");
	}
}
