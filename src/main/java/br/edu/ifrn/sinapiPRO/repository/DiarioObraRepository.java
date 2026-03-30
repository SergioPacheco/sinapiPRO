package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.DiarioObra;
@Repository
public interface DiarioObraRepository extends JpaRepository<DiarioObra, Long> {
	List<DiarioObra> findByObraCodigoOrderByDataDesc(Long codigoObra);
}
