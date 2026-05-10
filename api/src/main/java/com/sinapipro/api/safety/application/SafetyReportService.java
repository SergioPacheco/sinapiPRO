package com.sinapipro.api.safety.application;

import module java.base;

import com.sinapipro.api.safety.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SafetyReportService {

    private final SafetyInspectionRepository inspectionRepository;
    private final SafetyIncidentRepository incidentRepository;

    public SafetyReportService(SafetyInspectionRepository inspectionRepository, SafetyIncidentRepository incidentRepository) {
        this.inspectionRepository = inspectionRepository;
        this.incidentRepository = incidentRepository;
    }

    public byte[] generateSafetyReportPdf(UUID budgetId) {
        var inspections = inspectionRepository.findByBudgetIdOrderByInspectionDateDesc(budgetId);
        var incidents = incidentRepository.findByBudgetIdOrderByIncidentDateDesc(budgetId);

        List<String> lines = new ArrayList<>();
        lines.add("RELATORIO DE SEGURANCA DO TRABALHO");
        lines.add("");

        // Summary
        long passCount = inspections.stream().filter(i -> "PASS".equals(i.getStatus())).count();
        long failCount = inspections.stream().filter(i -> "FAIL".equals(i.getStatus())).count();
        long openIncidents = incidents.stream().filter(i -> "OPEN".equals(i.getStatus())).count();
        lines.add("Inspecoes realizadas: " + inspections.size());
        lines.add("  Aprovadas: " + passCount + "  Reprovadas: " + failCount);
        lines.add("Incidentes registrados: " + incidents.size());
        lines.add("  Em aberto: " + openIncidents + "  Resolvidos: " + (incidents.size() - openIncidents));
        lines.add("");

        // Inspections
        if (!inspections.isEmpty()) {
            lines.add("--- INSPECOES ---");
            lines.add(String.format("%-12s %-20s %-20s %8s", "DATA", "CHECKLIST", "INSPETOR", "STATUS"));
            lines.add("--------------------------------------------------------------");
            inspections.stream().limit(20).forEach(i -> lines.add(String.format("%-12s %-20s %-20s %8s",
                    i.getInspectionDate(), abbreviate(i.getTemplate().getName(), 20),
                    abbreviate(i.getInspector(), 20), i.getStatus())));
            lines.add("");
        }

        // Incidents
        if (!incidents.isEmpty()) {
            lines.add("--- INCIDENTES ---");
            lines.add(String.format("%-12s %-10s %-40s %8s", "DATA", "GRAVIDADE", "DESCRICAO", "STATUS"));
            lines.add("--------------------------------------------------------------");
            incidents.stream().limit(20).forEach(i -> lines.add(String.format("%-12s %-10s %-40s %8s",
                    i.getIncidentDate(), i.getSeverity(), abbreviate(i.getDescription(), 40), i.getStatus())));
        }

        return SimplePdf.write(lines);
    }

    private String abbreviate(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
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
            try { out.write(value.getBytes(StandardCharsets.ISO_8859_1)); }
            catch (IOException e) { throw new UncheckedIOException(e); }
        }
    }
}
