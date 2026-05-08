package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Cliente;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ClienteFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractFilterableUniqueFieldCrudService;

@Service
public class ClienteService extends AbstractFilterableUniqueFieldCrudService<Cliente, ClienteFilter, ClientesRepository, String> {

	private final ClientesRepository repository;

	public ClienteService(ClientesRepository repository) {
		super(
				repository,
				Cliente::getCodigo,
				Cliente::getCpfOuCnpjSemFormatacao,
				repository::findByCpfOuCnpj,
				"CPF/CNPJ já cadastrado!",
				"Impossível apagar o cliente. Já foi usado em algum orçamento.",
				"Cliente não encontrado.");
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public Cliente buscarComCidadeEstado(Long codigo) {
		return repository.buscarComCidadeEstado(codigo);
	}
}
