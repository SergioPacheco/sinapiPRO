package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Indice; import br.edu.ifrn.sinapiPRO.repository.helper.indice.IndicesRepositoryQueries;
@Repository public interface IndicesRepository extends JpaRepository<Indice, Long>, IndicesRepositoryQueries {
	Optional<Indice> findByNomeIgnoreCase(String nome); }
