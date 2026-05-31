package com.sinapipro.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Cache configuration inspired by JHipster's Caffeine/EhCache setup.
 * Exposes cache metrics to Micrometer/Prometheus automatically.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        var manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(Duration.ofMinutes(10))
                .recordStats());
        manager.setCacheNames(java.util.List.of(
                "compositions", "materials", "budgetItems",
                "projects", "suppliers", "users"
        ));
        return manager;
    }
}
