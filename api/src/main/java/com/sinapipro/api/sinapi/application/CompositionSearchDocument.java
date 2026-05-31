package com.sinapipro.api.sinapi.application;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Elasticsearch document for full-text search on SINAPI compositions.
 * Inspired by JHipster's Elasticsearch integration pattern.
 */
@Document(indexName = "compositions")
public class CompositionSearchDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Text, analyzer = "brazilian")
    private String code;

    @Field(type = FieldType.Text, analyzer = "brazilian")
    private String description;

    @Field(type = FieldType.Keyword)
    private String unit;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Double)
    private BigDecimal unitCost;

    @Field(type = FieldType.Keyword)
    private String referenceMonth;

    public CompositionSearchDocument() {}

    public CompositionSearchDocument(UUID id, String code, String description, String unit,
                                     String category, BigDecimal unitCost, String referenceMonth) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.unit = unit;
        this.category = category;
        this.unitCost = unitCost;
        this.referenceMonth = referenceMonth;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public String getCategory() { return category; }
    public BigDecimal getUnitCost() { return unitCost; }
    public String getReferenceMonth() { return referenceMonth; }
}
