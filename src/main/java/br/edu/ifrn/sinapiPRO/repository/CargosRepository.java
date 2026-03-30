package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Cargo;
import br.edu.ifrn.sinapiPRO.repository.helper.cargo.CargosRepositoryQueries;
@Repository
public interface CargosRepository extends JpaRepository<Cargo, Long>, CargosRepositoryQueries {
	Optional<Cargo> findByNomeIgnoreCase(String nome);
}
