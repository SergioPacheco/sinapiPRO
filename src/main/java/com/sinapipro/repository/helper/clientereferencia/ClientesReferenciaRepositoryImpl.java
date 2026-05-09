package com.sinapipro.repository.helper.clientereferencia;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import com.sinapipro.model.ClienteReferencia;
public class ClientesReferenciaRepositoryImpl implements ClientesReferenciaRepositoryQueries {
	@PersistenceContext
	private EntityManager manager;
	@Override @Transactional(readOnly = true)
	public List<ClienteReferencia> findByClienteCodigo(Long codigoCliente) {
		return manager.createQuery("from ClienteReferencia where cliente.codigo = :cod", ClienteReferencia.class)
				.setParameter("cod", codigoCliente).getResultList();
			}
}
