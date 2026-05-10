package com.sinapipro.api.budget.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "budget_item_tag", uniqueConstraints = @UniqueConstraint(columnNames = {"budget_item_id", "tag"}))
public class BudgetItemTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "budget_item_id", nullable = false)
    private UUID budgetItemId;

    @Column(nullable = false, length = 50)
    private String tag;

    @Column(length = 7)
    private String color;

    public BudgetItemTag() {}

    public BudgetItemTag(UUID budgetItemId, String tag, String color) {
        this.budgetItemId = budgetItemId;
        this.tag = tag;
        this.color = color;
    }

    public UUID getId() { return id; }
    public UUID getBudgetItemId() { return budgetItemId; }
    public String getTag() { return tag; }
    public String getColor() { return color; }
}
