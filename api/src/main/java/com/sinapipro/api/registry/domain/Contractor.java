package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "contractor")
public class Contractor extends TenantAwareEntity {
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 20) private String document;
    @Column(length = 100) private String specialty;
    @Column(length = 30) private String phone;
    @Column(length = 200) private String email;
    @Column(length = 100) private String city;
    @Column(length = 2) private String state;
    @Column(nullable = false) private boolean active = true;
    protected Contractor() {}
    public Contractor(String name, String document, String specialty, String phone, String email, String city, String state) {
        this.name = name; this.document = document; this.specialty = specialty;
        this.phone = phone; this.email = email; this.city = city; this.state = state;
    }
    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getSpecialty() { return specialty; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
    public void update(String name, String document, String specialty, String phone, String email, String city, String state) {
        this.name = name; this.document = document; this.specialty = specialty;
        this.phone = phone; this.email = email; this.city = city; this.state = state;
    }
}
