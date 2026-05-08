package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.UnidadeMedida;
import br.edu.ifrn.sinapiPRO.repository.filter.UnidadeMedidaFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.unidademedida.UnidadesMedidaRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface UnidadesMedidaRepository extends JpaRepository<UnidadeMedida, Long>, UnidadesMedidaRepositoryQueries,
		NamedEntityRepository<UnidadeMedida>, FilterableRepository<UnidadeMedida, UnidadeMedidaFilter> {

	Optional<UnidadeMedida> findByNomeIgnoreCase(String nome);
}
