package com.sinapipro.api.supplier.domain;

import com.sinapipro.api.shared.domain.EvaluationCriterion;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "supplier_evaluation")
public class SupplierEvaluation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "supplier_id", nullable = false) private UUID supplierId;
    @Column(name = "evaluation_date", nullable = false) private LocalDate evaluationDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private EvaluationCriterion criterion;
    @Column(nullable = false) private int score;
    @Column(length = 140) private String evaluator;
    @Column(length = 500) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();

    protected SupplierEvaluation() {}
    public SupplierEvaluation(UUID supplierId, LocalDate evaluationDate, EvaluationCriterion criterion, int score, String evaluator, String notes) {
        this.supplierId = supplierId; this.evaluationDate = evaluationDate; this.criterion = criterion; this.score = score; this.evaluator = evaluator; this.notes = notes;
    }

    public UUID getId() { return id; }
    public UUID getSupplierId() { return supplierId; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public EvaluationCriterion getCriterion() { return criterion; }
    public int getScore() { return score; }
    public String getEvaluator() { return evaluator; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
}
