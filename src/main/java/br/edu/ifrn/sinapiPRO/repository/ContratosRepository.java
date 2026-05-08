package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Contrato;
import br.edu.ifrn.sinapiPRO.repository.support.ObraScopedRepository;
@Repository
public interface ContratosRepository extends JpaRepository<Contrato, Long>, ObraScopedRepository<Contrato> {
	List<Contrato> findByObraCodigoOrderByDescricaoAsc(Long codigoObra);

	@Override
	default List<Contrato> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoOrderByDescricaoAsc(codigoObra);
	}
}
