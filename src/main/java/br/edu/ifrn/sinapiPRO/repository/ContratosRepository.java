package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Contrato;
@Repository
public interface ContratosRepository extends JpaRepository<Contrato, Long> {
	List<Contrato> findByObraCodigoOrderByDescricaoAsc(Long codigoObra);
}
