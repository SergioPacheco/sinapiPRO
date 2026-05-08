package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Funcionario;
import br.edu.ifrn.sinapiPRO.repository.FuncionariosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.FuncionarioFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractFilterableSimpleCrudService;

@Service
public class CadastroFuncionarioService extends AbstractFilterableSimpleCrudService<Funcionario, FuncionarioFilter, FuncionariosRepository> {

	public CadastroFuncionarioService(FuncionariosRepository repository) {
		super(repository, "Impossível apagar. Já está em uso.", "Funcionário não encontrado.");
	}
}
