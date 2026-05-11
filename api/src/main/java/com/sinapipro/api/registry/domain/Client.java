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
    public void deactivate() { this.active = false; }
}
