package com.sinapipro.api.finance.application;

import module java.base;

import com.sinapipro.api.finance.application.BudgetVsActualService.*;
import org.springframework.stereotype.Service;

@Service
public class BudgetVsActualReportService {

    private final BudgetVsActualService budgetVsActualService;

    public BudgetVsActualReportService(BudgetVsActualService budgetVsActualService) {
        this.budgetVsActualService = budgetVsActualService;
    }

    public byte[] generatePdf(UUID budgetId) {
        var report = budgetVsActualService.consolidatedReport(budgetId);

        List<String> lines = new ArrayList<>();
        lines.add("RELATORIO ORCADO X REALIZADO");
        lines.add("");
        lines.add(String.format("%-8s %-30s %12s %12s %12s %12s %6s",
                "CODIGO", "DESCRICAO", "ORCADO", "COMPROM.", "REALIZADO", "SALDO", "%"));
        lines.add("------------------------------------------------------------------------------------------------------");

        for (var line : report.lines()) {
            lines.add(String.format("%-8s %-30s %12s %12s %12s %12s %6s",
                    line.code(),
                    abbreviate(line.name(), 30),
                    money(line.budgeted()),
                    money(line.committed()),
                    money(line.actual()),
                    money(line.variance()),
                    pct(line.pctExecuted())));
        }

        lines.add("------------------------------------------------------------------------------------------------------");
        var t = report.totals();
        lines.add(String.format("%-8s %-30s %12s %12s %12s %12s %6s",
                "", "TOTAL GERAL", money(t.budgeted()), money(t.committed()),
                money(t.actual()), money(t.variance()), pct(t.pctExecuted())));

        return SimplePdf.write(lines);
    }

    private String abbreviate(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String pct(BigDecimal value) {
        return value == null ? "0.0" : value.setScale(1, RoundingMode.HALF_UP).toPlainString();
    }

    private static final class SimplePdf {
        private SimplePdf() {}

        static byte[] write(List<String> lines) {
            StringBuilder content = new StringBuilder("BT\n/F1 8 Tf\n30 800 Td\n11 TL\n");
            lines.stream().limit(62).forEach(line -> content
                    .append("(").append(escape(line)).append(") Tj\nT*\n"));
            content.append("ET\n");

            byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
            List<String> objects = List.of(
                    "<< /Type /Catalog /Pages 2 0 R >>",
                    "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 842 595] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
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
            try { out.write(value.getBytes(StandardCharsets.ISO_8859_1)); }
            catch (IOException e) { throw new UncheckedIOException(e); }
        }
    }
}
