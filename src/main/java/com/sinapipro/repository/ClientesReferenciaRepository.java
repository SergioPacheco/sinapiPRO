package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.ClienteReferencia;
import com.sinapipro.repository.helper.clientereferencia.ClientesReferenciaRepositoryQueries;
import com.sinapipro.repository.support.ClienteOwnedRepository;
@Repository
public interface ClientesReferenciaRepository extends JpaRepository<ClienteReferencia, Long>, ClientesReferenciaRepositoryQueries, ClienteOwnedRepository<ClienteReferencia> {
	List<ClienteReferencia> findByClienteCodigo(Long codigoCliente);
}
