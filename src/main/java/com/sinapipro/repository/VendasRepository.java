package com.sinapipro.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Venda;
import com.sinapipro.repository.support.ObraScopedRepository;

@Repository
public interface VendasRepository extends JpaRepository<Venda, Long>, ObraScopedRepository<Venda> {
	List<Venda> findByUnidadeObraCodigoOrderByDataVendaDesc(Long codigoObra);
	List<Venda> findByClienteCodigoOrderByDataVendaDesc(Long codigoCliente);

	@Override
	default List<Venda> findByObraScopeCodigo(Long codigoObra) {
		return findByUnidadeObraCodigoOrderByDataVendaDesc(codigoObra);
	}
}
