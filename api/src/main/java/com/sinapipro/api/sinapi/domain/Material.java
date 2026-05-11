package com.sinapipro.api.sinapi.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "material")
public class Material extends TenantAwareEntity {

    @Column(name = "sinapi_code", nullable = false, unique = true, length = 20)
    private String sinapiCode;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(nullable = false, length = 30)
    private String origin;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaterialPrice> prices = new ArrayList<>();

    protected Material() {}

    public Material(String sinapiCode, String description, String unit, String origin) {
        this.sinapiCode = sinapiCode;
        this.description = description;
        this.unit = unit;
        this.origin = origin;
    }

    public String getSinapiCode() { return sinapiCode; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public String getOrigin() { return origin; }
    public List<MaterialPrice> getPrices() { return prices; }

    public void update(String description, String unit) {
        this.description = description;
        this.unit = unit;
    }

    public boolean isEditable() { return "PROPRIO".equals(origin); }
}
