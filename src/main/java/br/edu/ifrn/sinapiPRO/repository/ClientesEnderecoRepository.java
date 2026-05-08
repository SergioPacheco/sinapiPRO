package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.ClienteEndereco;
import br.edu.ifrn.sinapiPRO.repository.helper.clienteendereco.ClientesEnderecoRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.ClienteOwnedRepository;
@Repository
public interface ClientesEnderecoRepository extends JpaRepository<ClienteEndereco, Long>, ClientesEnderecoRepositoryQueries, ClienteOwnedRepository<ClienteEndereco> {
	List<ClienteEndereco> findByClienteCodigo(Long codigoCliente);
}
