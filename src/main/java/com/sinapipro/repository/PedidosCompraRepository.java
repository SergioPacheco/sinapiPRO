package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.PedidoCompra;
import com.sinapipro.repository.support.ObraScopedRepository;
@Repository
public interface PedidosCompraRepository extends JpaRepository<PedidoCompra, Long>, ObraScopedRepository<PedidoCompra> {
	List<PedidoCompra> findByObraCodigoOrderByDataPedidoDesc(Long codigoObra);

	@Override
	default List<PedidoCompra> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoOrderByDataPedidoDesc(codigoObra);
	}
}
