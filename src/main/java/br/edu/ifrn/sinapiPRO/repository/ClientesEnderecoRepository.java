package br.edu.ifrn.sinapiPRO.repository;
import java.util.List; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.ClienteEndereco; import br.edu.ifrn.sinapiPRO.repository.helper.clienteendereco.ClientesEnderecoRepositoryQueries;
@Repository public interface ClientesEnderecoRepository extends JpaRepository<ClienteEndereco, Long>, ClientesEnderecoRepositoryQueries {
	List<ClienteEndereco> findByClienteCodigo(Long codigoCliente); }
