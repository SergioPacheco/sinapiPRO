package br.edu.ifrn.sinapiPRO.repository;
import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Estoque;
@Repository public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
	List<Estoque> findByObraCodigo(Long codigoObra);
	Optional<Estoque> findByObraCodigoAndInsumoCodigo(Long codigoObra, Long codigoInsumo); }
