package com.sinapipro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Contrato;
import com.sinapipro.model.ContratoItem;
import com.sinapipro.model.Despesa;
import com.sinapipro.model.Medicao;
import com.sinapipro.model.MedicaoItem;
import com.sinapipro.repository.ContratosRepository;
import com.sinapipro.repository.DespesasRepository;
import com.sinapipro.repository.MedicoesRepository;
import com.sinapipro.service.exception.ResourceNotFoundException;

/**
 * Lógica de negócio para medições de contratos de construção civil.
 *
 * Regras padrão (baseadas em práticas de construção civil brasileira):
 *
 * 1. CÁLCULO DA MEDIÇÃO:
 *    - Cada item do contrato tem quantidade contratada e valor unitário
 *    - A medição registra a quantidade executada no período
 *    - Valor medido = quantidade medida × valor unitário do contrato
 *    - % executado = (quantidade medida / quantidade contratada) × 100
 *
 * 2. RETENÇÃO (Retainage):
 *    - Percentual retido de cada medição (padrão: 5-10%)
 *    - Liberado ao final do contrato ou após prazo de garantia
 *    - Valor líquido = valor medido - retenção
 *
 * 3. APROVAÇÃO:
 *    - Medição criada com situação ABERTA
 *    - Aprovação muda para APROVADA e gera despesa/fatura
 *    - Pagamento muda para PAGA
 *
 * 4. GERAÇÃO DE DESPESA:
 *    - Ao aprovar, gera automaticamente uma Despesa vinculada ao fornecedor do contrato
 *    - Valor = valor líquido da medição (após retenção)
 */
@Service
public class MedicaoContratoService {

    /** Percentual de retenção padrão (5%) */
    private static final BigDecimal PERCENTUAL_RETENCAO_PADRAO = new BigDecimal("5.00");

    private final MedicoesRepository medicaoRepository;
    private final ContratosRepository contratoRepository;
    private final DespesasRepository despesaRepository;

    public MedicaoContratoService(
            MedicoesRepository medicaoRepository,
            ContratosRepository contratoRepository,
            DespesasRepository despesaRepository) {
        this.medicaoRepository = medicaoRepository;
        this.contratoRepository = contratoRepository;
        this.despesaRepository = despesaRepository;
    }

    /**
     * Calcula o valor de cada item da medição com base no % executado.
     *
     * Regra: valorMedido = (percentualExecutado / 100) × valorTotal do item do contrato
     * Alternativa: valorMedido = quantidadeMedida × valorUnitario do contrato
     */
    @Transactional
    public Medicao calcularMedicao(Medicao medicao) {
        buscarContrato(medicao.getContrato().getCodigo());

        BigDecimal totalMedido = BigDecimal.ZERO;

        for (MedicaoItem item : medicao.getItens()) {
            ContratoItem contratoItem = item.getContratoItem();

            // Calcula valor medido: quantidade medida × valor unitário do contrato
            if (item.getQuantidadeMedida() != null && contratoItem.getValorUnitario() != null) {
                BigDecimal valorMedido = item.getQuantidadeMedida()
                        .multiply(contratoItem.getValorUnitario())
                        .setScale(2, RoundingMode.HALF_UP);
                item.setValorMedido(valorMedido);

                // Calcula % executado em relação à quantidade contratada
                if (contratoItem.getQuantidade() != null && contratoItem.getQuantidade().signum() > 0) {
                    BigDecimal percentual = item.getQuantidadeMedida()
                            .multiply(BigDecimal.valueOf(100))
                            .divide(contratoItem.getQuantidade(), 2, RoundingMode.HALF_UP);
                    item.setPercentualExecutado(percentual);
                }

                totalMedido = totalMedido.add(valorMedido);
            }
        }

        medicao.setValorMedido(totalMedido);
        return medicaoRepository.saveAndFlush(medicao);
    }

    /**
     * Aprova uma medição e gera automaticamente uma Despesa.
     *
     * Regras:
     * - Medição deve estar ABERTA
     * - Calcula retenção (percentual configurável, padrão 5%)
     * - Valor líquido = valor medido - retenção
     * - Gera Despesa com vencimento = data da medição + 30 dias (padrão)
     * - Vincula ao fornecedor do contrato (se houver)
     *
     * @param codigoMedicao código da medição a aprovar
     * @param percentualRetencao percentual de retenção (null = usa padrão 5%)
     * @return despesa gerada
     */
    @Transactional
    public Despesa aprovarMedicao(Long codigoMedicao, BigDecimal percentualRetencao) {
        Medicao medicao = buscarMedicao(codigoMedicao);

        if (!"ABERTA".equals(medicao.getSituacao())) {
            throw new IllegalArgumentException(
                    "Apenas medições ABERTAS podem ser aprovadas. Situação atual: " + medicao.getSituacao());
        }

        BigDecimal retencao = percentualRetencao != null ? percentualRetencao : PERCENTUAL_RETENCAO_PADRAO;
        BigDecimal valorRetido = medicao.getValorMedido()
                .multiply(retencao)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal valorLiquido = medicao.getValorMedido().subtract(valorRetido);

        // Aprova a medição
        medicao.setSituacao("APROVADA");
        medicao.setObservacao(montarObservacaoRetencao(medicao, retencao, valorRetido, valorLiquido));
        medicaoRepository.saveAndFlush(medicao);

        // Gera despesa automaticamente
        Despesa despesa = new Despesa();
        Contrato contrato = medicao.getContrato();

        despesa.setDescricao(String.format("Medição #%d — %s",
                medicao.getNumero() != null ? medicao.getNumero() : 0,
                contrato.getDescricao()));
        despesa.setValor(valorLiquido);
        despesa.setDataVencimento(medicao.getDataMedicao().plusDays(30));
        despesa.setDataCompetencia(medicao.getDataMedicao());
        despesa.setSituacao("ABERTA");
        despesa.setObra(contrato.getObra());

        // Vincula ao fornecedor se o contrato tiver cliente (subempreiteiro)
        // Na prática, o contrato pode ter um fornecedor — aqui usamos o cliente como referência
        despesa.setObservacao(String.format(
                "Gerada automaticamente da Medição #%d do Contrato: %s | Retenção: R$ %.2f",
                medicao.getNumero() != null ? medicao.getNumero() : 0,
                contrato.getNumero() != null ? contrato.getNumero() : contrato.getDescricao(),
                valorRetido));

        return despesaRepository.saveAndFlush(despesa);
    }

    /**
     * Calcula o percentual total executado do contrato até a medição atual.
     * Soma todas as medições aprovadas + a atual.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularPercentualAcumulado(Long codigoContrato) {
        Contrato contrato = buscarContrato(codigoContrato);

        if (contrato.getValorTotal() == null || contrato.getValorTotal().signum() == 0) {
            return BigDecimal.ZERO;
        }

        List<Medicao> medicoes = medicaoRepository.findByContratoCodigoOrderByNumeroAsc(codigoContrato);
        BigDecimal totalMedido = medicoes.stream()
                .filter(m -> "APROVADA".equals(m.getSituacao()) || "PAGA".equals(m.getSituacao()))
                .map(Medicao::getValorMedido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalMedido
                .multiply(BigDecimal.valueOf(100))
                .divide(contrato.getValorTotal(), 2, RoundingMode.HALF_UP);
    }

    /**
     * Retorna o saldo disponível para medição (valor contratado - total já medido).
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoDisponivel(Long codigoContrato) {
        Contrato contrato = buscarContrato(codigoContrato);

        List<Medicao> medicoes = medicaoRepository.findByContratoCodigoOrderByNumeroAsc(codigoContrato);
        BigDecimal totalMedido = medicoes.stream()
                .map(Medicao::getValorMedido)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return contrato.getValorTotal().subtract(totalMedido);
    }

    private Medicao buscarMedicao(Long codigoMedicao) {
        return medicaoRepository.findById(codigoMedicao)
                .orElseThrow(() -> new ResourceNotFoundException("Medição não encontrada."));
    }

    private Contrato buscarContrato(Long codigoContrato) {
        return contratoRepository.findById(codigoContrato)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado."));
    }

    private String montarObservacaoRetencao(
            Medicao medicao,
            BigDecimal retencao,
            BigDecimal valorRetido,
            BigDecimal valorLiquido) {
        String observacaoAtual = medicao.getObservacao() == null ? "" : medicao.getObservacao();
        return observacaoAtual + String.format(
                " | Retenção: %.2f%% = R$ %.2f | Líquido: R$ %.2f",
                retencao,
                valorRetido,
                valorLiquido);
    }
}
