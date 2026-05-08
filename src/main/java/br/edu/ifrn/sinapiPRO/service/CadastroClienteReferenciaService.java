package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.ClienteReferencia;
import br.edu.ifrn.sinapiPRO.repository.ClientesReferenciaRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractClienteOwnedCrudService;

@Service
public class CadastroClienteReferenciaService extends AbstractClienteOwnedCrudService<ClienteReferencia, ClientesReferenciaRepository> {

	public CadastroClienteReferenciaService(ClientesReferenciaRepository repository) {
		super(repository, "Impossível apagar.", "Referência do cliente não encontrada.");
	}
}
