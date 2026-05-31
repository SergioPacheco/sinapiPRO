package com.sinapipro.api.sinapi.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "composition")
public class Composition extends TenantAwareEntity {

    @Column(name = "sinapi_code", nullable = false, unique = true, length = 20)
    private String sinapiCode;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "group_name", length = 140)
    private String groupName;

    @Column(nullable = false, length = 20)
    private String origin = "SINAPI";

    @Column(nullable = false)
    private Integer version = 1;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = true;

    @Column(name = "unit_cost", precision = 18, scale = 4)
    private java.math.BigDecimal unitCost;

    @Column(name = "reference_date")
    private java.time.LocalDate referenceDate;

    @OneToMany(mappedBy = "composition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompositionItem> items = new ArrayList<>();

    protected Composition() {}

    public Composition(String sinapiCode, String description, String unit, String groupName) {
        this.sinapiCode = sinapiCode;
        this.description = description;
        this.unit = unit;
        this.groupName = groupName;
    }

    public Composition(String code, String description, String unit, String groupName, String origin) {
        this.sinapiCode = code;
        this.description = description;
        this.unit = unit;
        this.groupName = groupName;
        this.origin = origin;
    }

    public String getSinapiCode() { return sinapiCode; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public String getGroupName() { return groupName; }
    public String getOrigin() { return origin; }
    public List<CompositionItem> getItems() { return items; }

    public void update(String description, String unit, String groupName) {
        this.description = description;
        this.unit = unit;
        this.groupName = groupName;
    }

    public void addItem(Material material, BigDecimal coefficient) {
        items.add(new CompositionItem(this, material, coefficient));
    }

    public void addItem(Material material, BigDecimal coefficient, ItemType itemType) {
        items.add(new CompositionItem(this, material, coefficient, itemType));
    }

    public void addCompositionItem(Composition childComposition, BigDecimal coefficient) {
        items.add(new CompositionItem(this, childComposition, coefficient));
    }

    public Composition createNewVersion(String description, String unit, String groupName) {
        var next = new Composition(this.sinapiCode, description, unit, groupName, this.origin);
        next.version = this.version + 1;
        next.parentId = this.parentId != null ? this.parentId : this.getId();
        next.isCurrent = true;
        return next;
    }

    public void markSuperseded() {
        this.isCurrent = false;
    }

    public UUID getChainRoot() {
        return parentId != null ? parentId : getId();
    }

    public Integer getVersion() { return version; }
    public UUID getParentId() { return parentId; }
    public Boolean getIsCurrent() { return isCurrent; }
    public java.math.BigDecimal getUnitCost() { return unitCost; }
    public java.time.LocalDate getReferenceDate() { return referenceDate; }

    public boolean isEditable() { return "PROPRIO".equals(origin); }
}
