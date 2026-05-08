package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Obra;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ObraFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class ObraService extends AbstractFilterableUniqueFieldCrudService<Obra, ObraFilter, ObrasRepository, String> {

	private final ObrasRepository repository;

	public ObraService(ObrasRepository repository) {
		super(
				repository,
				Obra::getCodigo,
				Obra::getCei,
				repository::findByCei,
				"CEI já cadastrada!",
				"Impossível apagar a obra. Já esta sendo usada em algum orçamento",
				"Obra não encontrada.");
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public Obra buscarComCidadeEstado(Long codigo) {
		return repository.buscarComCidadeEstado(codigo);
	}
}
