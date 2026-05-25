package com.sinapipro.api.measurement.application;

import com.sinapipro.api.measurement.domain.MeasurementEvent;
import com.sinapipro.api.shared.observability.BusinessMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Reacts to Measurement domain events — decoupled from the aggregate.
 *
 * Responsibilities:
 * - Record business metrics (Prometheus counters)
 * - Log audit trail
 * - Future: create notifications, trigger webhooks
 */
@Component
public class MeasurementEventListener {

    private static final Logger log = LoggerFactory.getLogger(MeasurementEventListener.class);

    private final BusinessMetricsService metrics;

    public MeasurementEventListener(BusinessMetricsService metrics) {
        this.metrics = metrics;
    }

    @EventListener
    public void on(MeasurementEvent.Submitted event) {
        metrics.measurementSubmitted();
        log.info("Measurement #{} submitted (budget={}, gross={})", event.number(), event.budgetId(), event.grossAmount());
    }

    @EventListener
    public void on(MeasurementEvent.Approved event) {
        metrics.measurementApproved();
        log.info("Measurement #{} approved by {} (budget={}, net={})", event.number(), event.approvedBy(), event.budgetId(), event.netAmount());
    }

    @EventListener
    public void on(MeasurementEvent.Rejected event) {
        metrics.measurementRejected();
        log.warn("Measurement #{} rejected (budget={}, reason={})", event.number(), event.budgetId(), event.reason());
    }

    @EventListener
    public void on(MeasurementEvent.Paid event) {
        metrics.measurementPaid();
        log.info("Measurement #{} paid (budget={}, net={})", event.number(), event.budgetId(), event.netAmount());
    }
}
