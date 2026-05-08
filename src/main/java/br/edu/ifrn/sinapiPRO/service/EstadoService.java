package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Estado;
import br.edu.ifrn.sinapiPRO.repository.EstadosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.EstadoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class EstadoService extends AbstractNamedEntityCrudService<Estado, EstadoFilter, EstadosRepository> {

	public EstadoService(EstadosRepository repository) {
		super(
				repository,
				Estado::getCodigo,
				Estado::getNome,
				"Nome do estado já cadastrado",
				"Impossível apagar o estado. Já está em uso.",
				"Estado não encontrado.");
	}
}
