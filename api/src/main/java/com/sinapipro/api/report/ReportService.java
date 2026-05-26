package com.sinapipro.api.report;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.ResourceCodeResolver;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Sprint 24 — Serviço central de geração de relatórios PDF.
 * Factory pattern: JTE (tabular) ou Playwright (gráficos).
 */
@Service
public class ReportService {

    private final TemplateEngine templateEngine;

    public ReportService() {
        var resolver = new ResourceCodeResolver("templates/reports");
        this.templateEngine = TemplateEngine.create(resolver, ContentType.Html);
    }

    /**
     * Gera PDF via JTE template + OpenHTMLtoPDF (relatórios tabulares).
     */
    public byte[] generatePdf(String templateName, Map<String, Object> data) {
        var html = renderHtml(templateName, data);
        return htmlToPdf(html);
    }

    /**
     * Gera PDF com template base (header empresa + footer paginação).
     */
    public byte[] generateWithBaseTemplate(String title, String bodyHtml, Map<String, Object> meta) {
        var companyName = meta.getOrDefault("companyName", "SinapiPRO").toString();
        var generatedAt = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        var fullHtml = BASE_TEMPLATE
                .replace("{{TITLE}}", escapeXml(title))
                .replace("{{COMPANY}}", escapeXml(companyName))
                .replace("{{GENERATED_AT}}", generatedAt)
                .replace("{{BODY}}", bodyHtml);

        return htmlToPdf(fullHtml);
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

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Template base com header empresa e footer paginação (CSS paged media) */
    private static final String BASE_TEMPLATE = """
        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
        <html xmlns="http://www.w3.org/1999/xhtml"><head><style>
        @page { size: A4; margin: 20mm 15mm 25mm 15mm;
            @top-center { content: "{{COMPANY}}"; font-size: 9px; color: #666; }
            @bottom-left { content: "{{TITLE}}"; font-size: 8px; color: #999; }
            @bottom-right { content: "P\\00E1gina " counter(page) " de " counter(pages); font-size: 8px; color: #999; }
        }
        body { font-family: Arial, Helvetica, sans-serif; font-size: 11px; color: #333; line-height: 1.4; }
        h1 { color: #1a56db; font-size: 18px; border-bottom: 2px solid #1a56db; padding-bottom: 6px; margin-bottom: 12px; }
        h2 { color: #374151; font-size: 14px; margin-top: 16px; }
        table { width: 100%%; border-collapse: collapse; margin: 10px 0; }
        th { background: #1a56db; color: white; padding: 6px 8px; text-align: left; font-size: 10px; }
        td { border-bottom: 1px solid #e5e7eb; padding: 5px 8px; font-size: 10px; }
        tr:nth-child(even) td { background: #f9fafb; }
        .meta { background: #f3f4f6; padding: 10px; border-radius: 4px; margin-bottom: 12px; font-size: 10px; }
        .total { font-weight: bold; background: #eef2ff; }
        .right { text-align: right; }
        .footer-info { margin-top: 20px; font-size: 9px; color: #666; border-top: 1px solid #ddd; padding-top: 8px; }
        </style></head><body>
        <h1>{{TITLE}}</h1>
        <div class="meta"><strong>Gerado em:</strong> {{GENERATED_AT}} | <strong>Empresa:</strong> {{COMPANY}}</div>
        {{BODY}}
        <div class="footer-info">SinapiPRO - Sistema de Gest&#227;o de Obras</div>
        </body></html>
        """;
}
