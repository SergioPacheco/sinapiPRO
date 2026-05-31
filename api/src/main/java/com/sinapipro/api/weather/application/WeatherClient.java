package com.sinapipro.api.weather.application;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

/**
 * Declarative HTTP client using @HttpExchange (Spring Framework 7).
 *
 * This replaces manual RestClient/WebClient calls with a type-safe interface.
 * Spring generates the implementation at runtime via HttpServiceProxyFactory.
 *
 * Benefits over RestTemplate/WebClient:
 * - Type-safe, no string URL building
 * - Testable (mock the interface)
 * - Automatic retry/circuit-breaker integration via Resilience4j
 * - Works with Virtual Threads (non-blocking under the hood)
 */
@HttpExchange(url = "https://api.openweathermap.org/data/2.5")
public interface WeatherClient {

    @GetExchange("/forecast")
    Map<String, Object> getForecast(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam("appid") String apiKey,
            @RequestParam("units") String units,
            @RequestParam("lang") String lang
    );

    @GetExchange("/weather")
    Map<String, Object> getCurrentWeather(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam("appid") String apiKey,
            @RequestParam("units") String units
    );
}
