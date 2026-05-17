package com.sinapipro.api.supplier.domain;

import module java.base;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "supplier")
public class Supplier extends TenantAwareEntity {

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

    @Column(name = "contact_name", length = 140)
    private String contactName;

    @Column(length = 200)
    private String website;

    @Column(nullable = false, length = 40)
    private String category;

    @Column(name = "qualification_status", nullable = false, length = 30)
    private String qualificationStatus;

    @Column(name = "payment_term_days", nullable = false)
    private Integer paymentTermDays;

    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays;

    @Column(length = 300)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private Boolean active;

    protected Supplier() {}

    public Supplier(String code, String name, String tradeName, String taxId,
                    String email, String phone, String contactName, String website,
                    String category, String qualificationStatus, Integer paymentTermDays,
                    Integer leadTimeDays, String address, String city, String state,
                    String postalCode, String notes, Integer rating, Boolean active) {
        this.code = code;
        this.name = name;
        this.tradeName = tradeName;
        this.taxId = taxId;
        this.email = email;
        this.phone = phone;
        this.contactName = contactName;
        this.website = website;
        this.category = category;
        this.qualificationStatus = qualificationStatus;
        this.paymentTermDays = paymentTermDays;
        this.leadTimeDays = leadTimeDays;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.notes = notes;
        this.rating = rating;
        this.active = active;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getTradeName() { return tradeName; }
    public String getTaxId() { return taxId; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getContactName() { return contactName; }
    public String getWebsite() { return website; }
    public String getCategory() { return category; }
    public String getQualificationStatus() { return qualificationStatus; }
    public Integer getPaymentTermDays() { return paymentTermDays; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getNotes() { return notes; }
    public Integer getRating() { return rating; }
    public Boolean getActive() { return active; }

    public void update(String name, String tradeName, String email, String phone,
                       String contactName, String website, String category,
                       String qualificationStatus, Integer paymentTermDays,
                       Integer leadTimeDays, String address, String city,
                       String state, String postalCode, String notes,
                       Integer rating, Boolean active) {
        this.name = name;
        this.tradeName = tradeName;
        this.email = email;
        this.phone = phone;
        this.contactName = contactName;
        this.website = website;
        this.category = category;
        this.qualificationStatus = qualificationStatus;
        this.paymentTermDays = paymentTermDays;
        this.leadTimeDays = leadTimeDays;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.notes = notes;
        this.rating = rating;
        this.active = active;
    }
}
