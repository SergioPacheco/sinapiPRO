package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.DocumentoGed;
import br.edu.ifrn.sinapiPRO.repository.support.ObraScopedRepository;
@Repository
public interface DocumentosGedRepository extends JpaRepository<DocumentoGed, Long>, ObraScopedRepository<DocumentoGed> {
	List<DocumentoGed> findByObraCodigoOrderByDataUploadDesc(Long codigoObra);

	@Override
	default List<DocumentoGed> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoOrderByDataUploadDesc(codigoObra);
	}
}
