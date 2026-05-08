package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Empresa;
import br.edu.ifrn.sinapiPRO.repository.EmpresasRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.EmpresaFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroEmpresaService extends AbstractNamedEntityCrudService<Empresa, EmpresaFilter, EmpresasRepository> {

	public CadastroEmpresaService(EmpresasRepository repository) {
		super(
				repository,
				Empresa::getCodigo,
				Empresa::getNome,
				"Empresa já cadastrada",
				"Impossível apagar. Já está em uso.",
				"Empresa não encontrada.");
	}
}
