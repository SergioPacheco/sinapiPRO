package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.DocumentoGed;
@Repository
public interface DocumentosGedRepository extends JpaRepository<DocumentoGed, Long> {
List<DocumentoGed> findByObraCodigoOrderByDataUploadDesc(Long codigoObra);
}
