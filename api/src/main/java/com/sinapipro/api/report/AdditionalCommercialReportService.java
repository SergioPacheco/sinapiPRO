package com.sinapipro.api.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Relatórios comerciais adicionais.
 * Equivalentes Strato: VORxxxxx (7), VRSxxxxx (4), TPExxxxx (5), PRCxxxxx (8), CRTxxxxx (5), CRIxxxxx (4), RNKxxxxx (3)
 */
@Service
@Transactional(readOnly = true)
public class AdditionalCommercialReportService {

    private final ReportService reportService;
    public AdditionalCommercialReportService(ReportService reportService) { this.reportService = reportService; }

    // VOR — Vendas por corretor/origem
    public byte[] vendasPorCorretor(UUID developmentId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/commercial/vendas-por-corretor.jte", Map.of("developmentId", developmentId, "from", from, "to", to)); }
    public byte[] vendasPorOrigem(UUID developmentId) { return reportService.generatePdf("reports/commercial/vendas-por-origem.jte", Map.of("developmentId", developmentId)); }
    public byte[] vendasPorPeriodo(UUID developmentId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/commercial/vendas-por-periodo.jte", Map.of("developmentId", developmentId, "from", from, "to", to)); }

    // VRS — Vendas resumo por status
    public byte[] vendasResumoStatus(UUID developmentId) { return reportService.generatePdf("reports/commercial/vendas-resumo-status.jte", Map.of("developmentId", developmentId)); }

    // TPE — Tabela preço empreiteiro
    public byte[] tabelaPrecoEmpreiteiro(UUID tableId) { return reportService.generatePdf("reports/commercial/tabela-preco-empreiteiro.jte", Map.of("tableId", tableId)); }

    // PRC — Tabela de preços (listagem, comparativo)
    public byte[] tabelaPrecos(UUID developmentId) { return reportService.generatePdf("reports/commercial/tabela-precos.jte", Map.of("developmentId", developmentId)); }
    public byte[] comparativoPrecos(UUID tableId1, UUID tableId2) { return reportService.generatePdf("reports/commercial/comparativo-precos.jte", Map.of("tableId1", tableId1, "tableId2", tableId2)); }
    public byte[] historicoPrecos(UUID unitId) { return reportService.generatePdf("reports/commercial/historico-precos.jte", Map.of("unitId", unitId)); }

    // CRT — Contratos (aditivos, medições vinculadas)
    public byte[] contratoComAditivos(UUID contractId) { return reportService.generatePdf("reports/commercial/contrato-aditivos.jte", Map.of("contractId", contractId)); }
    public byte[] medicoesDoContrato(UUID contractId) { return reportService.generatePdf("reports/commercial/medicoes-contrato.jte", Map.of("contractId", contractId)); }
    public byte[] saldoContratual(UUID contractId) { return reportService.generatePdf("reports/commercial/saldo-contratual.jte", Map.of("contractId", contractId)); }

    // CRI — Critérios de avaliação
    public byte[] criteriosAvaliacao() { return reportService.generatePdf("reports/commercial/criterios-avaliacao.jte", Map.of()); }

    // RNK — Ranking fornecedores
    public byte[] rankingFornecedores() { return reportService.generatePdf("reports/commercial/ranking-fornecedores.jte", Map.of()); }
    public byte[] rankingFornecedoresPorCategoria(String category) { return reportService.generatePdf("reports/commercial/ranking-fornecedores-categoria.jte", Map.of("category", category)); }

    // PCM — Plano compras mensal
    public byte[] planoComprasMensal(UUID projectId, LocalDate yearMonth) { return reportService.generatePdf("reports/procurement/plano-compras-mensal.jte", Map.of("projectId", projectId, "yearMonth", yearMonth)); }

    // CUN — Custo unitário
    public byte[] custoUnitario(UUID budgetId) { return reportService.generatePdf("reports/budget/custo-unitario.jte", Map.of("budgetId", budgetId)); }
    public byte[] custoUnitarioComparativo(UUID budgetId) { return reportService.generatePdf("reports/budget/custo-unitario-comparativo.jte", Map.of("budgetId", budgetId)); }

    // MRO — Manutenção/reparo
    public byte[] manutencaoPreventiva(UUID equipmentId) { return reportService.generatePdf("reports/equipment/manutencao-preventiva.jte", Map.of("equipmentId", equipmentId)); }
    public byte[] historicoManutencao(UUID equipmentId) { return reportService.generatePdf("reports/equipment/historico-manutencao.jte", Map.of("equipmentId", equipmentId)); }

    // FIC — Fichas cadastrais
    public byte[] fichaCliente(UUID clientId) { return reportService.generatePdf("reports/registry/ficha-cliente.jte", Map.of("clientId", clientId)); }
    public byte[] fichaFornecedor(UUID supplierId) { return reportService.generatePdf("reports/registry/ficha-fornecedor.jte", Map.of("supplierId", supplierId)); }
}
