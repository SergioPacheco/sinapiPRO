package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.ContactDepartment;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "client_contact")
public class ClientContact {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "client_id", nullable = false) private UUID clientId;
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 100) private String role;
    @Column(length = 200) private String email;
    @Column(length = 30) private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private ContactDepartment department;
    @Column(name = "is_primary", nullable = false) private boolean primary = false;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected ClientContact() {}
    public ClientContact(UUID clientId, String name, String role, String email, String phone, ContactDepartment department, boolean primary) {
        this.clientId = clientId; this.name = name; this.role = role; this.email = email; this.phone = phone; this.department = department; this.primary = primary;
    }

    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public ContactDepartment getDepartment() { return department; }
    public boolean isPrimary() { return primary; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String name, String role, String email, String phone, ContactDepartment department, boolean primary) {
        this.name = name; this.role = role; this.email = email; this.phone = phone; this.department = department; this.primary = primary; this.updatedAt = Instant.now();
    }
}
