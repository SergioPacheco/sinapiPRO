package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.EspecieInsumo;
import br.edu.ifrn.sinapiPRO.repository.EspecieInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.EspecieInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroEspecieInsumoService extends AbstractNamedEntityCrudService<EspecieInsumo, EspecieInsumoFilter, EspecieInsumosRepository> {

	public CadastroEspecieInsumoService(EspecieInsumosRepository repository) {
		super(
				repository,
				EspecieInsumo::getCodigo,
				EspecieInsumo::getNome,
				"Espécie de insumo já cadastrada",
				"Impossível apagar espécie de insumo. Já está em uso.",
				"Espécie de insumo não encontrada.");
	}
}
