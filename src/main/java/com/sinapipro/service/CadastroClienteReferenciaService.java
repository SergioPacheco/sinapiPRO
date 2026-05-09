package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sinapipro.model.ClienteReferencia;
import com.sinapipro.repository.ClientesReferenciaRepository;
import com.sinapipro.service.support.AbstractClienteOwnedCrudService;

@Service
public class CadastroClienteReferenciaService extends AbstractClienteOwnedCrudService<ClienteReferencia, ClientesReferenciaRepository> {

	public CadastroClienteReferenciaService(ClientesReferenciaRepository repository) {
		super(repository, "Impossível apagar.", "Referência do cliente não encontrada.");
	}
}
