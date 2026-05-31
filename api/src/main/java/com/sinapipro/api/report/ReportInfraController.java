package com.sinapipro.api.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Report Infrastructure", description = "Sprint 24: Generic Excel export, async report jobs")
@RestController
@RequestMapping("/api/v1/reports")
@PreAuthorize("@perm.check('report.read')")
public class ReportInfraController {

    private final ExcelExportService excelService;
    private final AsyncReportService asyncService;

    public ReportInfraController(ExcelExportService excelService, AsyncReportService asyncService) {
        this.excelService = excelService;
        this.asyncService = asyncService;
    }

    // ═══════════════════════════════════════════════════════════
    // 24.7 — Endpoint genérico de exportação Excel
    // ═══════════════════════════════════════════════════════════

    record ExcelExportRequest(@NotBlank String sheetName, @NotEmpty List<String> headers,
                              @NotEmpty List<Map<String, Object>> rows) {}

    @Operation(summary = "Generic Excel export from any data set")
    @PostMapping("/export/excel")
    @PreAuthorize("@perm.check('report.read')")
    ResponseEntity<byte[]> exportExcel(@Valid @RequestBody ExcelExportRequest req) {
        var bytes = excelService.export(req.sheetName(), req.headers(), req.rows());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + req.sheetName() + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    // ═══════════════════════════════════════════════════════════
    // 24.8 — Fila assíncrona para relatórios pesados
    // ═══════════════════════════════════════════════════════════

    record AsyncJobResponse(UUID jobId, String status, String reportName, String error) {
        static AsyncJobResponse from(AsyncReportService.ReportJob job) {
            return new AsyncJobResponse(job.id(), job.status(), job.reportName(), job.error());
        }
    }

    @Operation(summary = "Check status of an async report job")
    @GetMapping("/jobs/{jobId}")
    ResponseEntity<AsyncJobResponse> getJobStatus(@PathVariable UUID jobId) {
        var job = asyncService.getStatus(jobId);
        if (job == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(AsyncJobResponse.from(job));
    }

    @Operation(summary = "Download completed async report")
    @GetMapping("/jobs/{jobId}/download")
    ResponseEntity<byte[]> downloadJob(@PathVariable UUID jobId) {
        var result = asyncService.getResult(jobId);
        if (result == null) return ResponseEntity.notFound().build();
        var job = asyncService.getStatus(jobId);
        asyncService.cleanup(jobId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + job.reportName() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(result);
    }
}
