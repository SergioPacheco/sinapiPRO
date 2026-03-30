package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.DiarioAcidente;
@Repository
public interface DiarioAcidentesRepository extends JpaRepository<DiarioAcidente, Long> {
	Optional<DiarioAcidente> findByNomeIgnoreCase(String nome);
}
