package com.sinapipro.api.budget.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "budget_stage")
public class BudgetStage extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private BudgetStage parent;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder")
    private List<BudgetStage> children = new ArrayList<>();

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetItem> items = new ArrayList<>();

    protected BudgetStage() {}

    public BudgetStage(Budget budget, BudgetStage parent, String name, Integer sortOrder) {
        this.budget = budget;
        this.parent = parent;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public Budget getBudget() { return budget; }
    public BudgetStage getParent() { return parent; }
    public UUID getParentId() { return parent != null ? parent.getId() : null; }
    public String getName() { return name; }
    public Integer getSortOrder() { return sortOrder; }
    public List<BudgetStage> getChildren() { return children; }
    public List<BudgetItem> getItems() { return items; }

    public void update(String name, Integer sortOrder, BudgetStage parent) {
        this.name = name;
        this.sortOrder = sortOrder;
        this.parent = parent;
    }
}
