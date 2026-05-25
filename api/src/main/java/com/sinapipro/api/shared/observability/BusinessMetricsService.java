package com.sinapipro.api.shared.observability;

import com.sinapipro.api.shared.events.OperationEventType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Custom business metrics — expõe contadores de domínio para Prometheus/Grafana.
 *
 * Métricas registradas:
 * - sinapipro.measurement.status.total{status=SUBMITTED|APPROVED|REJECTED|PAID}
 * - sinapipro.budget.created.total
 * - sinapipro.procurement.order.total{status=CREATED|APPROVED}
 * - sinapipro.email.sent.total{result=SUCCESS|FAILED|CIRCUIT_OPEN}
 * - sinapipro.business.operations{domain,type} (genérico)
 */
@Component
public class BusinessMetricsService {

    private final MeterRegistry registry;

    private final Counter measurementSubmitted;
    private final Counter measurementApproved;
    private final Counter measurementRejected;
    private final Counter measurementPaid;
    private final Counter budgetCreated;
    private final Counter emailSuccess;
    private final Counter emailFailed;
    private final Counter emailCircuitOpen;

    public BusinessMetricsService(MeterRegistry registry) {
        this.registry = registry;
        this.measurementSubmitted = Counter.builder("sinapipro.measurement.status.total")
                .tag("status", "SUBMITTED").description("Measurements submitted for approval").register(registry);
        this.measurementApproved = Counter.builder("sinapipro.measurement.status.total")
                .tag("status", "APPROVED").description("Measurements approved").register(registry);
        this.measurementRejected = Counter.builder("sinapipro.measurement.status.total")
                .tag("status", "REJECTED").description("Measurements rejected").register(registry);
        this.measurementPaid = Counter.builder("sinapipro.measurement.status.total")
                .tag("status", "PAID").description("Measurements paid").register(registry);
        this.budgetCreated = Counter.builder("sinapipro.budget.created.total")
                .description("Budgets created").register(registry);
        this.emailSuccess = Counter.builder("sinapipro.email.sent.total")
                .tag("result", "SUCCESS").register(registry);
        this.emailFailed = Counter.builder("sinapipro.email.sent.total")
                .tag("result", "FAILED").register(registry);
        this.emailCircuitOpen = Counter.builder("sinapipro.email.sent.total")
                .tag("result", "CIRCUIT_OPEN").register(registry);
    }

    // === Domain-specific metrics ===

    public void measurementSubmitted() { measurementSubmitted.increment(); }
    public void measurementApproved() { measurementApproved.increment(); }
    public void measurementRejected() { measurementRejected.increment(); }
    public void measurementPaid() { measurementPaid.increment(); }
    public void budgetCreated() { budgetCreated.increment(); }
    public void emailSent() { emailSuccess.increment(); }
    public void emailFailed() { emailFailed.increment(); }
    public void emailCircuitOpen() { emailCircuitOpen.increment(); }

    // === Generic operation counter (backward compatible) ===

    public void record(String domain, OperationEventType type) {
        registry.counter("sinapipro.business.operations",
                "domain", domain,
                "type", type.name().toLowerCase())
                .increment();
    }
}
