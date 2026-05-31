package com.sinapipro.api.sinapi.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface CompositionSearchRepository extends ElasticsearchRepository<CompositionSearchDocument, UUID> {

    @Query("""
        {"multi_match": {"query": "?0", "fields": ["description^3", "code^2", "category"], "fuzziness": "AUTO"}}
        """)
    Page<CompositionSearchDocument> search(String query, Pageable pageable);
}
