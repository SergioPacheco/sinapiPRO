package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Estoque;
import br.edu.ifrn.sinapiPRO.model.ParcelaVenda;
import br.edu.ifrn.sinapiPRO.repository.EstoqueRepository;
import br.edu.ifrn.sinapiPRO.repository.VendasRepository;

/**
 * Relatórios operacionais do sistema.
 */
@Service
public class RelatorioOperacionalService {

    @Autowired
    private VendasRepository vendaRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    // ---- Inadimplência ----

    /**
     * Retorna parcelas em atraso (vencidas e não pagas).
     * Ordenadas por data de vencimento (mais antigas primeiro).
     */
    @Transactional(readOnly = true)
    public List<ParcelaInadimplente> findInadimplentes(Long codigoObra) {
        return vendaRepository.findAll().stream()
                .filter(v -> codigoObra == null
                        || v.getUnidade().getObra().getCodigo().equals(codigoObra))
                .filter(v -> "ATIVA".equals(v.getSituacao()))
                .flatMap(v -> v.getParcelas().stream()
                        .filter(p -> "ABERTA".equals(p.getSituacao()))
                        .filter(p -> p.getDataVencimento().isBefore(LocalDate.now()))
                        .map(p -> {
                            ParcelaInadimplente pi = new ParcelaInadimplente();
                            pi.setVenda(v);
                            pi.setParcela(p);
                            pi.setDiasAtraso(
                                    (int) java.time.temporal.ChronoUnit.DAYS.between(
                                            p.getDataVencimento(), LocalDate.now()));
                            return pi;
                        }))
                .sorted(java.util.Comparator.comparing(pi -> pi.getParcela().getDataVencimento()))
                .collect(Collectors.toList());
    }

    /**
     * Calcula o total inadimplente por obra.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalInadimplente(Long codigoObra) {
        return findInadimplentes(codigoObra).stream()
                .map(pi -> pi.getParcela().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---- Posição de Estoque ----

    /**
     * Retorna a posição atual do estoque de uma obra.
     * Inclui: quantidade atual, custo médio, valor total em estoque, status.
     */
    @Transactional(readOnly = true)
    public List<PosicaoEstoque> getPosicaoEstoque(Long codigoObra) {
        return estoqueRepository.findByObraCodigo(codigoObra).stream()
                .map(e -> {
                    PosicaoEstoque pos = new PosicaoEstoque();
                    pos.setEstoque(e);
                    pos.setValorTotal(e.getQuantidadeAtual()
                            .multiply(e.getCustoMedio() != null ? e.getCustoMedio() : BigDecimal.ZERO));
                    pos.setStatus(calcularStatus(e));
                    return pos;
                })
                .sorted(java.util.Comparator.comparing(p -> p.getEstoque().getInsumo().getDescricao()))
                .collect(Collectors.toList());
    }

    /**
     * Calcula o valor total do estoque de uma obra.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularValorTotalEstoque(Long codigoObra) {
        return getPosicaoEstoque(codigoObra).stream()
                .map(PosicaoEstoque::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String calcularStatus(Estoque e) {
        if (e.getQuantidadeAtual().signum() == 0) return "ZERADO";
        if (e.getQuantidadeAtual().compareTo(e.getQuantidadeMinima()) <= 0) return "CRITICO";
        if (e.getQuantidadeAtual().compareTo(e.getQuantidadeMinima().multiply(new BigDecimal("1.5"))) <= 0) return "BAIXO";
        return "OK";
    }

    // ---- DTOs ----

    public static class ParcelaInadimplente {
        private br.edu.ifrn.sinapiPRO.model.Venda venda;
        private ParcelaVenda parcela;
        private int diasAtraso;

public br.edu.ifrn.sinapiPRO.model.Venda getVenda() {
	return venda;
}

public void setVenda(br.edu.ifrn.sinapiPRO.model.Venda venda) {
	this.venda = venda;
}

public ParcelaVenda getParcela() {
	return parcela;
}

public void setParcela(ParcelaVenda parcela) {
	this.parcela = parcela;
}

public int getDiasAtraso() {
	return diasAtraso;
}

public void setDiasAtraso(int diasAtraso) {
	this.diasAtraso = diasAtraso;
}

    }

    public static class PosicaoEstoque {
        private Estoque estoque;
        private BigDecimal valorTotal;
        private String status;

public Estoque getEstoque() {
	return estoque;
}

public void setEstoque(Estoque estoque) {
	this.estoque = estoque;
}

public BigDecimal getValorTotal() {
	return valorTotal;
}

public void setValorTotal(BigDecimal valorTotal) {
	this.valorTotal = valorTotal;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

    }
}
