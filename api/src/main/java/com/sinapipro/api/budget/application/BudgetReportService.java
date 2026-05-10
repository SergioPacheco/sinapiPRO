package com.sinapipro.api.budget.application;

import module java.base;

import com.sinapipro.api.budget.domain.BdiConfig;
import com.sinapipro.api.budget.domain.BdiConfigRepository;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.budget.domain.BudgetStage;
import com.sinapipro.api.budget.domain.BudgetStageRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BudgetReportService {

    private final BudgetRepository budgetRepository;
    private final BudgetStageRepository stageRepository;
    private final BudgetItemRepository itemRepository;
    private final BdiConfigRepository bdiConfigRepository;
    private final AbcCurveService abcCurveService;

    public BudgetReportService(BudgetRepository budgetRepository,
                               BudgetStageRepository stageRepository,
                               BudgetItemRepository itemRepository,
                               BdiConfigRepository bdiConfigRepository,
                               AbcCurveService abcCurveService) {
        this.budgetRepository = budgetRepository;
        this.stageRepository = stageRepository;
        this.itemRepository = itemRepository;
        this.bdiConfigRepository = bdiConfigRepository;
        this.abcCurveService = abcCurveService;
    }

    public WorksheetReport buildWorksheetReport(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        BigDecimal subtotal = itemRepository.sumDirectCostByBudget(budgetId).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bdiPct = bdiConfigRepository.findByBudgetId(budgetId)
                .map(BdiConfig::getTotalBdi)
                .orElse(BigDecimal.ZERO);
        BigDecimal bdiTotal = subtotal.multiply(bdiPct).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(bdiTotal).setScale(2, RoundingMode.HALF_UP);

        List<WorksheetReport.Line> lines = new ArrayList<>();
        stageRepository.findRootStages(budgetId).forEach(stage -> appendStage(lines, stage, 1));

        return new WorksheetReport(
                new WorksheetReport.Header(
                        budget.getId(), budget.getCode(), budget.getTitle(), budget.getCustomerName(),
                        budget.getStartDate(), budget.getEndDate(), budget.getStatus().name(), budget.isActive()),
                lines,
                subtotal,
                BigDecimal.ZERO,
                bdiPct,
                bdiTotal,
                BigDecimal.ZERO,
                total);
    }

    public byte[] generateSyntheticWorksheetPdf(UUID budgetId) {
        WorksheetReport report = buildWorksheetReport(budgetId);
        List<String> lines = new ArrayList<>();
        lines.add("RELATORIO DO ORCAMENTO");
        lines.add("ORCAMENTO: " + report.header().code() + " - " + report.header().title());
        lines.add("CLIENTE/OBRA: " + report.header().customerName());
        lines.add("STATUS: " + report.header().status() + (report.header().active() ? " - VIGENTE" : ""));
        lines.add("");
        lines.add(String.format("%-10s %-52s %8s %12s %12s", "CODIGO", "DESCRICAO", "QTD", "UNITARIO", "TOTAL"));
        lines.add("-----------------------------------------------------------------------------------------------");
        report.lines().forEach(line -> lines.add(formatLine(line)));
        lines.add("-----------------------------------------------------------------------------------------------");
        lines.add("Sub-Total: " + money(report.subtotal()));
        lines.add("Leis Sociais (0.00 %): " + money(report.socialChargesTotal()));
        lines.add("BDI (" + percent(report.bdiPct()) + "): " + money(report.bdiTotal()));
        lines.add("Taxa de Administracao (0.00 %): " + money(report.administrationTotal()));
        lines.add("Total do Orcamento: " + money(report.total()));
        return SimplePdf.write(lines);
    }

    public byte[] generateServiceAbcPdf(UUID budgetId) {
        WorksheetReport report = buildWorksheetReport(budgetId);
        List<String> lines = new ArrayList<>();
        lines.add("CURVA ABC DO ORCAMENTO");
        lines.add("ORCAMENTO: " + report.header().code() + " - " + report.header().title());
        lines.add("CLIENTE/OBRA: " + report.header().customerName());
        lines.add("");
        lines.add(String.format("%-10s %-42s %6s %12s %8s %8s %3s",
                "CODIGO", "SERVICO", "UN", "TOTAL", "%", "% ACUM", "ABC"));
        lines.add("-----------------------------------------------------------------------------------------------");
        abcCurveService.calculateServiceAbcCurve(budgetId).stream().limit(45).forEach(entry -> lines.add(String.format(
                "%-10s %-42s %6s %12s %8s %8s %3s",
                entry.serviceCode(),
                abbreviate(entry.description(), 42),
                entry.unit(),
                money(entry.cost()),
                percentValue(entry.percentage()),
                percentValue(entry.cumulativePercentage()),
                entry.classification())));
        return SimplePdf.write(lines);
    }

    private void appendStage(List<WorksheetReport.Line> lines, BudgetStage stage, int level) {
        lines.add(WorksheetReport.Line.stage(
                stage.getId(), level, stage.getSortOrder().toString(), stage.getName(), stageTotal(stage)));
        stage.getItems().forEach(item -> lines.add(WorksheetReport.Line.item(
                item.getId(), level + 1, item.getComposition().getSinapiCode(),
                item.getComposition().getDescription(), item.getComposition().getUnit(),
                item.getQuantity(), item.getUnitCost(), item.getDirectCost(), item.getBdiPct())));
        stage.getChildren().forEach(child -> appendStage(lines, child, level + 1));
    }

    private BigDecimal stageTotal(BudgetStage stage) {
        BigDecimal ownTotal = stage.getItems().stream()
                .map(BudgetItem::getDirectCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal childrenTotal = stage.getChildren().stream()
                .map(this::stageTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ownTotal.add(childrenTotal).setScale(2, RoundingMode.HALF_UP);
    }

    private String formatLine(WorksheetReport.Line line) {
        String indent = "  ".repeat(Math.max(0, line.level() - 1));
        String description = abbreviate(indent + line.description(), 52);
        if (line.kind() == WorksheetReport.LineKind.STAGE) {
            return String.format("%-10s %-52s %8s %12s %12s",
                    line.code(), description, "", "", money(line.total()));
        }
        return String.format("%-10s %-52s %8s %12s %12s",
                line.code(), description, number(line.quantity()), money(line.unitCost()), money(line.total()));
    }

    private String abbreviate(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }

    private String number(BigDecimal value) {
        return value == null ? "" : value.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String percent(BigDecimal value) {
        return value == null ? "0.00 %" : value.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP) + " %";
    }

    private String percentValue(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public record WorksheetReport(
            Header header,
            List<Line> lines,
            BigDecimal subtotal,
            BigDecimal socialChargesTotal,
            BigDecimal bdiPct,
            BigDecimal bdiTotal,
            BigDecimal administrationTotal,
            BigDecimal total) {
        public record Header(UUID id, String code, String title, String customerName,
                             LocalDate startDate, LocalDate endDate, String status, boolean active) {}

        public record Line(UUID id, LineKind kind, int level, String code, String description, String unit,
                           BigDecimal quantity, BigDecimal unitCost, BigDecimal total, BigDecimal bdiPct) {
            static Line stage(UUID id, int level, String code, String description, BigDecimal total) {
                return new Line(id, LineKind.STAGE, level, code, description, null, null, null, total, BigDecimal.ZERO);
            }

            static Line item(UUID id, int level, String code, String description, String unit,
                             BigDecimal quantity, BigDecimal unitCost, BigDecimal total, BigDecimal bdiPct) {
                return new Line(id, LineKind.ITEM, level, code, description, unit, quantity, unitCost, total, bdiPct);
            }
        }

        public enum LineKind {
            STAGE,
            ITEM
        }
    }

    public byte[] generateAnalyticalPdf(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        var items = itemRepository.findAllByBudgetId(budgetId);

        List<String> lines = new ArrayList<>();
        lines.add("RELATORIO ANALITICO DO ORCAMENTO");
        lines.add("ORCAMENTO: " + budget.getCode() + " - " + budget.getTitle());
        lines.add("CLIENTE/OBRA: " + budget.getCustomerName());
        lines.add("");

        for (var item : items) {
            var comp = item.getComposition();
            lines.add("---------------------------------------------------------------");
            lines.add(comp.getSinapiCode() + " - " + abbreviate(comp.getDescription(), 60));
            lines.add("Unidade: " + comp.getUnit() + " | Qtd: " + number(item.getQuantity())
                    + " | Custo Unit: " + money(item.getUnitCost())
                    + " | Total: " + money(item.getDirectCost()));
            lines.add("");
        }

        lines.add("---------------------------------------------------------------");
        lines.add("TOTAL DO ORCAMENTO: " + money(itemRepository.sumDirectCostByBudget(budgetId)));
        return SimplePdf.write(lines);
    }

    private static final class SimplePdf {
        private SimplePdf() {}

        static byte[] write(List<String> lines) {
            StringBuilder content = new StringBuilder("BT\n/F1 9 Tf\n40 800 Td\n12 TL\n");
            lines.stream().limit(58).forEach(line -> content
                    .append("(").append(escape(line)).append(") Tj\nT*\n"));
            content.append("ET\n");

            byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
            List<String> objects = List.of(
                    "<< /Type /Catalog /Pages 2 0 R >>",
                    "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                    "<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>",
                    "<< /Length " + contentBytes.length + " >>\nstream\n" + content + "endstream");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            write(out, "%PDF-1.4\n");
            List<Integer> offsets = new ArrayList<>();
            for (int i = 0; i < objects.size(); i++) {
                offsets.add(out.size());
                write(out, (i + 1) + " 0 obj\n" + objects.get(i) + "\nendobj\n");
            }
            int xref = out.size();
            write(out, "xref\n0 " + (objects.size() + 1) + "\n");
            write(out, "0000000000 65535 f \n");
            offsets.forEach(offset -> write(out, String.format("%010d 00000 n \n", offset)));
            write(out, "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
            return out.toByteArray();
        }

        private static String escape(String value) {
            return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
        }

        private static void write(ByteArrayOutputStream out, String value) {
            try {
                out.write(value.getBytes(StandardCharsets.ISO_8859_1));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
