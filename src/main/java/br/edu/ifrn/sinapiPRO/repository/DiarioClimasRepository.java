package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.DiarioClima;
@Repository
public interface DiarioClimasRepository extends JpaRepository<DiarioClima, Long> {
	Optional<DiarioClima> findByNomeIgnoreCase(String nome);
}
