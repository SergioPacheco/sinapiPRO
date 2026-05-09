package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Cotacao;
import com.sinapipro.repository.support.ObraScopedRepository;
@Repository
public interface CotacoesRepository extends JpaRepository<Cotacao, Long>, ObraScopedRepository<Cotacao> {
	List<Cotacao> findByObraCodigoOrderByDataCotacaoDesc(Long codigoObra);

	@Override
	default List<Cotacao> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoOrderByDataCotacaoDesc(codigoObra);
	}
}
