package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoClassesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoClasseFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class ComposicaoClasseService extends AbstractFilterableUniqueFieldCrudService<ComposicaoClasse, ComposicaoClasseFilter, ComposicaoClassesRepository, String> {

	public ComposicaoClasseService(ComposicaoClassesRepository repository) {
		super(
				repository,
				ComposicaoClasse::getCodigo,
				ComposicaoClasse::getSigla,
				repository::findBySiglaIgnoreCase,
				"Sigla da classe já cadastrada",
				"Impossível apagar Insumo. Já foi usado em algum orçamento.",
				"Classe de composição não encontrada.");
	}
}
