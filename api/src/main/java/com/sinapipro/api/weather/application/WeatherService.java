package com.sinapipro.api.weather.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Integração com OpenWeatherMap API.
 * Busca previsão do tempo para o local da obra e identifica dias com risco de chuva.
 * Config: sinapipro.weather.api-key no application.yaml
 */
@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    private final RestClient restClient;
    private final String apiKey;

    public WeatherService(@Value("${sinapipro.weather.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder().baseUrl("https://api.openweathermap.org/data/2.5").build();
    }

    /** Busca previsão 5 dias para coordenadas da obra */
    public WeatherForecast getForecast(double lat, double lon) {
        if (apiKey.isBlank()) {
            log.warn("Weather API key not configured. Returning empty forecast.");
            return new WeatherForecast(List.of(), 0);
        }

        var response = restClient.get()
                .uri("/forecast?lat={lat}&lon={lon}&appid={key}&units=metric&lang=pt_br", lat, lon, apiKey)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("list")) {
            return new WeatherForecast(List.of(), 0);
        }

        @SuppressWarnings("unchecked")
        var list = (List<Map<String, Object>>) response.get("list");
        var days = list.stream().map(this::mapEntry).toList();
        int rainDays = (int) days.stream().filter(d -> d.rainMm > 0).count();

        return new WeatherForecast(days, rainDays);
    }

    /** Verifica se amanhã tem previsão de chuva forte (>10mm) */
    public boolean isRainExpectedTomorrow(double lat, double lon) {
        var forecast = getForecast(lat, lon);
        return forecast.days().stream()
                .filter(d -> d.date.equals(LocalDate.now().plusDays(1)))
                .anyMatch(d -> d.rainMm > 10);
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
    public record WeatherDay(LocalDate date, double tempCelsius, double humidity, String description, String icon, double rainMm) {}
}
