package com.sinapipro.api.security.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// ═══════════════════════════════════════════════════════════
// User entity (local user linked to JWT sub)
// ═══════════════════════════════════════════════════════════

@Entity @Table(name = "app_user")
public class AppUser extends TenantAwareEntity {
    @Column(nullable = false, unique = true, length = 200) private String email;
    @Column(nullable = false, length = 200) private String name;
    @Column(name = "external_id", unique = true, length = 100) private String externalId; // JWT sub
    @Column(nullable = false) private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new java.util.HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_project_access", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "project_id")
    private Set<UUID> projectAccess = new java.util.HashSet<>(); // empty = all projects

    protected AppUser() {}
    public AppUser(String email, String name, String externalId) {
        this.email = email; this.name = name; this.externalId = externalId;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public boolean isActive() { return active; }
    public Set<Role> getRoles() { return roles; }
    public Set<UUID> getProjectAccess() { return projectAccess; }

    public void setName(String name) { this.name = name; }
    public void deactivate() { this.active = false; }
    public void activate() { this.active = true; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public void grantProjectAccess(UUID projectId) { this.projectAccess.add(projectId); }
    public void revokeProjectAccess(UUID projectId) { this.projectAccess.remove(projectId); }
    public boolean hasAccessToProject(UUID projectId) { return projectAccess.isEmpty() || projectAccess.contains(projectId); }

    public Set<String> getAllPermissions() {
        var perms = new java.util.HashSet<String>();
        for (var role : roles) perms.addAll(role.getPermissions());
        return perms;
    }
}
