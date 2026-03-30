package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.DiarioArea;
@Repository public interface DiarioAreasRepository extends JpaRepository<DiarioArea, Long> {
	Optional<DiarioArea> findByNomeIgnoreCase(String nome); }
