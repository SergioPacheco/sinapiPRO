package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.DivisaoInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.DivisaoInsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.divisaoinsumo.DivisoesInsumoRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface DivisoesInsumoRepository extends JpaRepository<DivisaoInsumo, Long>, DivisoesInsumoRepositoryQueries,
		NamedEntityRepository<DivisaoInsumo>, FilterableRepository<DivisaoInsumo, DivisaoInsumoFilter> {

	Optional<DivisaoInsumo> findByNomeIgnoreCase(String nome);
}
