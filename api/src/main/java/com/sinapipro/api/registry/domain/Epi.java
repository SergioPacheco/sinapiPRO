package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "epi")
public class Epi extends TenantAwareEntity {
    @Column(nullable = false, length = 200) private String name;
    @Column(name = "ca_number", length = 30) private String caNumber;
    @Column(name = "validity_months") private Integer validityMonths;
    @Column(length = 500) private String description;
    protected Epi() {}
    public Epi(String name, String caNumber, Integer validityMonths, String description) {
        this.name = name; this.caNumber = caNumber; this.validityMonths = validityMonths; this.description = description;
    }
    public String getName() { return name; }
    public String getCaNumber() { return caNumber; }
    public Integer getValidityMonths() { return validityMonths; }
    public String getDescription() { return description; }
    public void update(String name, String caNumber, Integer validityMonths, String description) {
        this.name = name; this.caNumber = caNumber; this.validityMonths = validityMonths; this.description = description;
    }
}
