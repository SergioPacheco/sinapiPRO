package com.sinapipro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.PedidoCompra;
import com.sinapipro.repository.PedidosCompraRepository;
import com.sinapipro.service.support.AbstractObraScopedCrudService;

@Service
public class PedidoCompraService extends AbstractObraScopedCrudService<PedidoCompra, PedidosCompraRepository> {

	private final PedidosCompraRepository repository;

	public PedidoCompraService(PedidosCompraRepository repository) {
		super(repository, "Impossível apagar o pedido.", "Pedido não encontrado.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public PedidoCompra salvar(PedidoCompra pedido) {
		pedido.getItens().forEach(item -> {
			item.setPedido(pedido);
			item.setValorTotal(item.getQuantidade().multiply(item.getValorUnitario()));
		});
		pedido.getNotasFiscais().forEach(notaFiscal -> notaFiscal.setPedido(pedido));
		BigDecimal total = pedido.getItens().stream()
				.map(item -> item.getValorTotal())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		pedido.setValorTotal(total);
		return repository.saveAndFlush(pedido);
	}

	@Transactional(readOnly = true)
	public PedidoCompra buscarComItens(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
