package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.TipoUnidade;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUnidadeFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.tipounidade.TipoUnidadesRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface TipoUnidadesRepository extends JpaRepository<TipoUnidade, Long>, TipoUnidadesRepositoryQueries,
		NamedEntityRepository<TipoUnidade>, FilterableRepository<TipoUnidade, TipoUnidadeFilter> {

	Optional<TipoUnidade> findByNomeIgnoreCase(String nome);
}
