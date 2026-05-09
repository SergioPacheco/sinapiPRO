package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.ClienteEndereco;
import com.sinapipro.repository.helper.clienteendereco.ClientesEnderecoRepositoryQueries;
import com.sinapipro.repository.support.ClienteOwnedRepository;
@Repository
public interface ClientesEnderecoRepository extends JpaRepository<ClienteEndereco, Long>, ClientesEnderecoRepositoryQueries, ClienteOwnedRepository<ClienteEndereco> {
	List<ClienteEndereco> findByClienteCodigo(Long codigoCliente);
}
