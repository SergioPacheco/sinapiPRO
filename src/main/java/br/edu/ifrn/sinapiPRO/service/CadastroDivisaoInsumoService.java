package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.DivisaoInsumo;
import br.edu.ifrn.sinapiPRO.repository.DivisoesInsumoRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.DivisaoInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroDivisaoInsumoService extends AbstractNamedEntityCrudService<DivisaoInsumo, DivisaoInsumoFilter, DivisoesInsumoRepository> {

	public CadastroDivisaoInsumoService(DivisoesInsumoRepository repository) {
		super(
				repository,
				DivisaoInsumo::getCodigo,
				DivisaoInsumo::getNome,
				"Divisão já cadastrada",
				"Impossível apagar. Já está em uso.",
				"Divisão de insumo não encontrada.");
	}
}
