package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.EspecieInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.EspecieInsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.especieinsumo.EspecieInsumosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface EspecieInsumosRepository extends JpaRepository<EspecieInsumo, Long>, EspecieInsumosRepositoryQueries,
		NamedEntityRepository<EspecieInsumo>, FilterableRepository<EspecieInsumo, EspecieInsumoFilter> {

	Optional<EspecieInsumo> findByNomeIgnoreCase(String nome);
}
