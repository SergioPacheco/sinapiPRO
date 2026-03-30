package br.edu.ifrn.sinapiPRO.repository.helper.clienteendereco;
import java.util.List; import javax.persistence.EntityManager; import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.ClienteEndereco;
public class ClientesEnderecoRepositoryImpl implements ClientesEnderecoRepositoryQueries {
	@PersistenceContext private EntityManager manager;
	@Override @Transactional(readOnly = true)
	public List<ClienteEndereco> findByClienteCodigo(Long codigoCliente) {
		return manager.createQuery("from ClienteEndereco where cliente.codigo = :cod", ClienteEndereco.class)
				.setParameter("cod", codigoCliente).getResultList(); }
}
