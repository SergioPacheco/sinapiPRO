package com.sinapipro.api.commercial.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "proposal")
public class Proposal extends AuditableEntity {

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "client_name", nullable = false, length = 200)
    private String clientName;

    @Column(columnDefinition = "text")
    private String scope;

    @Column(name = "total_value", precision = 18, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "validity_days")
    private Integer validityDays;

    @Column(name = "proposal_date", nullable = false)
    private LocalDate proposalDate;

    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    @Column(length = 500)
    private String conditions;

    @Column(length = 500)
    private String notes;

    public Proposal() {}

    public UUID getClientId() { return clientId; }
    public UUID getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getClientName() { return clientName; }
    public String getScope() { return scope; }
    public BigDecimal getTotalValue() { return totalValue; }
    public Integer getValidityDays() { return validityDays; }
    public LocalDate getProposalDate() { return proposalDate; }
    public String getStatus() { return status; }
    public String getConditions() { return conditions; }
    public String getNotes() { return notes; }

    public void setStatus(String status) { this.status = status; }
    public void setTitle(String title) { this.title = title; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public void setScope(String scope) { this.scope = scope; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }
    public void setProposalDate(LocalDate proposalDate) { this.proposalDate = proposalDate; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
}
