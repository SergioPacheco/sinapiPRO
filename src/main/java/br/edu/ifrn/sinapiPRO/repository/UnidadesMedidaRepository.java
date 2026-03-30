package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.UnidadeMedida;
import br.edu.ifrn.sinapiPRO.repository.helper.unidademedida.UnidadesMedidaRepositoryQueries;

@Repository
public interface UnidadesMedidaRepository extends JpaRepository<UnidadeMedida, Long>, UnidadesMedidaRepositoryQueries {
	Optional<UnidadeMedida> findByNomeIgnoreCase(String nome);
}
