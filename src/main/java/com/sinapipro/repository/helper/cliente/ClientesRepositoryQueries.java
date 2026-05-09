package com.sinapipro.repository.helper.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.Cliente;
import com.sinapipro.repository.filter.ClienteFilter;

public interface ClientesRepositoryQueries {

	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable);

	Cliente buscarComCidadeEstado(Long codigo);
}
