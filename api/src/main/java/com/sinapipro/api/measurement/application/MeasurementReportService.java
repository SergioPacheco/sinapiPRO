package com.sinapipro.api.measurement.application;

import module java.base;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MeasurementReportService {

    private final MeasurementService measurementService;

    public MeasurementReportService(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    public byte[] generateBulletinPdf(UUID measurementId) {
        var detail = measurementService.detail(measurementId);
        List<String> lines = new ArrayList<>();
        lines.add("BOLETIM DE MEDICAO");
        lines.add("MEDICAO: " + detail.number() + "  PERIODO: " + detail.periodStart() + " a " + detail.periodEnd());
        lines.add("STATUS: " + detail.status());
        lines.add("");
        lines.add(String.format("%-38s %10s %10s %10s %10s %10s %12s",
                "ITEM", "CONTR.", "ANTER.", "ESTA", "ACUM.", "SALDO", "VALOR"));
        lines.add("--------------------------------------------------------------------------------------------------------------");
        detail.items().stream().limit(38).forEach(item -> lines.add(String.format(
                "%-38s %10s %10s %10s %10s %10s %12s",
                abbreviate(item.description(), 38),
                qty(item.contractedQuantity()),
                qty(item.previousQuantity()),
                qty(item.periodQuantity()),
                qty(item.cumulativeQuantity()),
                qty(item.balanceQuantity()),
                money(item.periodAmount()))));
        lines.add("--------------------------------------------------------------------------------------------------------------");
        lines.add("Valor Bruto da Medicao: " + money(detail.grossAmount()));
        lines.add("Retencao: " + percent(detail.retentionPct()));
        lines.add("Valor Liquido da Medicao: " + money(detail.netAmount()));
        return SimplePdf.write(lines);
    }

    private String abbreviate(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }

    private String qty(BigDecimal value) {
        return value == null ? "0.0000" : value.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String percent(BigDecimal value) {
        return value == null ? "0.00%" : value.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%";
    }

    private static final class SimplePdf {
        private SimplePdf() {}

        static byte[] write(List<String> lines) {
            StringBuilder content = new StringBuilder("BT\n/F1 8 Tf\n24 540 Td\n11 TL\n");
            lines.stream().limit(48).forEach(line -> content
                    .append("(").append(escape(line)).append(") Tj\nT*\n"));
            content.append("ET\n");

            byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
            List<String> objects = List.of(
                    "<< /Type /Catalog /Pages 2 0 R >>",
                    "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 864 576] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
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
