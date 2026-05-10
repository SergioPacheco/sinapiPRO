package com.sinapipro.api.team.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "team_member")
public class TeamMember extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 80)
    private String role;

    protected TeamMember() {}

    public TeamMember(UUID employeeId, String name, String role) {
        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
    }

    public Team getTeam() { return team; }
    public UUID getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getRole() { return role; }

    public void setTeam(Team team) { this.team = team; }
}
