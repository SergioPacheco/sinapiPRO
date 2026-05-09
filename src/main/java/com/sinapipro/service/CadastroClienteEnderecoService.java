package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sinapipro.model.ClienteEndereco;
import com.sinapipro.repository.ClientesEnderecoRepository;
import com.sinapipro.service.support.AbstractClienteOwnedCrudService;

@Service
public class CadastroClienteEnderecoService extends AbstractClienteOwnedCrudService<ClienteEndereco, ClientesEnderecoRepository> {

	public CadastroClienteEnderecoService(ClientesEnderecoRepository repository) {
		super(repository, "Impossível apagar.", "Endereço do cliente não encontrado.");
	}
}
