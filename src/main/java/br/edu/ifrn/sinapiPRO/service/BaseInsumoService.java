package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.BaseInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class BaseInsumoService extends AbstractNamedEntityCrudService<BaseInsumo, BaseInsumoFilter, BaseInsumosRepository> {

	public BaseInsumoService(BaseInsumosRepository repository) {
		super(
				repository,
				BaseInsumo::getCodigo,
				BaseInsumo::getNome,
				"Nome da Base Já Cadastrada",
				"Impossível apagar base. Já foi usado em alguma cerveja.",
				"Base de insumo não encontrada.");
	}
}
