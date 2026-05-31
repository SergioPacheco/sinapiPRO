package com.sinapipro.api.config;

import com.sinapipro.api.weather.application.WeatherClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Registers @HttpExchange declarative clients (Spring Framework 7 pattern).
 * Uses RestClient (synchronous, Virtual Thread friendly) as transport.
 */
@Configuration
public class HttpClientConfiguration {

    @Bean
    WeatherClient weatherClient() {
        var restClient = RestClient.builder()
                .baseUrl("https://api.openweathermap.org/data/2.5")
                .build();
        var factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(WeatherClient.class);
    }
}
