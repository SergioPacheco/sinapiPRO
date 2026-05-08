package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.BaseInsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.baseinsumos.BaseInsumosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface BaseInsumosRepository extends JpaRepository<BaseInsumo, Long>, BaseInsumosRepositoryQueries,
		NamedEntityRepository<BaseInsumo>, FilterableRepository<BaseInsumo, BaseInsumoFilter> {

	public Optional<BaseInsumo> findByNomeIgnoreCase(String nome);
}
