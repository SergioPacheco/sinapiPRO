package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Funcao;
import br.edu.ifrn.sinapiPRO.repository.FuncoesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.FuncaoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroFuncaoService extends AbstractNamedEntityCrudService<Funcao, FuncaoFilter, FuncoesRepository> {

	public CadastroFuncaoService(FuncoesRepository repository) {
		super(
				repository,
				Funcao::getCodigo,
				Funcao::getNome,
				"Funcao já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"Funcao não encontrada.");
	}
}
