package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.SubDivisaoInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.SubDivisaoInsumoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.subdivisaoinsumo.SubDivisoesInsumoRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
@Repository
public interface SubDivisoesInsumoRepository extends JpaRepository<SubDivisaoInsumo, Long>, SubDivisoesInsumoRepositoryQueries,
		FilterableRepository<SubDivisaoInsumo, SubDivisaoInsumoFilter> {
	List<SubDivisaoInsumo> findByDivisaoCodigo(Long codigoDivisao);
}
