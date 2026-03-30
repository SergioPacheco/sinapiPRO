package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.TipoObra;
import br.edu.ifrn.sinapiPRO.repository.helper.tipoobra.TiposObraRepositoryQueries;
@Repository
public interface TiposObraRepository extends JpaRepository<TipoObra, Long>, TiposObraRepositoryQueries {
	Optional<TipoObra> findByNomeIgnoreCase(String nome);
}
