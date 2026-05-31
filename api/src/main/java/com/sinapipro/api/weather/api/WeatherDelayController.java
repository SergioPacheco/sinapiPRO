package com.sinapipro.api.weather.api;

import com.sinapipro.api.weather.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Weather Delays", description = "Weather delay tracking and schedule impact")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/weather-delays")
public class WeatherDelayController {

    private final WeatherDelayRepository repository;
    private final com.sinapipro.api.weather.application.WeatherService weatherService;

    public WeatherDelayController(WeatherDelayRepository repository, com.sinapipro.api.weather.application.WeatherService weatherService) {
        this.repository = repository;
        this.weatherService = weatherService;
    }

    @Operation(summary = "List weather delays") @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    List<WeatherDelayResponse> list(@PathVariable UUID projectId) {
        return repository.findByBudgetIdOrderByDelayDateDesc(projectId).stream().map(WeatherDelayResponse::from).toList();
    }

    @Operation(summary = "Record a weather delay") @PostMapping
    @PreAuthorize("@perm.check('budget.write')") @ResponseStatus(HttpStatus.CREATED)
    WeatherDelayResponse record(@PathVariable UUID projectId, @Valid @RequestBody CreateWeatherDelayRequest req) {
        WeatherDelay wd = repository.save(new WeatherDelay(projectId, req.delayDate(), req.weatherCondition(),
                req.hoursLost(), req.fullDayLost() != null && req.fullDayLost(), req.impactDescription(), req.reportedBy()));
        return WeatherDelayResponse.from(wd);
    }

    @Operation(summary = "Weather delay impact summary") @GetMapping("/summary")
    @PreAuthorize("@perm.check('budget.read')")
    WeatherDelaySummary summary(@PathVariable UUID projectId) {
        return new WeatherDelaySummary(
                repository.findByBudgetIdOrderByDelayDateDesc(projectId).size(),
                repository.countFullDaysLost(projectId),
                repository.sumHoursLostByBudget(projectId));
    }

    record CreateWeatherDelayRequest(@NotNull LocalDate delayDate, @NotBlank String weatherCondition,
                                     @NotNull BigDecimal hoursLost, Boolean fullDayLost,
                                     String impactDescription, String reportedBy) {}
    record WeatherDelayResponse(UUID id, LocalDate delayDate, String weatherCondition,
                                BigDecimal hoursLost, Boolean fullDayLost, String impactDescription) {
        static WeatherDelayResponse from(WeatherDelay w) {
            return new WeatherDelayResponse(w.getId(), w.getDelayDate(), w.getWeatherCondition(),
                    w.getHoursLost(), w.getFullDayLost(), w.getImpactDescription());
        }
    }
    record WeatherDelaySummary(int totalDelays, long fullDaysLost, BigDecimal totalHoursLost) {}
}
