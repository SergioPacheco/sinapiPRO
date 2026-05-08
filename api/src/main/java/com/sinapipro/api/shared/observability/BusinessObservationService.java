package com.sinapipro.api.shared.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class BusinessObservationService {

    private final ObservationRegistry observationRegistry;

    public BusinessObservationService(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public <T> T observe(String name, String domain, Supplier<T> action) {
        Observation observation = Observation.start(name, observationRegistry)
                .lowCardinalityKeyValue("domain", domain);
        try (Observation.Scope ignored = observation.openScope()) {
            return action.get();
        } catch (RuntimeException exception) {
            observation.error(exception);
            throw exception;
        } finally {
            observation.stop();
        }
    }
}
