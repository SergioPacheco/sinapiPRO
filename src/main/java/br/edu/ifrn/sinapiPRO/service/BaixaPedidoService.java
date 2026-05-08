package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Estoque;
import br.edu.ifrn.sinapiPRO.model.MovimentoEstoque;
import br.edu.ifrn.sinapiPRO.model.PedidoCompra;
import br.edu.ifrn.sinapiPRO.model.PedidoItem;
import br.edu.ifrn.sinapiPRO.repository.EstoqueRepository;
import br.edu.ifrn.sinapiPRO.repository.PedidosCompraRepository;

/**
 * Lógica de negócio para recebimento (baixa) de pedidos de compra.
 *
 * REGRAS (práticas de ERP para construção civil):
 *
 * 1. RECEBIMENTO TOTAL: todos os itens recebidos na quantidade pedida
 *    → situação do pedido: RECEBIDO
 *
 * 2. RECEBIMENTO PARCIAL: alguns itens recebidos em quantidade menor
 *    → situação do pedido: PARCIAL
 *    → itens totalmente recebidos: quantidade_recebida = quantidade
 *    → pedido permanece aberto para recebimento do restante
 *
 * 3. ATUALIZAÇÃO DE ESTOQUE:
 *    → Para cada item recebido com insumo vinculado:
 *      - Busca ou cria item de estoque na obra do pedido
 *      - Registra ENTRADA com custo = valor_unitario do pedido
 *      - Atualiza custo médio ponderado (via EstoqueService)
 *
 * 4. VALIDAÇÕES:
 *    → Não pode receber mais do que o pedido
 *    → Pedido CANCELADO não pode ser recebido
 */
@Service
public class BaixaPedidoService {

    private final PedidosCompraRepository pedidoRepository;
    private final EstoqueRepository estoqueRepository;
    private final EstoqueService estoqueService;

    public BaixaPedidoService(
            PedidosCompraRepository pedidoRepository,
            EstoqueRepository estoqueRepository,
            EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.estoqueRepository = estoqueRepository;
        this.estoqueService = estoqueService;
    }

    /**
     * Registra o recebimento de um pedido de compra e atualiza o estoque.
     *
     * @param codigoPedido       código do pedido
     * @param quantidadesRecebidas mapa itemCodigo → quantidade recebida nesta entrega
     * @param dataRecebimento    data do recebimento
     * @param numeroNF           número da nota fiscal (opcional)
     * @return resultado do recebimento
     */
    @Transactional
    public ResultadoBaixa receberPedido(Long codigoPedido,
            java.util.Map<Long, BigDecimal> quantidadesRecebidas,
            LocalDate dataRecebimento,
            String numeroNF) {

        PedidoCompra pedido = pedidoRepository.findById(codigoPedido)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if ("CANCELADO".equals(pedido.getSituacao())) {
            throw new RuntimeException("Pedido cancelado não pode ser recebido.");
        }
        if ("RECEBIDO".equals(pedido.getSituacao())) {
            throw new RuntimeException("Pedido já foi totalmente recebido.");
        }

        ResultadoBaixa resultado = new ResultadoBaixa();
        resultado.setItensAtualizados(new ArrayList<>());

        for (PedidoItem item : pedido.getItens()) {
            BigDecimal qtdRecebida = quantidadesRecebidas.getOrDefault(item.getCodigo(), BigDecimal.ZERO);
            if (qtdRecebida.signum() <= 0) continue;

            BigDecimal qtdPendente = item.getQuantidade().subtract(item.getQuantidadeRecebida());
            if (qtdRecebida.compareTo(qtdPendente) > 0) {
                throw new RuntimeException(String.format(
                        "Item '%s': quantidade a receber (%.2f) maior que pendente (%.2f).",
                        item.getDescricao(), qtdRecebida, qtdPendente));
            }

            // Atualiza quantidade recebida no item
            item.setQuantidadeRecebida(item.getQuantidadeRecebida().add(qtdRecebida));

            // Atualiza estoque se o item tem insumo vinculado
            if (item.getInsumo() != null) {
                atualizarEstoque(pedido, item, qtdRecebida, dataRecebimento, numeroNF);
                resultado.getItensAtualizados().add(item.getDescricao());
            }
        }

        // Determina nova situação do pedido
        boolean todoRecebido = pedido.getItens().stream()
                .allMatch(i -> i.getQuantidadeRecebida().compareTo(i.getQuantidade()) >= 0);
        boolean algumRecebido = pedido.getItens().stream()
                .anyMatch(i -> i.getQuantidadeRecebida().signum() > 0);

        if (todoRecebido) {
            pedido.setSituacao("RECEBIDO");
        } else if (algumRecebido) {
            pedido.setSituacao("PARCIAL");
        }

        pedidoRepository.saveAndFlush(pedido);
        resultado.setSituacaoPedido(pedido.getSituacao());
        return resultado;
    }

    private void atualizarEstoque(PedidoCompra pedido, PedidoItem item,
            BigDecimal qtdRecebida, LocalDate dataRecebimento, String numeroNF) {

        // Busca ou cria item de estoque para obra + insumo
        Optional<Estoque> estoqueOpt = estoqueRepository
                .findByObraCodigoAndInsumoCodigo(
                        pedido.getObra().getCodigo(),
                        item.getInsumo().getCodigo());

        Estoque estoque;
        if (estoqueOpt.isPresent()) {
            estoque = estoqueOpt.get();
        } else {
            // Cria novo item de estoque
            estoque = new Estoque();
            estoque.setObra(pedido.getObra());
            estoque.setInsumo(item.getInsumo());
            estoque.setQuantidadeAtual(BigDecimal.ZERO);
            estoque.setQuantidadeMinima(BigDecimal.ZERO);
            estoque.setCustoMedio(BigDecimal.ZERO);
            estoque = estoqueRepository.saveAndFlush(estoque);
        }

        // Registra entrada no estoque com custo médio ponderado
        MovimentoEstoque movimento = new MovimentoEstoque();
        movimento.setTipo("ENTRADA");
        movimento.setQuantidade(qtdRecebida);
        movimento.setDataMovimento(dataRecebimento);
        movimento.setDocumento(numeroNF != null ? "NF: " + numeroNF : "Pedido #" + pedido.getNumero());
        movimento.setObservacao("Recebimento do Pedido de Compra #" + pedido.getNumero()
                + (pedido.getFornecedor() != null ? " — " + pedido.getFornecedor().getNome() : ""));

        estoqueService.movimentar(estoque.getCodigo(), movimento, item.getValorUnitario());
    }

    public static class ResultadoBaixa {
        private String situacaoPedido;
        private List<String> itensAtualizados;

public String getSituacaoPedido() {
	return situacaoPedido;
}

public void setSituacaoPedido(String situacaoPedido) {
	this.situacaoPedido = situacaoPedido;
}

public List<String> getItensAtualizados() {
	return itensAtualizados;
}

public void setItensAtualizados(List<String> itensAtualizados) {
	this.itensAtualizados = itensAtualizados;
}

    }
}
