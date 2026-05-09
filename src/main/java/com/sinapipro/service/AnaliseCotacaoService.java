package com.sinapipro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Cotacao;
import com.sinapipro.model.CotacaoFornecedor;
import com.sinapipro.model.CotacaoItem;
import com.sinapipro.model.PedidoCompra;
import com.sinapipro.model.PedidoItem;
import com.sinapipro.model.RespostaCotacao;
import com.sinapipro.repository.CotacoesRepository;
import com.sinapipro.repository.PedidosCompraRepository;

/**
 * Lógica de negócio para cotações de suprimentos.
 *
 * Fluxo padrão (baseado em práticas de ERP para construção civil):
 * 1. Cotação criada com itens e fornecedores convidados
 * 2. Fornecedores respondem com preços (RespostaCotacao)
 * 3. Análise comparativa: menor preço por item, total por fornecedor
 * 4. Seleção: usuário marca qual resposta foi selecionada por item
 * 5. Geração automática de pedido(s) de compra agrupados por fornecedor
 */
@Service
public class AnaliseCotacaoService {

    private final CotacoesRepository cotacaoRepository;
    private final PedidosCompraRepository pedidoRepository;

    public AnaliseCotacaoService(CotacoesRepository cotacaoRepository, PedidosCompraRepository pedidoRepository) {
        this.cotacaoRepository = cotacaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Retorna análise comparativa: para cada item, lista as respostas ordenadas por preço.
     * Marca automaticamente a resposta de menor preço como sugerida.
     */
    @Transactional(readOnly = true)
    public List<ItemAnalise> analisarCotacao(Long codigoCotacao) {
        Cotacao cotacao = cotacaoRepository.findById(codigoCotacao)
                .orElseThrow(() -> new RuntimeException("Cotação não encontrada"));

        List<ItemAnalise> analise = new ArrayList<>();

        for (CotacaoItem item : cotacao.getItens()) {
            ItemAnalise ia = new ItemAnalise();
            ia.setItem(item);

            // Ordena respostas por preço (menor primeiro)
            List<RespostaCotacao> respostas = item.getRespostas().stream()
                    .filter(r -> r.getValorUnitario() != null && r.getValorUnitario().signum() > 0)
                    .sorted(Comparator.comparing(RespostaCotacao::getValorUnitario))
                    .collect(Collectors.toList());

            ia.setRespostas(respostas);

            // Marca menor preço como sugerido
            if (!respostas.isEmpty()) {
                ia.setMenorPreco(respostas.get(0).getValorUnitario());
                ia.setFornecedorMenorPreco(respostas.get(0).getCotacaoFornecedor().getFornecedor().getNome());
                ia.setEconomia(calcularEconomia(respostas));
            }

            analise.add(ia);
        }

        return analise;
    }

    /**
     * Calcula economia potencial: diferença entre maior e menor preço.
     */
    private BigDecimal calcularEconomia(List<RespostaCotacao> respostas) {
        if (respostas.size() < 2) return BigDecimal.ZERO;
        BigDecimal menor = respostas.get(0).getValorUnitario();
        BigDecimal maior = respostas.get(respostas.size() - 1).getValorUnitario();
        return maior.subtract(menor);
    }

    /**
     * Retorna totais por fornecedor (soma dos itens onde o fornecedor tem resposta).
     * Útil para decidir concentrar pedido em um único fornecedor.
     */
    @Transactional(readOnly = true)
    public List<TotalFornecedor> calcularTotaisPorFornecedor(Long codigoCotacao) {
        Cotacao cotacao = cotacaoRepository.findById(codigoCotacao)
                .orElseThrow(() -> new RuntimeException("Cotação não encontrada"));

        Map<Long, TotalFornecedor> totais = new LinkedHashMap<>();

        for (CotacaoFornecedor cf : cotacao.getFornecedores()) {
            TotalFornecedor tf = new TotalFornecedor();
            tf.setCotacaoFornecedor(cf);
            tf.setTotal(BigDecimal.ZERO);
            tf.setItensRespondidos(0);
            totais.put(cf.getCodigo(), tf);
        }

        for (CotacaoItem item : cotacao.getItens()) {
            for (RespostaCotacao resposta : item.getRespostas()) {
                Long cfCodigo = resposta.getCotacaoFornecedor().getCodigo();
                if (totais.containsKey(cfCodigo) && resposta.getValorUnitario() != null) {
                    TotalFornecedor tf = totais.get(cfCodigo);
                    BigDecimal valorTotal = resposta.getValorUnitario().multiply(item.getQuantidade());
                    tf.setTotal(tf.getTotal().add(valorTotal));
                    tf.setItensRespondidos(tf.getItensRespondidos() + 1);
                }
            }
        }

        return new ArrayList<>(totais.values());
    }

    /**
     * Seleciona a resposta de menor preço para todos os itens automaticamente.
     * Regra: para cada item, marca a resposta com menor valorUnitario como selecionada.
     */
    @Transactional
    public int selecionarMenorPrecoAutomatico(Long codigoCotacao) {
        Cotacao cotacao = cotacaoRepository.findById(codigoCotacao)
                .orElseThrow(() -> new RuntimeException("Cotação não encontrada"));

        int count = 0;
        for (CotacaoItem item : cotacao.getItens()) {
            // Desmarca todas as respostas do item
            item.getRespostas().forEach(r -> r.setSelecionado(false));

            // Seleciona a de menor preço
            item.getRespostas().stream()
                    .filter(r -> r.getValorUnitario() != null && r.getValorUnitario().signum() > 0)
                    .min(Comparator.comparing(RespostaCotacao::getValorUnitario))
                    .ifPresent(r -> {
                        r.setSelecionado(true);
                    });
            count++;
        }

        cotacao.setSituacao("ANALISADA");
        cotacaoRepository.saveAndFlush(cotacao);
        return count;
    }

    /**
     * Gera pedidos de compra a partir das respostas selecionadas.
     *
     * Regra: agrupa itens selecionados por fornecedor → cria um pedido por fornecedor.
     * Cada pedido recebe os itens com o preço da resposta selecionada.
     *
     * @return lista de pedidos gerados
     */
    @Transactional
    public List<PedidoCompra> gerarPedidos(Long codigoCotacao) {
        Cotacao cotacao = cotacaoRepository.findById(codigoCotacao)
                .orElseThrow(() -> new RuntimeException("Cotação não encontrada"));

        // Agrupa respostas selecionadas por fornecedor
        Map<Long, List<RespostaCotacao>> porFornecedor = new LinkedHashMap<>();

        for (CotacaoItem item : cotacao.getItens()) {
            item.getRespostas().stream()
                    .filter(RespostaCotacao::isSelecionado)
                    .findFirst()
                    .ifPresent(resposta -> {
                        Long fornecedorId = resposta.getCotacaoFornecedor().getFornecedor().getCodigo();
                        porFornecedor.computeIfAbsent(fornecedorId, k -> new ArrayList<>()).add(resposta);
                    });
        }

        if (porFornecedor.isEmpty()) {
            throw new RuntimeException("Nenhuma resposta selecionada. Execute a análise antes de gerar pedidos.");
        }

        List<PedidoCompra> pedidosGerados = new ArrayList<>();

        for (Map.Entry<Long, List<RespostaCotacao>> entry : porFornecedor.entrySet()) {
            List<RespostaCotacao> respostas = entry.getValue();
            RespostaCotacao primeiraResposta = respostas.get(0);

            PedidoCompra pedido = new PedidoCompra();
            pedido.setObra(cotacao.getObra());
            pedido.setFornecedor(primeiraResposta.getCotacaoFornecedor().getFornecedor());
            pedido.setDataPedido(LocalDate.now());
            pedido.setSituacao("ABERTO");

            // Prazo de entrega: maior prazo entre os itens selecionados
            int maxPrazo = respostas.stream()
                    .mapToInt(r -> r.getPrazoEntrega() != null ? r.getPrazoEntrega() : 0)
                    .max().orElse(0);
            if (maxPrazo > 0) {
                pedido.setDataEntrega(LocalDate.now().plusDays(maxPrazo));
            }

            // Cria itens do pedido
            BigDecimal totalPedido = BigDecimal.ZERO;
            for (RespostaCotacao resposta : respostas) {
                CotacaoItem cotacaoItem = resposta.getCotacaoItem();

                PedidoItem pedidoItem = new PedidoItem();
                pedidoItem.setPedido(pedido);
                pedidoItem.setDescricao(cotacaoItem.getDescricao());
                pedidoItem.setUnidade(cotacaoItem.getUnidade());
                pedidoItem.setQuantidade(cotacaoItem.getQuantidade());
                pedidoItem.setValorUnitario(resposta.getValorUnitario());
                pedidoItem.setValorTotal(resposta.getValorUnitario().multiply(cotacaoItem.getQuantidade()));
                if (cotacaoItem.getInsumo() != null) {
                    pedidoItem.setInsumo(cotacaoItem.getInsumo());
                }
                pedido.getItens().add(pedidoItem);
                totalPedido = totalPedido.add(pedidoItem.getValorTotal());
            }

            pedido.setValorTotal(totalPedido);
            pedidoRepository.saveAndFlush(pedido);
            pedidosGerados.add(pedido);
        }

        // Encerra a cotação
        cotacao.setSituacao("ENCERRADA");
        cotacaoRepository.saveAndFlush(cotacao);

        return pedidosGerados;
    }

    // ---- DTOs internos ----

    public static class ItemAnalise {
        private CotacaoItem item;
        private List<RespostaCotacao> respostas = new ArrayList<>();
        private BigDecimal menorPreco;
        private String fornecedorMenorPreco;
        private BigDecimal economia;

public CotacaoItem getItem() {
	return item;
}

public void setItem(CotacaoItem item) {
	this.item = item;
}

public List<RespostaCotacao> getRespostas() {
	return respostas;
}

public void setRespostas(List<RespostaCotacao> respostas) {
	this.respostas = respostas;
}

public BigDecimal getMenorPreco() {
	return menorPreco;
}

public void setMenorPreco(BigDecimal menorPreco) {
	this.menorPreco = menorPreco;
}

public String getFornecedorMenorPreco() {
	return fornecedorMenorPreco;
}

public void setFornecedorMenorPreco(String fornecedorMenorPreco) {
	this.fornecedorMenorPreco = fornecedorMenorPreco;
}

public BigDecimal getEconomia() {
	return economia;
}

public void setEconomia(BigDecimal economia) {
	this.economia = economia;
}

    }

    public static class TotalFornecedor {
        private CotacaoFornecedor cotacaoFornecedor;
        private BigDecimal total;
        private int itensRespondidos;

public CotacaoFornecedor getCotacaoFornecedor() {
	return cotacaoFornecedor;
}

public void setCotacaoFornecedor(CotacaoFornecedor cotacaoFornecedor) {
	this.cotacaoFornecedor = cotacaoFornecedor;
}

public BigDecimal getTotal() {
	return total;
}

public void setTotal(BigDecimal total) {
	this.total = total;
}

public int getItensRespondidos() {
	return itensRespondidos;
}

public void setItensRespondidos(int itensRespondidos) {
	this.itensRespondidos = itensRespondidos;
}

    }
}
