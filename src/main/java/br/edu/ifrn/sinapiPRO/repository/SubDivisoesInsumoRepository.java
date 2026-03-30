package br.edu.ifrn.sinapiPRO.repository;
import java.util.List; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.SubDivisaoInsumo; import br.edu.ifrn.sinapiPRO.repository.helper.subdivisaoinsumo.SubDivisoesInsumoRepositoryQueries;
@Repository public interface SubDivisoesInsumoRepository extends JpaRepository<SubDivisaoInsumo, Long>, SubDivisoesInsumoRepositoryQueries {
	List<SubDivisaoInsumo> findByDivisaoCodigo(Long codigoDivisao); }
