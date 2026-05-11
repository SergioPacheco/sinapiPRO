package com.sinapipro.api.tenant.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tenant")
public class Tenant extends AuditableEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 20)
    private String document;

    @Column(length = 200)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false, length = 20)
    private String plan = "FREE";

    @Column(name = "plan_expires_at")
    private LocalDate planExpiresAt;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "max_users", nullable = false)
    private int maxUsers = 5;

    @Column(name = "max_projects", nullable = false)
    private int maxProjects = 3;

    public Tenant() {}

    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPlan() { return plan; }
    public LocalDate getPlanExpiresAt() { return planExpiresAt; }
    public boolean isActive() { return active; }
    public int getMaxUsers() { return maxUsers; }
    public int getMaxProjects() { return maxProjects; }

    public void setName(String name) { this.name = name; }
    public void setDocument(String document) { this.document = document; }
    public void setEmail(String email) { this.email = email; }
    public void setPlan(String plan) { this.plan = plan; }
    public void setActive(boolean active) { this.active = active; }
    public void setMaxUsers(int max) { this.maxUsers = max; }
    public void setMaxProjects(int max) { this.maxProjects = max; }
}
