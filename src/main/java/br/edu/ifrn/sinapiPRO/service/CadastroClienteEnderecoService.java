package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.ClienteEndereco;
import br.edu.ifrn.sinapiPRO.repository.ClientesEnderecoRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractClienteOwnedCrudService;

@Service
public class CadastroClienteEnderecoService extends AbstractClienteOwnedCrudService<ClienteEndereco, ClientesEnderecoRepository> {

	public CadastroClienteEnderecoService(ClientesEnderecoRepository repository) {
		super(repository, "Impossível apagar.", "Endereço do cliente não encontrado.");
	}
}
