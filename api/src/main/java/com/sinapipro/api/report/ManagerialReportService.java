package com.sinapipro.api.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ManagerialReportService {

    private final ReportService reportService;

    public ManagerialReportService(ReportService reportService) { this.reportService = reportService; }

    public byte[] dashboardExecutivo() { return reportService.generatePdf("reports/managerial/dashboard-executivo.jte", Map.of()); }
    public byte[] gerencialResumo(UUID projectId) { return reportService.generatePdf("reports/managerial/gerencial-resumo.jte", Map.of("projectId", projectId)); }
    public byte[] evm(UUID projectId) { return reportService.generatePdf("reports/managerial/evm.jte", Map.of("projectId", projectId)); }
    public byte[] posicaoFinanceiraConsolidada() { return reportService.generatePdf("reports/managerial/posicao-financeira.jte", Map.of()); }
    public byte[] contratos(UUID projectId) { return reportService.generatePdf("reports/managerial/contratos.jte", Map.of("projectId", projectId)); }
    public byte[] seguranca(UUID projectId) { return reportService.generatePdf("reports/managerial/seguranca.jte", Map.of("projectId", projectId)); }
    public byte[] avaliacaoFornecedores() { return reportService.generatePdf("reports/managerial/avaliacao-fornecedores.jte", Map.of()); }
}
