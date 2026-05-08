package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.model.Cliente;
import br.edu.ifrn.sinapiPRO.repository.filter.ClienteFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.cliente.ClientesRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;

public interface ClientesRepository extends JpaRepository<Cliente, Long>, ClientesRepositoryQueries,
		FilterableRepository<Cliente, ClienteFilter> {

	public Optional<Cliente> findByCpfOuCnpj(String cpfOuCnpj);

	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);
}
