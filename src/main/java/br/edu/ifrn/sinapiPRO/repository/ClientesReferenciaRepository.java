package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.ClienteReferencia;
import br.edu.ifrn.sinapiPRO.repository.helper.clientereferencia.ClientesReferenciaRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.ClienteOwnedRepository;
@Repository
public interface ClientesReferenciaRepository extends JpaRepository<ClienteReferencia, Long>, ClientesReferenciaRepositoryQueries, ClienteOwnedRepository<ClienteReferencia> {
	List<ClienteReferencia> findByClienteCodigo(Long codigoCliente);
}
