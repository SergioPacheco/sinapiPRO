package com.sinapipro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.model.Cliente;
import com.sinapipro.repository.filter.ClienteFilter;
import com.sinapipro.repository.helper.cliente.ClientesRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;

public interface ClientesRepository extends JpaRepository<Cliente, Long>, ClientesRepositoryQueries,
		FilterableRepository<Cliente, ClienteFilter> {

	public Optional<Cliente> findByCpfOuCnpj(String cpfOuCnpj);

	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);
}
