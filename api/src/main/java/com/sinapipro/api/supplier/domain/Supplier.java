package com.sinapipro.api.supplier.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "supplier")
public class Supplier extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(name = "trade_name", length = 140)
    private String tradeName;

    @Column(name = "tax_id", nullable = false, unique = true, length = 30)
    private String taxId;

    @Column(length = 140)
    private String email;

    @Column(length = 40)
    private String phone;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private Boolean active;

    protected Supplier() {}

    public Supplier(String code, String name, String tradeName, String taxId,
                    String email, String phone, Integer rating, Boolean active) {
        this.code = code;
        this.name = name;
        this.tradeName = tradeName;
        this.taxId = taxId;
        this.email = email;
        this.phone = phone;
        this.rating = rating;
        this.active = active;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getTradeName() { return tradeName; }
    public String getTaxId() { return taxId; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Integer getRating() { return rating; }
    public Boolean getActive() { return active; }

    public void update(String name, String tradeName, String email, String phone, Integer rating, Boolean active) {
        this.name = name;
        this.tradeName = tradeName;
        this.email = email;
        this.phone = phone;
        this.rating = rating;
        this.active = active;
    }
}
