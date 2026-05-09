package com.sinapipro.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.TabelaPreco;
import com.sinapipro.repository.support.ObraScopedRepository;

@Repository
public interface TabelasPrecosRepository extends JpaRepository<TabelaPreco, Long>, ObraScopedRepository<TabelaPreco> {
	List<TabelaPreco> findByObraCodigoAndAtivaTrue(Long codigoObra);

	@Override
	default List<TabelaPreco> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoAndAtivaTrue(codigoObra);
	}
}
