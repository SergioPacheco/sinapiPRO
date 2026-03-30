package br.edu.ifrn.sinapiPRO.repository;
import java.util.List; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.PlanoContas;
@Repository public interface PlanoContasRepository extends JpaRepository<PlanoContas, Long> {
	List<PlanoContas> findByPaiIsNullOrderByNumeroAsc();
	List<PlanoContas> findByTipoAndAtivoTrueOrderByNumeroAsc(String tipo); }
