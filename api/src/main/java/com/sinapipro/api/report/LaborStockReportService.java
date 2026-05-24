package com.sinapipro.api.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class LaborStockReportService {

    private final ReportService reportService;

    public LaborStockReportService(ReportService reportService) { this.reportService = reportService; }

    public byte[] folhaResumo(UUID projectId, LocalDate yearMonth) { return reportService.generatePdf("reports/labor/folha-resumo.jte", Map.of("projectId", projectId, "yearMonth", yearMonth)); }
    public byte[] bancoHoras(UUID employeeId, UUID projectId) { return reportService.generatePdf("reports/labor/banco-horas.jte", Map.of("employeeId", employeeId, "projectId", projectId)); }
    public byte[] produtividade(UUID projectId) { return reportService.generatePdf("reports/labor/produtividade.jte", Map.of("projectId", projectId)); }
    public byte[] posicaoEstoque(UUID projectId) { return reportService.generatePdf("reports/labor/posicao-estoque.jte", Map.of("projectId", projectId)); }
    public byte[] movimentacaoEstoque(UUID projectId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/labor/movimentacao-estoque.jte", Map.of("projectId", projectId, "from", from, "to", to)); }
    public byte[] controleEpi(UUID employeeId) { return reportService.generatePdf("reports/labor/controle-epi.jte", Map.of("employeeId", employeeId)); }
    public byte[] fichaEquipamento(UUID equipmentId) { return reportService.generatePdf("reports/labor/ficha-equipamento.jte", Map.of("equipmentId", equipmentId)); }
    public byte[] etiquetasPatrimonio(UUID projectId) { return reportService.generatePdf("reports/labor/etiquetas-patrimonio.jte", Map.of("projectId", projectId)); }
}
