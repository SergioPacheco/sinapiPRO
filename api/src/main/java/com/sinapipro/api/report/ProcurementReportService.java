package com.sinapipro.api.report;

import com.sinapipro.api.procurement.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProcurementReportService {

    private final ReportService reportService;
    private final PurchaseRequestRepository requestRepo;
    private final PurchaseOrderRepository orderRepo;
    private final PurchaseOrderItemRepository orderItemRepo;
    private final QuotationRepository quotationRepo;
    private final ProcurementScheduleRepository scheduleRepo;

    public ProcurementReportService(ReportService reportService, PurchaseRequestRepository requestRepo,
                                     PurchaseOrderRepository orderRepo, PurchaseOrderItemRepository orderItemRepo,
                                     QuotationRepository quotationRepo, ProcurementScheduleRepository scheduleRepo) {
        this.reportService = reportService; this.requestRepo = requestRepo; this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo; this.quotationRepo = quotationRepo; this.scheduleRepo = scheduleRepo;
    }

    public byte[] requisicao(UUID id) { return reportService.generatePdf("reports/procurement/requisicao.jte", Map.of("request", requestRepo.findById(id).orElseThrow())); }
    public byte[] mapaComparativo(UUID id) { return reportService.generatePdf("reports/procurement/mapa-comparativo.jte", Map.of("quotation", quotationRepo.findById(id).orElseThrow())); }
    public byte[] pedidoCompra(UUID id) { return reportService.generatePdf("reports/procurement/pedido-compra.jte", Map.of("order", orderRepo.findById(id).orElseThrow(), "items", orderItemRepo.findByOrderId(id))); }
    public byte[] pedidosAtraso(UUID projectId) { return reportService.generatePdf("reports/procurement/pedidos-atraso.jte", Map.of("projectId", projectId)); }
    public byte[] curvaAbcInsumos(UUID budgetId) { return reportService.generatePdf("reports/procurement/curva-abc-insumos.jte", Map.of("budgetId", budgetId)); }
    public byte[] curvaAbcFornecedores(UUID projectId) { return reportService.generatePdf("reports/procurement/curva-abc-fornecedores.jte", Map.of("projectId", projectId)); }
    public byte[] cronogramaCompras(UUID projectId) { return reportService.generatePdf("reports/procurement/cronograma-compras.jte", Map.of("schedule", scheduleRepo.findByProjectIdOrderByPlannedDate(projectId))); }
    public byte[] notaRecebimento(UUID id) { return reportService.generatePdf("reports/procurement/nota-recebimento.jte", Map.of("order", orderRepo.findById(id).orElseThrow(), "items", orderItemRepo.findByOrderId(id))); }
}
