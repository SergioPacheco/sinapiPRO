package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.PedidoCompra;
@Repository
public interface PedidosCompraRepository extends JpaRepository<PedidoCompra, Long> {
	List<PedidoCompra> findByObraCodigoOrderByDataPedidoDesc(Long codigoObra);
}
