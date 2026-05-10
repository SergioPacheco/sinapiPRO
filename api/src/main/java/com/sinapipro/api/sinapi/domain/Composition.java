package com.sinapipro.api.sinapi.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "composition")
public class Composition extends AuditableEntity {

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

    public void addItem(Material material, java.math.BigDecimal coefficient) {
        items.add(new CompositionItem(this, material, coefficient));
    }

    public boolean isEditable() { return "PROPRIO".equals(origin); }
}
