package com.sinapipro.api.report;

import com.sinapipro.api.measurement.domain.*;
import com.sinapipro.api.dailylog.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MeasurementReportService {

    private final ReportService reportService;
    private final MeasurementRepository measurementRepo;
    private final DailyLogRepository dailyLogRepo;

    public MeasurementReportService(ReportService reportService, MeasurementRepository measurementRepo, DailyLogRepository dailyLogRepo) {
        this.reportService = reportService; this.measurementRepo = measurementRepo; this.dailyLogRepo = dailyLogRepo;
    }

    public byte[] boletimAcumulado(UUID id) { return reportService.generatePdf("reports/measurement/boletim-acumulado.jte", Map.of("measurement", measurementRepo.findById(id).orElseThrow())); }
    public byte[] medicaoEmpreiteiro(UUID id) { return reportService.generatePdf("reports/measurement/medicao-empreiteiro.jte", Map.of("measurement", measurementRepo.findById(id).orElseThrow())); }
    public byte[] resumoMedicoes(UUID projectId) { return reportService.generatePdf("reports/measurement/resumo-medicoes.jte", Map.of("projectId", projectId)); }
    public byte[] memoriaCalculo(UUID id) { return reportService.generatePdf("reports/measurement/memoria-calculo.jte", Map.of("measurement", measurementRepo.findById(id).orElseThrow())); }
    public byte[] rdoCompleto(UUID id) { return reportService.generatePdf("reports/measurement/rdo-completo.jte", Map.of("dailyLog", dailyLogRepo.findById(id).orElseThrow())); }
    public byte[] cronogramaFisicoFinanceiro(UUID projectId) { return reportService.generatePdf("reports/measurement/cronograma.jte", Map.of("projectId", projectId)); }
    public byte[] curvaS(UUID projectId) { return reportService.generatePdf("reports/measurement/curva-s.jte", Map.of("projectId", projectId)); }
    public byte[] diarioConsolidado(UUID projectId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/measurement/diario-consolidado.jte", Map.of("projectId", projectId, "from", from, "to", to)); }
}
