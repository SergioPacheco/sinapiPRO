package com.sinapipro.api.team.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "team")
public class Team extends AuditableEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> members = new ArrayList<>();

    public Team() {}

    public String getName() { return name; }
    public UUID getProjectId() { return projectId; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public List<TeamMember> getMembers() { return members; }

    public void setName(String name) { this.name = name; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public void setDescription(String description) { this.description = description; }
    public void setActive(boolean active) { this.active = active; }

    public void addMember(TeamMember member) {
        members.add(member);
        member.setTeam(this);
    }
}
