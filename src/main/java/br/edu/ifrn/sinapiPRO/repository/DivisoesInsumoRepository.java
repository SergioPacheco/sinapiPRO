package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.DivisaoInsumo;
import br.edu.ifrn.sinapiPRO.repository.helper.divisaoinsumo.DivisoesInsumoRepositoryQueries;
@Repository
public interface DivisoesInsumoRepository extends JpaRepository<DivisaoInsumo, Long>, DivisoesInsumoRepositoryQueries {
	Optional<DivisaoInsumo> findByNomeIgnoreCase(String nome);
}
