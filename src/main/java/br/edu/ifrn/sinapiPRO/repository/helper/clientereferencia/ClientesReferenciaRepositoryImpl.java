package br.edu.ifrn.sinapiPRO.repository.helper.clientereferencia;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.ClienteReferencia;
public class ClientesReferenciaRepositoryImpl implements ClientesReferenciaRepositoryQueries {
	@PersistenceContext
	private EntityManager manager;
	@Override @Transactional(readOnly = true)
	public List<ClienteReferencia> findByClienteCodigo(Long codigoCliente) {
		return manager.createQuery("from ClienteReferencia where cliente.codigo = :cod", ClienteReferencia.class)
				.setParameter("cod", codigoCliente).getResultList();
			}
}
