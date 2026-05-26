package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sales_representative")
public class SalesRepresentative extends TenantAwareEntity {
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 20) private String document;
    @Column(length = 30) private String phone;
    @Column(name = "cell_phone", length = 30) private String cellPhone;
    @Column(length = 30) private String whatsapp;
    @Column(length = 200) private String email;
    @Column(name = "commission_rate", precision = 5, scale = 2) private BigDecimal commissionRate;
    @Column(length = 200) private String region;
    @Column(length = 500) private String notes;
    @Column(nullable = false) private boolean active = true;

    protected SalesRepresentative() {}

    public SalesRepresentative(String name, String document, String phone, String cellPhone,
                               String whatsapp, String email, BigDecimal commissionRate, String region, String notes) {
        this.name = name; this.document = document; this.phone = phone; this.cellPhone = cellPhone;
        this.whatsapp = whatsapp; this.email = email; this.commissionRate = commissionRate;
        this.region = region; this.notes = notes;
    }

    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getPhone() { return phone; }
    public String getCellPhone() { return cellPhone; }
    public String getWhatsapp() { return whatsapp; }
    public String getEmail() { return email; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public String getRegion() { return region; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void deactivate() { this.active = false; }

    public void update(String name, String document, String phone, String cellPhone,
                       String whatsapp, String email, BigDecimal commissionRate, String region, String notes) {
        this.name = name; this.document = document; this.phone = phone; this.cellPhone = cellPhone;
        this.whatsapp = whatsapp; this.email = email; this.commissionRate = commissionRate;
        this.region = region; this.notes = notes;
    }
}
