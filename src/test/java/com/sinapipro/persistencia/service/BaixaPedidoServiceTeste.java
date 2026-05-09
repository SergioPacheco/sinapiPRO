package com.sinapipro.persistencia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sinapipro.model.Estoque;
import com.sinapipro.model.Fornecedor;
import com.sinapipro.model.Insumo;
import com.sinapipro.model.Obra;
import com.sinapipro.model.PedidoCompra;
import com.sinapipro.model.PedidoItem;
import com.sinapipro.repository.EstoqueRepository;
import com.sinapipro.repository.PedidosCompraRepository;
import com.sinapipro.service.BaixaPedidoService;
import com.sinapipro.service.EstoqueService;

@RunWith(MockitoJUnitRunner.class)
public class BaixaPedidoServiceTeste {

    @Mock
    private PedidosCompraRepository pedidoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private EstoqueService estoqueService;

    @InjectMocks
    private BaixaPedidoService service;

    private PedidoCompra criarPedido() {
        Obra obra = new Obra();
        obra.setCodigo(1L);

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setCodigo(1L);
        fornecedor.setNome("Fornecedor Teste");

        Insumo insumo = new Insumo();
        insumo.setCodigo(1L);
        insumo.setDescricao("Cimento");

        PedidoItem item = new PedidoItem();
        item.setCodigo(1L);
        item.setDescricao("Cimento CP-II");
        item.setUnidade("sc");
        item.setQuantidade(new BigDecimal("100"));
        item.setValorUnitario(new BigDecimal("28.50"));
        item.setQuantidadeRecebida(BigDecimal.ZERO);
        item.setInsumo(insumo);

        PedidoCompra pedido = new PedidoCompra();
        pedido.setCodigo(1L);
        pedido.setNumero(1);
        pedido.setObra(obra);
        pedido.setFornecedor(fornecedor);
        pedido.setSituacao("ABERTO");
        pedido.getItens().add(item);
        item.setPedido(pedido);

        return pedido;
    }

    @Test
    public void should_receberTotal_and_updateSituacao() {
        // Arrange
        PedidoCompra pedido = criarPedido();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.saveAndFlush(any())).thenReturn(pedido);
        when(estoqueRepository.findByObraCodigoAndInsumoCodigo(any(), any()))
                .thenReturn(Optional.of(new Estoque()));
        when(estoqueService.movimentar(any(), any(), any())).thenReturn(new Estoque());

        Map<Long, BigDecimal> qtds = new HashMap<>();
        qtds.put(1L, new BigDecimal("100")); // recebe tudo

        // Act
        BaixaPedidoService.ResultadoBaixa resultado =
                service.receberPedido(1L, qtds, LocalDate.now(), "NF-001");

        // Assert
        assertThat(resultado.getSituacaoPedido()).isEqualTo("RECEBIDO");
        assertThat(resultado.getItensAtualizados()).hasSize(1);
    }

    @Test
    public void should_receberParcial_and_updateSituacao() {
        // Arrange
        PedidoCompra pedido = criarPedido();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.saveAndFlush(any())).thenReturn(pedido);
        when(estoqueRepository.findByObraCodigoAndInsumoCodigo(any(), any()))
                .thenReturn(Optional.of(new Estoque()));
        when(estoqueService.movimentar(any(), any(), any())).thenReturn(new Estoque());

        Map<Long, BigDecimal> qtds = new HashMap<>();
        qtds.put(1L, new BigDecimal("60")); // recebe 60 de 100

        // Act
        BaixaPedidoService.ResultadoBaixa resultado =
                service.receberPedido(1L, qtds, LocalDate.now(), null);

        // Assert
        assertThat(resultado.getSituacaoPedido()).isEqualTo("PARCIAL");
        assertThat(pedido.getItens().get(0).getQuantidadeRecebida())
                .isEqualByComparingTo(new BigDecimal("60"));
    }

    @Test
    public void should_throwException_when_qtdMaiorQuePendente() {
        // Arrange
        PedidoCompra pedido = criarPedido();
        pedido.getItens().get(0).setQuantidadeRecebida(new BigDecimal("80")); // já recebeu 80
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Map<Long, BigDecimal> qtds = new HashMap<>();
        qtds.put(1L, new BigDecimal("30")); // tenta receber 30, mas só faltam 20

        // Act & Assert
        assertThatThrownBy(() -> service.receberPedido(1L, qtds, LocalDate.now(), null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("maior que pendente");
    }

    @Test
    public void should_throwException_when_pedidoCancelado() {
        // Arrange
        PedidoCompra pedido = criarPedido();
        pedido.setSituacao("CANCELADO");
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act & Assert
        assertThatThrownBy(() -> service.receberPedido(1L, new HashMap<>(), LocalDate.now(), null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("cancelado");
    }
}
