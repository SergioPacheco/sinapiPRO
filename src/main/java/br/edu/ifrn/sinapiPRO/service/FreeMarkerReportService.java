package br.edu.ifrn.sinapiPRO.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Serviço de relatórios FreeMarker + Flying Saucer.
 * Padrão adaptado do sgn3 (FreeMarkerUtils + PdfUtil).
 *
 * Fluxo: dados → FreeMarker template (.ftl) → HTML → JTidy (XHTML) → Flying Saucer → PDF bytes
 */
@Service
public class FreeMarkerReportService {

    private static final String TEMPLATE_PATH = "/templates/relatorio/ftl/";

    private final Configuration cfg;

    public FreeMarkerReportService() {
        cfg = new Configuration(Configuration.VERSION_2_3_30);
        cfg.setClassForTemplateLoading(getClass(), TEMPLATE_PATH);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    /**
     * Processa template FreeMarker e retorna HTML.
     */
    public String parseTemplate(String templateName, Map<String, Object> data) throws IOException, TemplateException {
        Template template = cfg.getTemplate(templateName);
        try (StringWriter writer = new StringWriter()) {
            template.process(data, writer);
            return writer.toString();
        }
    }

    /**
     * Converte HTML em PDF usando JTidy + Flying Saucer (padrão sgn3 PdfUtil).
     */
    public byte[] htmlToPdf(String html) {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(StandardCharsets.UTF_8.name());
        tidy.setOutputEncoding(StandardCharsets.UTF_8.name());
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);

        Document doc;
        try (var input = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8))) {
            doc = tidy.parseDOM(input, null);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao limpar HTML com JTidy", e);
        }
        doc.normalizeDocument();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(doc, null);
            renderer.layout();
            renderer.createPDF(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF com Flying Saucer", e);
        }
    }

    /**
     * Gera PDF a partir de template FreeMarker + dados.
     * Método principal — combina parseTemplate + htmlToPdf.
     */
    public byte[] gerarPdf(String templateName, Map<String, Object> data) {
        try {
            String html = parseTemplate(templateName, data);
            return htmlToPdf(html);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Erro ao gerar relatório: " + templateName, e);
        }
    }
}
