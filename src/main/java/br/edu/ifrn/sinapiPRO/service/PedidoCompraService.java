package br.edu.ifrn.sinapiPRO.service;
import java.math.BigDecimal; import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.PedidoCompra; import br.edu.ifrn.sinapiPRO.repository.PedidosCompraRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class PedidoCompraService {
	@Autowired private PedidosCompraRepository repository;
	@Transactional public PedidoCompra salvar(PedidoCompra p) {
		p.getItens().forEach(i -> { i.setPedido(p); i.setValorTotal(i.getQuantidade().multiply(i.getValorUnitario())); });
		p.getNotasFiscais().forEach(nf -> nf.setPedido(p));
		BigDecimal total = p.getItens().stream().map(i -> i.getValorTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
		p.setValorTotal(total);
		return repository.saveAndFlush(p); }
	@Transactional public void excluir(Long codigo) { try { repository.deleteById(codigo); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar o pedido."); } }
	@Transactional(readOnly = true) public List<PedidoCompra> findByObra(Long codigoObra) { return repository.findByObraCodigoOrderByDataPedidoDesc(codigoObra); }
	@Transactional(readOnly = true) public PedidoCompra buscarComItens(Long codigo) { return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Pedido não encontrado")); }
}
