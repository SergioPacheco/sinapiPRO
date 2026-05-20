package com.sinapipro.api.dailylog.application;

import module java.base;

import com.sinapipro.api.dailylog.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DailyLogReportService {

    private final DailyLogRepository dailyLogRepository;

    public DailyLogReportService(DailyLogRepository dailyLogRepository) {
        this.dailyLogRepository = dailyLogRepository;
    }

    public byte[] generateRdoPdf(UUID dailyLogId) {
        var log = dailyLogRepository.findById(dailyLogId)
                .orElseThrow(() -> new DomainNotFoundException("Daily log not found: " + dailyLogId));

        List<String> lines = new ArrayList<>();
        lines.add("RELATORIO DIARIO DE OBRA - RDO");
        lines.add("");
        lines.add("Data: " + log.getLogDate());
        lines.add("Clima Manha: " + nvl(log.getWeatherMorning()));
        lines.add("Clima Tarde: " + nvl(log.getWeatherAfternoon()));
        lines.add("");

        // Labor
        lines.add("--- MAO DE OBRA ---");
        lines.add(String.format("%-30s %-20s %8s", "NOME", "FUNCAO", "HORAS"));
        lines.add("--------------------------------------------------------------");
        var totalHours = BigDecimal.ZERO;
        for (var l : log.getLaborEntries()) {
            lines.add(String.format("%-30s %-20s %8s",
                    abbreviate(l.getWorkerName(), 30), abbreviate(l.getRole(), 20), l.getHours().toPlainString()));
            totalHours = totalHours.add(l.getHours());
        }
        lines.add("Total: " + log.getLaborEntries().size() + " trabalhadores, " + totalHours.toPlainString() + " horas");
        lines.add("");

        // Equipment
        lines.add("--- EQUIPAMENTOS ---");
        lines.add(String.format("%-30s %10s %10s", "EQUIPAMENTO", "TRAB.", "PARADO"));
        lines.add("--------------------------------------------------------------");
        for (var e : log.getEquipmentEntries()) {
            lines.add(String.format("%-30s %10s %10s",
                    abbreviate(e.getEquipmentName(), 30), e.getHoursUsed().toPlainString(), e.getHoursIdle().toPlainString()));
        }
        lines.add("");

        // Occurrences
        if (!log.getOccurrences().isEmpty()) {
            lines.add("--- OCORRENCIAS ---");
            for (var o : log.getOccurrences()) {
                lines.add("[" + o.getType() + "] " + abbreviate(o.getDescription(), 70));
            }
            lines.add("");
        }

        // Photos
        if (!log.getPhotos().isEmpty()) {
            lines.add("--- FOTOS ANEXADAS ---");
            for (var p : log.getPhotos()) {
                lines.add("- " + p.getFilePath() + (p.getCaption() != null ? " (" + p.getCaption() + ")" : ""));
            }
            lines.add("");
        }

        // Observations
        if (log.getObservations() != null && !log.getObservations().isBlank()) {
            lines.add("--- OBSERVACOES ---");
            lines.add(abbreviate(log.getObservations(), 90));
        }

        return SimplePdf.write(lines);
    }

    public byte[] generatePhotoReportPdf(UUID dailyLogId) {
        var log = dailyLogRepository.findById(dailyLogId)
                .orElseThrow(() -> new DomainNotFoundException("Daily log not found: " + dailyLogId));

        List<String> lines = new ArrayList<>();
        lines.add("RELATORIO FOTOGRAFICO");
        lines.add("Obra: " + log.getBudget().getTitle());
        lines.add("Data: " + log.getLogDate());
        lines.add("");
        lines.add("--------------------------------------------------------------");

        int idx = 1;
        for (var photo : log.getPhotos()) {
            lines.add("Foto " + idx + ": " + photo.getFilePath());
            lines.add("Legenda: " + (photo.getCaption() != null ? photo.getCaption() : "Sem legenda"));
            lines.add("");
            idx++;
        }

        if (log.getPhotos().isEmpty()) {
            lines.add("Nenhuma foto registrada neste diario.");
        }

        lines.add("--------------------------------------------------------------");
        lines.add("Total de fotos: " + log.getPhotos().size());
        if (log.getSignedBy() != null) {
            lines.add("Assinado por: " + log.getSignedBy());
        }

        return SimplePdf.write(lines);
    }

    private String nvl(String value) { return value != null ? value : "-"; }

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
