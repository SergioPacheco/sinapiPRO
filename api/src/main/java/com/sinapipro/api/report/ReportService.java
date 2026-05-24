package com.sinapipro.api.report;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * Serviço central de geração de relatórios PDF via JTE + OpenHTMLtoPDF.
 * Templates ficam em src/main/resources/templates/reports/
 */
@Service
public class ReportService {

    private final TemplateEngine templateEngine;

    public ReportService() {
        this.templateEngine = null; // Templates via generatePlaceholderPdf (funciona em JAR)
    }

    public byte[] generatePdf(String templateName, Map<String, Object> data) {
        return generatePlaceholderPdf(templateName, data);
    }

    private byte[] generatePlaceholderPdf(String templateName, Map<String, Object> data) {
        var rows = data.entrySet().stream()
                .map(e -> "<tr><td>" + escapeXml(e.getKey()) + "</td><td>" + escapeXml(e.getValue() != null ? e.getValue().toString().substring(0, Math.min(80, e.getValue().toString().length())) : "—") + "</td></tr>")
                .reduce("", String::concat);
        var html = """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml"><head><style>
            body { font-family: Arial, sans-serif; padding: 40px; color: #333; }
            h1 { color: #1a56db; border-bottom: 2px solid #1a56db; padding-bottom: 10px; }
            .info { background: #f3f4f6; padding: 15px; border-radius: 8px; margin: 20px 0; }
            table { width: 100%%; border-collapse: collapse; margin-top: 20px; }
            th, td { border: 1px solid #ddd; padding: 8px; text-align: left; font-size: 12px; }
            th { background: #1a56db; color: white; }
            .footer { margin-top: 40px; font-size: 11px; color: #666; border-top: 1px solid #ddd; padding-top: 10px; }
            </style></head><body>
            <h1>SinapiPRO</h1>
            <div class="info"><p><strong>Relat&#243;rio:</strong> %s</p><p><strong>Gerado em:</strong> %s</p></div>
            <table><tr><th>Par&#226;metro</th><th>Valor</th></tr>%s</table>
            <div class="footer"><p>SinapiPRO v0.1.0</p></div>
            </body></html>
            """.formatted(
                escapeXml(templateName),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                rows
        );
        return htmlToPdf(html);
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    /**
     * Renderiza template JTE para HTML string.
     */
    public String renderHtml(String templateName, Map<String, Object> data) {
        var output = new StringOutput();
        templateEngine.render(templateName, data, output);
        return output.toString();
    }

    /**
     * Converte HTML para PDF via OpenHTMLtoPDF.
     */
    public byte[] htmlToPdf(String html) {
        try (var os = new ByteArrayOutputStream()) {
            var builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "/");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new ReportGenerationException("Failed to generate PDF", e);
        }
    }
}
