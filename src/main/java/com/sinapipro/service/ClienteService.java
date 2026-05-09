package com.sinapipro.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Cliente;
import com.sinapipro.repository.ClientesRepository;
import com.sinapipro.repository.filter.ClienteFilter;
import com.sinapipro.service.support.AbstractFilterableUniqueFieldCrudService;

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
