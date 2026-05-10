package com.sinapipro.api.budget.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "budget_item_memo")
public class BudgetItemMemo extends AuditableEntity {

    @Column(name = "budget_item_id", nullable = false)
    private UUID budgetItemId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<MemoLine> lines;

    @Column(precision = 14, scale = 4)
    private BigDecimal result;

    @Column(length = 500)
    private String notes;

    public BudgetItemMemo() {}

    public UUID getBudgetItemId() { return budgetItemId; }
    public List<MemoLine> getLines() { return lines; }
    public BigDecimal getResult() { return result; }
    public String getNotes() { return notes; }

    public void setBudgetItemId(UUID id) { this.budgetItemId = id; }
    public void setLines(List<MemoLine> lines) { this.lines = lines; }
    public void setResult(BigDecimal result) { this.result = result; }
    public void setNotes(String notes) { this.notes = notes; }

    public record MemoLine(String description, String formula, BigDecimal value) {}
}
