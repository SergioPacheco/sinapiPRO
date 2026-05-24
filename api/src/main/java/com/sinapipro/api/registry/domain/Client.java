package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "client")
public class Client extends TenantAwareEntity {
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 20) private String document;
    @Column(length = 200) private String email;
    @Column(length = 30) private String phone;
    @Column(length = 400) private String address;
    @Column(length = 100) private String city;
    @Column(length = 2) private String state;
    @Column(length = 500) private String notes;
    @Column(nullable = false) private boolean active = true;

    // V11 enrichment fields
    @Column(name = "trade_name", length = 200) private String tradeName;
    @Column(name = "person_type", nullable = false, length = 2) private String personType = "PJ";
    @Column(name = "cell_phone", length = 30) private String cellPhone;
    @Column(name = "home_phone", length = 30) private String homePhone;
    @Column(length = 30) private String whatsapp;
    @Column(length = 200) private String website;
    @Column(name = "address_number", length = 20) private String addressNumber;
    @Column(length = 100) private String neighborhood;
    @Column(name = "postal_code", length = 10) private String postalCode;
    @Column(name = "billing_address", length = 400) private String billingAddress;
    @Column(name = "billing_city", length = 100) private String billingCity;
    @Column(name = "billing_postal_code", length = 10) private String billingPostalCode;
    @Column(name = "work_address", length = 400) private String workAddress;
    @Column(name = "gross_income", precision = 18, scale = 2) private java.math.BigDecimal grossIncome;
    @Column(name = "spouse_income", precision = 18, scale = 2) private java.math.BigDecimal spouseIncome;
    @Column(name = "preferred_due_day") private Integer preferredDueDay;
    @Column(name = "billing_by_email", nullable = false) private boolean billingByEmail;

    protected Client() {}
    public Client(String name, String document, String email, String phone, String address, String city, String state, String notes) {
        this.name = name; this.document = document; this.email = email; this.phone = phone;
        this.address = address; this.city = city; this.state = state; this.notes = notes;
    }

    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public String getTradeName() { return tradeName; }
    public String getPersonType() { return personType; }
    public String getCellPhone() { return cellPhone; }
    public String getHomePhone() { return homePhone; }
    public String getWhatsapp() { return whatsapp; }
    public String getWebsite() { return website; }
    public String getAddressNumber() { return addressNumber; }
    public String getNeighborhood() { return neighborhood; }
    public String getPostalCode() { return postalCode; }
    public String getBillingAddress() { return billingAddress; }
    public String getBillingCity() { return billingCity; }
    public String getBillingPostalCode() { return billingPostalCode; }
    public String getWorkAddress() { return workAddress; }
    public java.math.BigDecimal getGrossIncome() { return grossIncome; }
    public java.math.BigDecimal getSpouseIncome() { return spouseIncome; }
    public Integer getPreferredDueDay() { return preferredDueDay; }
    public boolean isBillingByEmail() { return billingByEmail; }
    public void deactivate() { this.active = false; }
}
