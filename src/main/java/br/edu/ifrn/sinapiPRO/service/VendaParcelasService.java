package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Indice;
import br.edu.ifrn.sinapiPRO.model.ParcelaVenda;
import br.edu.ifrn.sinapiPRO.model.TabelaPreco;
import br.edu.ifrn.sinapiPRO.model.TabelaPrecoItem;
import br.edu.ifrn.sinapiPRO.model.UnidadeVenda;
import br.edu.ifrn.sinapiPRO.model.Venda;
import br.edu.ifrn.sinapiPRO.repository.IndicesRepository;
import br.edu.ifrn.sinapiPRO.repository.TabelasPrecosRepository;
import br.edu.ifrn.sinapiPRO.repository.VendasRepository;

/**
 * Lógica de negócio para vendas de imóveis/unidades.
 *
 * REGRAS PADRÃO (incorporação imobiliária brasileira):
 *
 * 1. GERAÇÃO DE PARCELAS:
 *    - Entrada: percentual do valor total pago na assinatura
 *    - Parcelas mensais: valor restante dividido em N meses
 *    - Chaves: percentual pago na entrega das chaves
 *    - Vencimento: dia fixo do mês (padrão: dia 10)
 *
 * 2. REAJUSTE POR ÍNDICE (INCC/IPCA/CUB):
 *    - Parcelas em aberto são reajustadas pelo índice acumulado
 *    - Novo valor = valor_original × (1 + índice_acumulado/100)
 *    - Aplicado mensalmente ou na data de reajuste contratual
 *
 * 3. TABELA DE PREÇOS:
 *    - Valor da unidade vem da tabela de preços vigente
 *    - Pode ter desconto negociado
 *
 * Referência: Lei 4.591/64 (incorporação imobiliária), práticas de mercado.
 */
@Service
public class VendaParcelasService {

    @Autowired
    private VendasRepository vendaRepository;

    @Autowired
    private TabelasPrecosRepository tabelaRepository;

    @Autowired
    private IndicesRepository indiceRepository;

    /**
     * Gera parcelas automaticamente para uma venda.
     *
     * @param codigoVenda       código da venda
     * @param percentualEntrada % do valor pago na entrada (ex: 20.0 = 20%)
     * @param numeroParcelas    número de parcelas mensais
     * @param percentualChaves  % pago na entrega das chaves (ex: 10.0 = 10%)
     * @param diaVencimento     dia do mês para vencimento (ex: 10)
     * @param dataInicio        data da primeira parcela
     */
    @Transactional
    public List<ParcelaVenda> gerarParcelas(Long codigoVenda,
            BigDecimal percentualEntrada,
            int numeroParcelas,
            BigDecimal percentualChaves,
            int diaVencimento,
            LocalDate dataInicio) {

        Venda venda = vendaRepository.findById(codigoVenda)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        if (!venda.getParcelas().isEmpty()) {
            throw new RuntimeException("Esta venda já possui parcelas geradas. Exclua as existentes antes de gerar novas.");
        }

        BigDecimal valorTotal = venda.getValorVenda();
        List<ParcelaVenda> parcelas = new ArrayList<>();
        int numeroParcela = 1;

        // 1. ENTRADA
        if (percentualEntrada != null && percentualEntrada.signum() > 0) {
            BigDecimal valorEntrada = valorTotal
                    .multiply(percentualEntrada)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            ParcelaVenda entrada = new ParcelaVenda();
            entrada.setVenda(venda);
            entrada.setNumero(numeroParcela++);
            entrada.setValor(valorEntrada);
            entrada.setDataVencimento(dataInicio);
            entrada.setSituacao("ABERTA");
            parcelas.add(entrada);
        }

        // 2. PARCELAS MENSAIS
        BigDecimal percentualParcelas = BigDecimal.valueOf(100)
                .subtract(percentualEntrada != null ? percentualEntrada : BigDecimal.ZERO)
                .subtract(percentualChaves != null ? percentualChaves : BigDecimal.ZERO);

        if (numeroParcelas > 0 && percentualParcelas.signum() > 0) {
            BigDecimal valorParcelas = valorTotal
                    .multiply(percentualParcelas)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal valorParcela = valorParcelas
                    .divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_UP);

            // Ajuste de centavos na última parcela
            BigDecimal totalParcelas = valorParcela.multiply(BigDecimal.valueOf(numeroParcelas));
            BigDecimal ajuste = valorParcelas.subtract(totalParcelas);

            for (int i = 0; i < numeroParcelas; i++) {
                LocalDate vencimento = dataInicio.plusMonths(i + 1)
                        .withDayOfMonth(Math.min(diaVencimento, dataInicio.plusMonths(i + 1).lengthOfMonth()));

                ParcelaVenda parcela = new ParcelaVenda();
                parcela.setVenda(venda);
                parcela.setNumero(numeroParcela++);
                parcela.setValor(i == numeroParcelas - 1
                        ? valorParcela.add(ajuste)  // última parcela absorve ajuste de centavos
                        : valorParcela);
                parcela.setDataVencimento(vencimento);
                parcela.setSituacao("ABERTA");
                parcelas.add(parcela);
            }
        }

        // 3. CHAVES (última parcela)
        if (percentualChaves != null && percentualChaves.signum() > 0) {
            BigDecimal valorChaves = valorTotal
                    .multiply(percentualChaves)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            LocalDate vencimentoChaves = dataInicio.plusMonths(numeroParcelas + 1)
                    .withDayOfMonth(Math.min(diaVencimento, dataInicio.plusMonths(numeroParcelas + 1).lengthOfMonth()));

            ParcelaVenda chaves = new ParcelaVenda();
            chaves.setVenda(venda);
            chaves.setNumero(numeroParcela);
            chaves.setValor(valorChaves);
            chaves.setDataVencimento(vencimentoChaves);
            chaves.setSituacao("ABERTA");
            parcelas.add(chaves);
        }

        venda.getParcelas().addAll(parcelas);
        vendaRepository.saveAndFlush(venda);
        return parcelas;
    }

    /**
     * Aplica reajuste por índice econômico nas parcelas em aberto.
     *
     * Regra: novo_valor = valor_atual × (1 + percentual_indice/100)
     *
     * @param codigoVenda      código da venda
     * @param codigoIndice     índice a aplicar (INCC, IPCA, CUB, etc.)
     * @param percentualIndice percentual do índice no período (ex: 0.52 = 0,52%)
     * @return número de parcelas reajustadas
     */
    @Transactional
    public int reajustarParcelas(Long codigoVenda, Long codigoIndice, BigDecimal percentualIndice) {
        Venda venda = vendaRepository.findById(codigoVenda)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        Indice indice = indiceRepository.findById(codigoIndice)
                .orElseThrow(() -> new RuntimeException("Índice não encontrado"));

        if (percentualIndice == null || percentualIndice.signum() == 0) {
            throw new RuntimeException("Percentual do índice não pode ser zero.");
        }

        BigDecimal fator = BigDecimal.ONE.add(
                percentualIndice.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

        int count = 0;
        for (ParcelaVenda parcela : venda.getParcelas()) {
            if ("ABERTA".equals(parcela.getSituacao())) {
                BigDecimal novoValor = parcela.getValor()
                        .multiply(fator)
                        .setScale(2, RoundingMode.HALF_UP);
                parcela.setValor(novoValor);
                count++;
            }
        }

        if (count > 0) {
            // Recalcula valor total da venda
            BigDecimal novoTotal = venda.getParcelas().stream()
                    .map(ParcelaVenda::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            venda.setValorVenda(novoTotal);
            vendaRepository.saveAndFlush(venda);
        }

        return count;
    }

    /**
     * Busca o valor da unidade na tabela de preços vigente.
     * Retorna null se não houver tabela ativa para a obra.
     */
    @Transactional(readOnly = true)
    public BigDecimal buscarValorNaTabelaPrecos(UnidadeVenda unidade) {
        List<TabelaPreco> tabelas = tabelaRepository.findByObraCodigoAndAtivaTrue(
                unidade.getObra().getCodigo());

        if (tabelas.isEmpty()) return null;

        // Usa a tabela mais recente (maior data de vigência)
        TabelaPreco tabela = tabelas.stream()
                .filter(t -> t.getDataVigencia() != null)
                .max((a, b) -> a.getDataVigencia().compareTo(b.getDataVigencia()))
                .orElse(tabelas.get(0));

        return tabela.getItens().stream()
                .filter(item -> item.getUnidade().getCodigo().equals(unidade.getCodigo()))
                .map(TabelaPrecoItem::getValor)
                .findFirst()
                .orElse(null);
    }
}
