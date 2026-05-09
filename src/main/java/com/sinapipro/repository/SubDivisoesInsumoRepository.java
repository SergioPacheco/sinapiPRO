package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.SubDivisaoInsumo;
import com.sinapipro.repository.filter.SubDivisaoInsumoFilter;
import com.sinapipro.repository.helper.subdivisaoinsumo.SubDivisoesInsumoRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
@Repository
public interface SubDivisoesInsumoRepository extends JpaRepository<SubDivisaoInsumo, Long>, SubDivisoesInsumoRepositoryQueries,
		FilterableRepository<SubDivisaoInsumo, SubDivisaoInsumoFilter> {
	List<SubDivisaoInsumo> findByDivisaoCodigo(Long codigoDivisao);
}
