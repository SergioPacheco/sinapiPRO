package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.UnidadeMedida;
import br.edu.ifrn.sinapiPRO.repository.UnidadesMedidaRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.UnidadeMedidaFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroUnidadeMedidaService extends AbstractNamedEntityCrudService<UnidadeMedida, UnidadeMedidaFilter, UnidadesMedidaRepository> {

	public CadastroUnidadeMedidaService(UnidadesMedidaRepository repository) {
		super(
				repository,
				UnidadeMedida::getCodigo,
				UnidadeMedida::getNome,
				"Unidade de medida já cadastrada",
				"Impossível apagar. Já está em uso.",
				"Unidade de medida não encontrada.");
	}
}
