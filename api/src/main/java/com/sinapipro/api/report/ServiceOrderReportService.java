package com.sinapipro.api.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Relatórios de Ordem de Serviço / Atendimento.
 * Equivalentes Strato: ATExxxxx (14), SLCxxxxx (11)
 */
@Service
@Transactional(readOnly = true)
public class ServiceOrderReportService {

    private final ReportService reportService;
    public ServiceOrderReportService(ReportService reportService) { this.reportService = reportService; }

    // ATE — Atendimento
    public byte[] fichaAtendimento(UUID ticketId) { return reportService.generatePdf("reports/serviceorder/ficha-atendimento.jte", Map.of("ticketId", ticketId)); }
    public byte[] historicoAtendimentos(UUID clientId) { return reportService.generatePdf("reports/serviceorder/historico-atendimentos.jte", Map.of("clientId", clientId)); }
    public byte[] atendimentosPorPeriodo(LocalDate from, LocalDate to) { return reportService.generatePdf("reports/serviceorder/atendimentos-periodo.jte", Map.of("from", from, "to", to)); }
    public byte[] atendimentosPorCategoria() { return reportService.generatePdf("reports/serviceorder/atendimentos-categoria.jte", Map.of()); }
    public byte[] slaReport() { return reportService.generatePdf("reports/serviceorder/sla-report.jte", Map.of()); }
    public byte[] backlogAtendimentos() { return reportService.generatePdf("reports/serviceorder/backlog.jte", Map.of()); }

    // SLC — Solicitações / Aprovações
    public byte[] solicitacoesPendentes() { return reportService.generatePdf("reports/serviceorder/solicitacoes-pendentes.jte", Map.of()); }
    public byte[] historicoAprovacoes(UUID projectId) { return reportService.generatePdf("reports/serviceorder/historico-aprovacoes.jte", Map.of("projectId", projectId)); }
    public byte[] solicitacoesPorStatus(UUID projectId) { return reportService.generatePdf("reports/serviceorder/solicitacoes-status.jte", Map.of("projectId", projectId)); }
}
