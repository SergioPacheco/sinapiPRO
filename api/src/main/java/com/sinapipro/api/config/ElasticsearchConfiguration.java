package com.sinapipro.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch is optional. App starts without it (graceful degradation).
 * Set spring.elasticsearch.uris to enable full-text search on SINAPI catalog.
 */
@Configuration
@ConditionalOnProperty(name = "spring.elasticsearch.uris", matchIfMissing = false)
@EnableElasticsearchRepositories(basePackages = "com.sinapipro.api.sinapi.application")
public class ElasticsearchConfiguration {
}
