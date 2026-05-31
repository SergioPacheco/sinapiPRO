package com.sinapipro.api.weather.application;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Weather integration using @HttpExchange declarative client (Spring 7).
 * Demonstrates: declarative HTTP, @Cacheable, @Observed, records.
 */
@Service
@Observed(name = "weather.service")
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    private final WeatherClient weatherClient;
    private final String apiKey;

    public WeatherService(WeatherClient weatherClient,
                          @Value("${sinapipro.weather.api-key:}") String apiKey) {
        this.weatherClient = weatherClient;
        this.apiKey = apiKey;
    }

    @Cacheable(value = "weather", key = "#lat + ',' + #lon")
    public WeatherForecast getForecast(double lat, double lon) {
        if (apiKey.isBlank()) {
            log.warn("Weather API key not configured. Returning empty forecast.");
            return new WeatherForecast(List.of(), 0);
        }

        var response = weatherClient.getForecast(lat, lon, apiKey, "metric", "pt_br");

        if (response == null || !response.containsKey("list")) {
            return new WeatherForecast(List.of(), 0);
        }

        @SuppressWarnings("unchecked")
        var list = (List<Map<String, Object>>) response.get("list");
        var days = list.stream().map(this::mapEntry).toList();
        int rainDays = (int) days.stream().filter(d -> d.rainMm() > 0).count();

        return new WeatherForecast(days, rainDays);
    }

    public boolean isRainExpectedTomorrow(double lat, double lon) {
        var forecast = getForecast(lat, lon);
        return forecast.days().stream()
                .filter(d -> d.date().equals(LocalDate.now().plusDays(1)))
                .anyMatch(d -> d.rainMm() > 10);
    }

    @SuppressWarnings("unchecked")
    private WeatherDay mapEntry(Map<String, Object> entry) {
        var main = (Map<String, Object>) entry.get("main");
        var weather = ((List<Map<String, Object>>) entry.get("weather")).getFirst();
        var rain = (Map<String, Object>) entry.getOrDefault("rain", Map.of());

        double temp = ((Number) main.get("temp")).doubleValue();
        double humidity = ((Number) main.get("humidity")).doubleValue();
        String description = (String) weather.get("description");
        String icon = (String) weather.get("icon");
        double rainMm = rain.containsKey("3h") ? ((Number) rain.get("3h")).doubleValue() : 0;
        String dtTxt = (String) entry.get("dt_txt");
        LocalDate date = LocalDate.parse(dtTxt.substring(0, 10));

        return new WeatherDay(date, temp, humidity, description, icon, rainMm);
    }

    public record WeatherForecast(List<WeatherDay> days, int rainDayCount) {}
    public record WeatherDay(LocalDate date, double tempCelsius, double humidity,
                             String description, String icon, double rainMm) {}
}
