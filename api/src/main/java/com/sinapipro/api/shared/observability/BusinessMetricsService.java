package com.sinapipro.api.shared.observability;

import com.sinapipro.api.shared.events.OperationEventType;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetricsService {

    private final MeterRegistry meterRegistry;

    public BusinessMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void record(String domain, OperationEventType type) {
        meterRegistry.counter("sinapipro.business.operations", "domain", domain, "type", type.name().toLowerCase()).increment();
    }
}
