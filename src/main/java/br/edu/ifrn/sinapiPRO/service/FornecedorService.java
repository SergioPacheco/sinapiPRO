package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Fornecedor;
import br.edu.ifrn.sinapiPRO.repository.FornecedoresRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.FornecedorFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class FornecedorService extends AbstractNamedEntityCrudService<Fornecedor, FornecedorFilter, FornecedoresRepository> {

	public FornecedorService(FornecedoresRepository repository) {
		super(
				repository,
				Fornecedor::getCodigo,
				Fornecedor::getNome,
				"Fornecedor já cadastrado",
				"Impossível apagar fornecedor. Já está em uso.",
				"Fornecedor não encontrado.");
	}
}
