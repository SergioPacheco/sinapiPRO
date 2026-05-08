package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.TipoObra;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoObraFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.tipoobra.TiposObraRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface TiposObraRepository extends JpaRepository<TipoObra, Long>, TiposObraRepositoryQueries,
		NamedEntityRepository<TipoObra>, FilterableRepository<TipoObra, TipoObraFilter> {

	Optional<TipoObra> findByNomeIgnoreCase(String nome);
}
