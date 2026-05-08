package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Departamento;
import br.edu.ifrn.sinapiPRO.repository.DepartamentosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.DepartamentoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroDepartamentoService extends AbstractNamedEntityCrudService<Departamento, DepartamentoFilter, DepartamentosRepository> {

	public CadastroDepartamentoService(DepartamentosRepository repository) {
		super(
				repository,
				Departamento::getCodigo,
				Departamento::getNome,
				"Departamento já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"Departamento não encontrado.");
	}
}
