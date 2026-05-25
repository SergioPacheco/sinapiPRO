package com.sinapipro.api.measurement.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Events do aggregate Measurement.
 * Publicados via Spring ApplicationEventPublisher após persistência.
 */
public sealed interface MeasurementEvent {

    UUID measurementId();
    UUID budgetId();
    Instant occurredAt();

    record Submitted(UUID measurementId, UUID budgetId, int number, BigDecimal grossAmount, Instant occurredAt) implements MeasurementEvent {}
    record Approved(UUID measurementId, UUID budgetId, int number, BigDecimal netAmount, String approvedBy, Instant occurredAt) implements MeasurementEvent {}
    record Rejected(UUID measurementId, UUID budgetId, int number, String reason, Instant occurredAt) implements MeasurementEvent {}
    record Paid(UUID measurementId, UUID budgetId, int number, BigDecimal netAmount, Instant occurredAt) implements MeasurementEvent {}
}
