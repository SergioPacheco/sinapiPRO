package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Cargo;
import br.edu.ifrn.sinapiPRO.repository.CargosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.CargoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class CadastroCargoService extends AbstractNamedEntityCrudService<Cargo, CargoFilter, CargosRepository> {

	public CadastroCargoService(CargosRepository repository) {
		super(
				repository,
				Cargo::getCodigo,
				Cargo::getNome,
				"Cargo já cadastrado(a)",
				"Impossível apagar. Já está em uso.",
				"Cargo não encontrado.");
	}
}
