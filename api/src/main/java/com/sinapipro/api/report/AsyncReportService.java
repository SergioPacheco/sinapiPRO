package com.sinapipro.api.report;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Sprint 24.8 — Fila assíncrona para relatórios pesados (>30s).
 * Usa Virtual Threads para geração paralela.
 * Em produção: substituir por mensageria (SQS/RabbitMQ) + storage (S3).
 */
@Service
public class AsyncReportService {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<UUID, ReportJob> jobs = new ConcurrentHashMap<>();

    /**
     * Submete geração de relatório para execução assíncrona.
     * Retorna jobId para polling.
     */
    public UUID submit(String reportName, Supplier<byte[]> generator) {
        var jobId = UUID.randomUUID();
        jobs.put(jobId, new ReportJob(jobId, reportName, "QUEUED", null, null, Instant.now()));

        executor.submit(() -> {
            jobs.put(jobId, new ReportJob(jobId, reportName, "PROCESSING", null, null, Instant.now()));
            try {
                var result = generator.get();
                jobs.put(jobId, new ReportJob(jobId, reportName, "COMPLETED", result, null, Instant.now()));
            } catch (Exception e) {
                jobs.put(jobId, new ReportJob(jobId, reportName, "FAILED", null, e.getMessage(), Instant.now()));
            }
        });

        return jobId;
    }

    /**
     * Consulta status de um job.
     */
    public ReportJob getStatus(UUID jobId) {
        return jobs.get(jobId);
    }

    /**
     * Recupera resultado (PDF bytes) de um job completo.
     */
    public byte[] getResult(UUID jobId) {
        var job = jobs.get(jobId);
        if (job == null || !"COMPLETED".equals(job.status())) return null;
        return job.result();
    }

    /**
     * Remove job do cache (após download).
     */
    public void cleanup(UUID jobId) {
        jobs.remove(jobId);
    }

    public record ReportJob(UUID id, String reportName, String status, byte[] result, String error, Instant updatedAt) {}
}
