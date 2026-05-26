package com.sinapipro.api.report;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Sprint 24.3 — PDF via headless browser (Playwright local ou Gotenberg em prod).
 * Usado para relatórios com gráficos ECharts, layouts complexos.
 */
@Service
public class PlaywrightPdfService {

    private final String gotenbergUrl;
    private final RestClient restClient;

    public PlaywrightPdfService(@Value("${report.gotenberg.url:}") String gotenbergUrl) {
        this.gotenbergUrl = gotenbergUrl;
        this.restClient = RestClient.create();
    }

    /**
     * Gera PDF a partir de HTML via Gotenberg (prod) ou Playwright local (dev).
     */
    public byte[] htmlToPdf(String html) {
        if (gotenbergUrl != null && !gotenbergUrl.isBlank()) {
            return viaGotenberg(html);
        }
        return viaPlaywrightLocal(html);
    }

    /**
     * Gera PDF a partir de uma URL (ex: Angular route com ECharts).
     */
    public byte[] urlToPdf(String url) {
        if (gotenbergUrl != null && !gotenbergUrl.isBlank()) {
            return viaGotenbergUrl(url);
        }
        return viaPlaywrightLocalUrl(url);
    }

    private byte[] viaGotenberg(String html) {
        // Gotenberg API: POST /forms/chromium/convert/html
        return restClient.post()
                .uri(gotenbergUrl + "/forms/chromium/convert/html")
                .header("Content-Type", "multipart/form-data")
                .body(Map.of("files", html))
                .retrieve()
                .body(byte[].class);
    }

    private byte[] viaGotenbergUrl(String url) {
        return restClient.post()
                .uri(gotenbergUrl + "/forms/chromium/convert/url")
                .body(Map.of("url", url))
                .retrieve()
                .body(byte[].class);
    }

    private byte[] viaPlaywrightLocal(String html) {
        try (var playwright = com.microsoft.playwright.Playwright.create()) {
            var browser = playwright.chromium().launch();
            var page = browser.newPage();
            page.setContent(html);
            var pdf = page.pdf(new com.microsoft.playwright.Page.PdfOptions()
                    .setFormat("A4").setPrintBackground(true));
            browser.close();
            return pdf;
        }
    }

    private byte[] viaPlaywrightLocalUrl(String url) {
        try (var playwright = com.microsoft.playwright.Playwright.create()) {
            var browser = playwright.chromium().launch();
            var page = browser.newPage();
            page.navigate(url);
            page.waitForLoadState();
            var pdf = page.pdf(new com.microsoft.playwright.Page.PdfOptions()
                    .setFormat("A4").setPrintBackground(true));
            browser.close();
            return pdf;
        }
    }
}
