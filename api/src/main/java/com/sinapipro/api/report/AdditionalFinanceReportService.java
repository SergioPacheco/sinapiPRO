package com.sinapipro.api.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Relatórios financeiros adicionais não cobertos pelo FinanceReportService.
 */
@Service
@Transactional(readOnly = true)
public class AdditionalFinanceReportService {

    private final ReportService reportService;
    public AdditionalFinanceReportService(ReportService reportService) { this.reportService = reportService; }

    // IRF — Informe de Rendimentos (DIRF)
    public byte[] informeRendimentos(UUID supplierId, int ano) { return reportService.generatePdf("reports/finance/informe-rendimentos.jte", Map.of("supplierId", supplierId, "ano", ano)); }
    public byte[] informeRendimentosConsolidado(int ano) { return reportService.generatePdf("reports/finance/informe-rendimentos-consolidado.jte", Map.of("ano", ano)); }

    // DIS — Distribuição de despesas (rateio)
    public byte[] distribuicaoDespesas(UUID projectId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/distribuicao-despesas.jte", Map.of("projectId", projectId, "from", from, "to", to)); }
    public byte[] mapaDespesasPorObra(LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/mapa-despesas-obra.jte", Map.of("from", from, "to", to)); }

    // ACF — Acompanhamento financeiro
    public byte[] acompanhamentoFinanceiro(UUID projectId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/acompanhamento-financeiro.jte", Map.of("projectId", projectId, "from", from, "to", to)); }
    public byte[] evolucaoSaldo(UUID bankAccountId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/evolucao-saldo.jte", Map.of("bankAccountId", bankAccountId, "from", from, "to", to)); }

    // POF — Posição financeira
    public byte[] posicaoFinanceiraPorObra(UUID projectId) { return reportService.generatePdf("reports/finance/posicao-financeira-obra.jte", Map.of("projectId", projectId)); }
    public byte[] posicaoFinanceiraGeral() { return reportService.generatePdf("reports/finance/posicao-financeira-geral.jte", Map.of()); }

    // PCO — Plano de contas
    public byte[] planoContas() { return reportService.generatePdf("reports/finance/plano-contas.jte", Map.of()); }
    public byte[] razaoContabil(String contaCode, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/razao-contabil.jte", Map.of("contaCode", contaCode, "from", from, "to", to)); }

    // PER — Períodos / Competência
    public byte[] fechamentoMensal(UUID projectId, LocalDate yearMonth) { return reportService.generatePdf("reports/finance/fechamento-mensal.jte", Map.of("projectId", projectId, "yearMonth", yearMonth)); }
    public byte[] resumoPorCompetencia(LocalDate yearMonth) { return reportService.generatePdf("reports/finance/resumo-competencia.jte", Map.of("yearMonth", yearMonth)); }

    // NOF — Notas fiscais
    public byte[] notasFiscaisEmitidas(LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/notas-fiscais-emitidas.jte", Map.of("from", from, "to", to)); }
    public byte[] notasFiscaisRecebidas(LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/notas-fiscais-recebidas.jte", Map.of("from", from, "to", to)); }

    // LIB — Liberações
    public byte[] liberacoesFinanceiras(UUID projectId) { return reportService.generatePdf("reports/finance/liberacoes.jte", Map.of("projectId", projectId)); }

    // CCH/CHE — Cheques
    public byte[] chequeEmitidos(UUID bankAccountId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/cheques-emitidos.jte", Map.of("bankAccountId", bankAccountId, "from", from, "to", to)); }
    public byte[] chequeCustódia(UUID bankAccountId) { return reportService.generatePdf("reports/finance/cheques-custodia.jte", Map.of("bankAccountId", bankAccountId)); }

    // RSD — Resumo despesas
    public byte[] resumoDespesasPorNatureza(UUID projectId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/resumo-despesas-natureza.jte", Map.of("projectId", projectId, "from", from, "to", to)); }
    public byte[] resumoDespesasPorFornecedor(UUID projectId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/resumo-despesas-fornecedor.jte", Map.of("projectId", projectId, "from", from, "to", to)); }
}
