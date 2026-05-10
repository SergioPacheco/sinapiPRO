package com.sinapipro.api.schedule.application;

import module java.base;

import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ScheduleReportService {

    private final ScheduleActivityRepository activityRepository;
    private final SCurveService sCurveService;

    public ScheduleReportService(ScheduleActivityRepository activityRepository, SCurveService sCurveService) {
        this.activityRepository = activityRepository;
        this.sCurveService = sCurveService;
    }

    public byte[] generatePhysicalFinancialPdf(UUID budgetId) {
        var activities = activityRepository.findByBudgetIdOrderBySortOrder(budgetId);
        var curve = sCurveService.calculate(budgetId);
        List<String> lines = new ArrayList<>();
        lines.add("CRONOGRAMA FISICO-FINANCEIRO");
        lines.add("");
        lines.add(String.format("%-4s %-42s %-10s %-10s %10s %10s",
                "ORD", "ATIVIDADE", "INICIO", "FIM", "PESO", "REAL"));
        lines.add("-----------------------------------------------------------------------------------------------");
        activities.forEach(activity -> lines.add(String.format(
                "%-4s %-42s %-10s %-10s %10s %10s",
                activity.getSortOrder(),
                abbreviate(activity.getName(), 42),
                activity.getPlannedStart(),
                activity.getPlannedEnd(),
                percent(activity.getWeight()),
                percent(activity.getProgressPct().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)))));
        lines.add("");
        lines.add("CURVA S");
        lines.add(String.format("%-10s %14s %14s", "PERIODO", "PLANEJADO", "REALIZADO"));
        curve.points().forEach(point -> lines.add(String.format("%-10s %14s %14s",
                point.period(),
                percent(point.plannedCumulative()),
                percent(point.actualCumulative()))));
        return SimplePdf.write(lines);
    }

    private String abbreviate(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }

    private String percent(BigDecimal value) {
        return value == null ? "0.00%" : value.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%";
    }

    private static final class SimplePdf {
        private SimplePdf() {}

        static byte[] write(List<String> lines) {
            StringBuilder content = new StringBuilder("BT\n/F1 9 Tf\n36 540 Td\n12 TL\n");
            lines.stream().limit(40).forEach(line -> content
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
            try {
                out.write(value.getBytes(StandardCharsets.ISO_8859_1));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
