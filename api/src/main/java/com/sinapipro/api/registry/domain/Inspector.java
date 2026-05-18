package com.sinapipro.api.registry.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "inspector")
public class Inspector {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 20) private String document;
    @Column(length = 100) private String role;
    @Column(length = 200) private String organization;
    @Column(length = 30) private String phone;
    @Column(length = 200) private String email;
    @Column(nullable = false) private boolean active = true;
    protected Inspector() {}
    public Inspector(String name, String document, String role, String organization, String phone, String email) {
        this.name = name; this.document = document; this.role = role;
        this.organization = organization; this.phone = phone; this.email = email;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getRole() { return role; }
    public String getOrganization() { return organization; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
    public void update(String name, String document, String role, String organization, String phone, String email) {
        this.name = name; this.document = document; this.role = role;
        this.organization = organization; this.phone = phone; this.email = email;
    }
}
