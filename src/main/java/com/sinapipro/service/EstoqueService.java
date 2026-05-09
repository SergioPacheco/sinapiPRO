package com.sinapipro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Estoque;
import com.sinapipro.model.MovimentoEstoque;
import com.sinapipro.repository.EstoqueRepository;

/**
 * Lógica de negócio para gestão de estoque com custo médio ponderado.
 *
 * MÉTODO: Custo Médio Ponderado (CMP) — padrão para construção civil.
 *
 * REGRAS:
 *
 * 1. ENTRADA (recebimento de material):
 *    - Novo custo médio = (qtd_atual × custo_médio_atual + qtd_entrada × custo_entrada)
 *                         / (qtd_atual + qtd_entrada)
 *    - Quantidade atual += quantidade entrada
 *
 * 2. SAÍDA (consumo/requisição):
 *    - Custo da saída = quantidade saída × custo médio atual
 *    - Quantidade atual -= quantidade saída
 *    - Custo médio NÃO muda na saída
 *
 * 3. AJUSTE DE INVENTÁRIO:
 *    - Quantidade ajustada diretamente (inventário físico)
 *    - Custo médio mantido
 *
 * 4. ALERTA DE ESTOQUE MÍNIMO:
 *    - Quando quantidade_atual <= quantidade_minima → alerta
 *
 * Referência: NBC TG 16 (CPC 16) — Estoques, método custo médio ponderado.
 */
@Service
public class EstoqueService {

    private final EstoqueRepository repository;

    public EstoqueService(EstoqueRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra uma movimentação de estoque e atualiza o saldo e custo médio.
     *
     * @param codigoEstoque código do item de estoque
     * @param movimento     movimentação a registrar (ENTRADA, SAIDA, AJUSTE)
     * @param custoUnitario custo unitário (obrigatório para ENTRADA, ignorado para SAIDA)
     * @return estoque atualizado
     */
    @Transactional
    public Estoque movimentar(Long codigoEstoque, MovimentoEstoque movimento, BigDecimal custoUnitario) {
        Estoque estoque = repository.findById(codigoEstoque)
                .orElseThrow(() -> new RuntimeException("Item de estoque não encontrado"));

        BigDecimal qtdMovimento = movimento.getQuantidade();

        switch (movimento.getTipo()) {
            case "ENTRADA":
                processarEntrada(estoque, qtdMovimento, custoUnitario);
                break;

            case "SAIDA":
                processarSaida(estoque, qtdMovimento);
                break;

            case "AJUSTE":
                // Ajuste de inventário: define quantidade diretamente
                estoque.setQuantidadeAtual(qtdMovimento);
                break;

            default:
                throw new RuntimeException("Tipo de movimentação inválido: " + movimento.getTipo()
                        + ". Use: ENTRADA, SAIDA ou AJUSTE");
        }

        movimento.setEstoque(estoque);
        estoque.getMovimentos().add(movimento);
        return repository.saveAndFlush(estoque);
    }

    /**
     * Processa entrada de material com atualização do custo médio ponderado.
     *
     * Fórmula CMP:
     * novo_custo_medio = (qtd_atual × custo_medio_atual + qtd_entrada × custo_entrada)
     *                    / (qtd_atual + qtd_entrada)
     */
    private void processarEntrada(Estoque estoque, BigDecimal qtdEntrada, BigDecimal custoEntrada) {
        if (custoEntrada == null || custoEntrada.signum() <= 0) {
            throw new RuntimeException("Custo unitário é obrigatório para entradas de estoque.");
        }

        BigDecimal qtdAtual = estoque.getQuantidadeAtual();
        BigDecimal custoMedioAtual = estoque.getCustoMedio() != null
                ? estoque.getCustoMedio()
                : BigDecimal.ZERO;

        BigDecimal novaQtd = qtdAtual.add(qtdEntrada);

        if (novaQtd.signum() > 0) {
            // Custo médio ponderado
            BigDecimal valorAtual = qtdAtual.multiply(custoMedioAtual);
            BigDecimal valorEntrada = qtdEntrada.multiply(custoEntrada);
            BigDecimal novoCustoMedio = valorAtual.add(valorEntrada)
                    .divide(novaQtd, 4, RoundingMode.HALF_UP);
            estoque.setCustoMedio(novoCustoMedio);
        }

        estoque.setQuantidadeAtual(novaQtd);
    }

    /**
     * Processa saída de material usando o custo médio atual.
     */
    private void processarSaida(Estoque estoque, BigDecimal qtdSaida) {
        BigDecimal qtdAtual = estoque.getQuantidadeAtual();

        if (qtdSaida.compareTo(qtdAtual) > 0) {
            throw new RuntimeException(String.format(
                    "Quantidade insuficiente em estoque. Disponível: %.2f, Solicitado: %.2f",
                    qtdAtual, qtdSaida));
        }

        estoque.setQuantidadeAtual(qtdAtual.subtract(qtdSaida));
        // Custo médio não muda na saída (método CMP)
    }

    /**
     * Retorna itens com estoque abaixo do mínimo (alertas).
     */
    @Transactional(readOnly = true)
    public List<Estoque> findAbaixoDoMinimo(Long codigoObra) {
        return repository.findByObraCodigo(codigoObra).stream()
                .filter(e -> e.getQuantidadeAtual().compareTo(e.getQuantidadeMinima()) <= 0)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Estoque> findByObra(Long codigoObra) {
        return repository.findByObraCodigo(codigoObra);
    }

    @Transactional(readOnly = true)
    public Estoque buscarComMovimentos(Long codigo) {
        return repository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
    }

    @Transactional
    public Estoque salvar(Estoque e) {
        return repository.saveAndFlush(e);
    }
}
