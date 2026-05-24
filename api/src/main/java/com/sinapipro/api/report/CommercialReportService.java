package com.sinapipro.api.report;

import com.sinapipro.api.commercial.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CommercialReportService {

    private final ReportService reportService;
    private final SaleContractRepository contractRepo;
    private final SaleInstallmentRepository installmentRepo;
    private final DevelopmentUnitRepository unitRepo;

    public CommercialReportService(ReportService reportService, SaleContractRepository contractRepo,
                                    SaleInstallmentRepository installmentRepo, DevelopmentUnitRepository unitRepo) {
        this.reportService = reportService; this.contractRepo = contractRepo;
        this.installmentRepo = installmentRepo; this.unitRepo = unitRepo;
    }

    public byte[] fichaImovel(UUID unitId) { return reportService.generatePdf("reports/commercial/ficha-imovel.jte", Map.of("unit", unitRepo.findById(unitId).orElseThrow())); }
    public byte[] contratoVenda(UUID id) { return reportService.generatePdf("reports/commercial/contrato-venda.jte", Map.of("contract", contractRepo.findById(id).orElseThrow(), "installments", installmentRepo.findByContractIdOrderByInstallmentNumber(id))); }
    public byte[] posicaoVendas(UUID devId) { return reportService.generatePdf("reports/commercial/posicao-vendas.jte", Map.of("contracts", contractRepo.findByDevelopmentIdAndStatus(devId, "ACTIVE"))); }
    public byte[] extratoCliente(UUID id) { return reportService.generatePdf("reports/commercial/extrato-cliente.jte", Map.of("contract", contractRepo.findById(id).orElseThrow(), "installments", installmentRepo.findByContractIdOrderByInstallmentNumber(id))); }
    public byte[] comissoes(UUID devId) { return reportService.generatePdf("reports/commercial/comissoes.jte", Map.of("contracts", contractRepo.findByDevelopmentIdAndStatus(devId, "ACTIVE"))); }
    public byte[] inadimplencia(UUID devId) { return reportService.generatePdf("reports/commercial/inadimplencia.jte", Map.of("developmentId", devId)); }
    public byte[] propostaComercial(UUID id) { return reportService.generatePdf("reports/commercial/proposta-comercial.jte", Map.of("contract", contractRepo.findById(id).orElseThrow())); }
    public byte[] distrato(UUID id) { return reportService.generatePdf("reports/commercial/distrato.jte", Map.of("contract", contractRepo.findById(id).orElseThrow())); }
}
