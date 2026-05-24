package com.sinapipro.api.report;

import com.sinapipro.api.budget.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BudgetReportService {

    private final ReportService reportService;
    private final BudgetRepository budgetRepo;
    private final BudgetItemRepository itemRepo;
    private final BudgetStageRepository stageRepo;

    public BudgetReportService(ReportService reportService, BudgetRepository budgetRepo, BudgetItemRepository itemRepo, BudgetStageRepository stageRepo) {
        this.reportService = reportService; this.budgetRepo = budgetRepo; this.itemRepo = itemRepo; this.stageRepo = stageRepo;
    }

    /** Planilha Sintética — etapas + itens + totais */
    public byte[] sintetico(UUID id) {
        var budget = budgetRepo.findById(id).orElseThrow();
        var stages = stageRepo.findRootStages(id);
        var total = itemRepo.sumDirectCostByBudget(id);

        var sb = new StringBuilder();
        sb.append(header("Planilha Orçamentária Sintética", budget));
        sb.append("<table><thead><tr><th style='width:60px'>Item</th><th style='width:55px'>Cód.Ref</th><th>Descrição</th><th style='width:25px'>Un</th><th class='r' style='width:65px'>Quantidade</th><th class='r' style='width:65px'>Valor Unit.</th><th class='r' style='width:75px'>Total</th></tr></thead><tbody>");

        int seq = 0;
        for (var stage : stages) {
            sb.append("<tr class='etapa'><td colspan='7'>").append(esc(stage.getName())).append("</td></tr>");
            for (var item : stage.getItems()) {
                seq++;
                sb.append("<tr><td class='code'>").append(String.format("%03d", seq)).append("</td>");
                sb.append("<td class='code'>").append(esc(item.getComposition().getSinapiCode())).append("</td>");
                sb.append("<td>").append(esc(item.getComposition().getDescription())).append("</td>");
                sb.append("<td>").append(esc(item.getComposition().getUnit())).append("</td>");
                sb.append("<td class='r'>").append(fmt4(item.getQuantity())).append("</td>");
                sb.append("<td class='r'>").append(fmt2(item.getUnitCost())).append("</td>");
                sb.append("<td class='r'>").append(fmt2(item.getDirectCost())).append("</td></tr>");
            }
        }
        sb.append("<tr class='total'><td colspan='6' style='text-align:right;padding-right:8px'>TOTAL GERAL</td><td class='r'>").append(fmt2(total)).append("</td></tr>");
        sb.append("</tbody></table>").append(footer());
        return reportService.htmlToPdf(sb.toString());
    }

    /** Planilha Analítica — composições abertas com insumos */
    public byte[] analitico(UUID id) {
        var budget = budgetRepo.findById(id).orElseThrow();
        var stages = stageRepo.findRootStages(id);
        var total = itemRepo.sumDirectCostByBudget(id);

        var sb = new StringBuilder();
        sb.append(header("Planilha Orçamentária Analítica", budget));
        sb.append("<table><thead><tr><th style='width:55px'>Código</th><th>Descrição</th><th style='width:25px'>Un</th><th class='r' style='width:65px'>Coef./Qtd</th><th class='r' style='width:65px'>Valor Unit.</th><th class='r' style='width:75px'>Total</th></tr></thead><tbody>");

        for (var stage : stages) {
            sb.append("<tr class='etapa'><td colspan='6'>").append(esc(stage.getName())).append("</td></tr>");
            for (var item : stage.getItems()) {
                sb.append("<tr class='comp'><td class='code'>").append(esc(item.getComposition().getSinapiCode())).append("</td>");
                sb.append("<td>").append(esc(item.getComposition().getDescription())).append("</td>");
                sb.append("<td>").append(esc(item.getComposition().getUnit())).append("</td>");
                sb.append("<td class='r'>").append(fmt4(item.getQuantity())).append("</td>");
                sb.append("<td class='r'>").append(fmt2(item.getUnitCost())).append("</td>");
                sb.append("<td class='r'>").append(fmt2(item.getDirectCost())).append("</td></tr>");
                for (var ci : item.getComposition().getItems()) {
                    var mat = ci.getMaterial();
                    if (mat != null) {
                        sb.append("<tr class='insumo'><td class='code'>").append(esc(mat.getSinapiCode())).append("</td>");
                        sb.append("<td>&#160;&#160;&#160;&#8627; ").append(esc(mat.getDescription())).append("</td>");
                        sb.append("<td>").append(esc(mat.getUnit())).append("</td>");
                        sb.append("<td class='r'>").append(fmt4(ci.getCoefficient())).append("</td>");
                        sb.append("<td class='r'></td><td class='r'></td></tr>");
                    }
                }
            }
        }
        sb.append("<tr class='total'><td colspan='5' style='text-align:right;padding-right:8px'>TOTAL</td><td class='r'>").append(fmt2(total)).append("</td></tr>");
        sb.append("</tbody></table>").append(footer());
        return reportService.htmlToPdf(sb.toString());
    }

    /** Curva ABC de Serviços */
    public byte[] cpu(UUID id) {
        var items = itemRepo.findAllByBudgetId(id);
        var total = items.stream().map(BudgetItem::getDirectCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        var budget = budgetRepo.findById(id).orElseThrow();
        items.sort((a, b) -> b.getDirectCost().compareTo(a.getDirectCost()));

        var sb = new StringBuilder();
        sb.append(header("Curva ABC de Serviços", budget));
        sb.append("<table><thead><tr><th>Código</th><th>Descrição</th><th>Un</th><th class='r'>Qtd</th><th class='r'>Unit.</th><th class='r'>Total</th><th class='r'>%</th><th class='r'>Acum.</th><th>Classe</th></tr></thead><tbody>");

        BigDecimal acum = BigDecimal.ZERO;
        for (var item : items) {
            acum = acum.add(item.getDirectCost());
            var pct = total.compareTo(BigDecimal.ZERO) != 0 ? item.getDirectCost().multiply(BigDecimal.valueOf(100)).divide(total, 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
            var acumPct = total.compareTo(BigDecimal.ZERO) != 0 ? acum.multiply(BigDecimal.valueOf(100)).divide(total, 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
            var classe = acumPct.compareTo(BigDecimal.valueOf(80)) <= 0 ? "A" : acumPct.compareTo(BigDecimal.valueOf(95)) <= 0 ? "B" : "C";
            sb.append("<tr><td class='code'>").append(esc(item.getComposition().getSinapiCode())).append("</td>");
            sb.append("<td>").append(esc(item.getComposition().getDescription())).append("</td>");
            sb.append("<td>").append(esc(item.getComposition().getUnit())).append("</td>");
            sb.append("<td class='r'>").append(fmt4(item.getQuantity())).append("</td>");
            sb.append("<td class='r'>").append(fmt2(item.getUnitCost())).append("</td>");
            sb.append("<td class='r'>").append(fmt2(item.getDirectCost())).append("</td>");
            sb.append("<td class='r'>").append(fmt2(pct)).append("%</td>");
            sb.append("<td class='r'>").append(fmt2(acumPct)).append("%</td>");
            sb.append("<td style='font-weight:bold;color:").append("A".equals(classe) ? "#cc0000" : "B".equals(classe) ? "#0066cc" : "#006600").append("'>").append(classe).append("</td></tr>");
        }
        sb.append("</tbody></table>").append(footer());
        return reportService.htmlToPdf(sb.toString());
    }

    // Stubs para outros relatórios
    public byte[] cronogramaFinanceiro(UUID id) { return reportService.generatePdf("cronograma", java.util.Map.of("budgetId", id)); }
    public byte[] analiseCompras(UUID id) { return reportService.generatePdf("analise-compras", java.util.Map.of("budgetId", id)); }
    public byte[] comparativo(UUID id1, UUID id2) { return reportService.generatePdf("comparativo", java.util.Map.of("budgetId1", id1, "budgetId2", id2)); }
    public byte[] listagemInsumos(UUID id) { return reportService.generatePdf("listagem-insumos", java.util.Map.of("items", itemRepo.findAllByBudgetId(id))); }
    public byte[] bdiDetalhado(UUID id) { return reportService.generatePdf("bdi-detalhado", java.util.Map.of("budgetId", id)); }

    // === Helpers ===
    private String header(String title, Budget budget) {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml"><head><style>
            @page { size: A4 landscape; margin: 12mm; }
            body { font-family: Arial, sans-serif; font-size: 8px; color: #000; margin: 0; }
            .header { border-bottom: 2px solid #003366; padding-bottom: 6px; margin-bottom: 8px; }
            .header h1 { font-size: 13px; color: #003366; margin: 0; }
            .header p { font-size: 9px; color: #333; margin: 2px 0; }
            table { width: 100%%; border-collapse: collapse; }
            th { background: #003366; color: white; padding: 4px 5px; font-size: 7px; text-transform: uppercase; text-align: left; }
            th.r { text-align: right; }
            td { padding: 3px 5px; border-bottom: 0.5px solid #ccc; font-size: 8px; }
            td.r { text-align: right; font-family: 'Courier New', monospace; }
            td.code { font-family: 'Courier New', monospace; font-size: 7px; color: #555; }
            tr.etapa td { background: #fff0f0; color: #cc0000; font-weight: bold; border-bottom: 1px solid #cc0000; font-size: 8px; }
            tr.comp td { font-weight: bold; }
            tr.insumo td { color: #006600; font-size: 7px; }
            tr.total td { background: #003366; color: white; font-weight: bold; font-size: 9px; }
            .footer { margin-top: 8px; font-size: 7px; color: #666; border-top: 0.5px solid #ccc; padding-top: 4px; }
            </style></head><body>
            <div class='header'><h1>%s</h1>
            <p><b>%s</b></p>
            <p>Cliente: %s | C&#243;digo: %s | Data: %s</p></div>
            """.formatted(title, esc(budget.getTitle()), esc(budget.getCustomerName()), esc(budget.getCode()), LocalDate.now());
    }

    private String footer() { return "<div class='footer'>SinapiPRO | Gerado em: " + LocalDate.now() + "</div></body></html>"; }
    private String fmt2(BigDecimal v) { return v != null ? String.format("%,.2f", v) : "0,00"; }
    private String fmt4(BigDecimal v) { return v != null ? String.format("%,.4f", v) : "0,0000"; }
    private String esc(String s) { return s != null ? s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") : ""; }
}
