package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Tributo;
import br.edu.ifrn.sinapiPRO.repository.filter.TributoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.tributo.TributosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;

@Repository
public interface TributosRepository extends JpaRepository<Tributo, Long>, TributosRepositoryQueries,
		FilterableRepository<Tributo, TributoFilter> {

	Optional<Tributo> findByDescricaoIgnoreCase(String descricao);
}
