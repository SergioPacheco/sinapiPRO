package com.sinapipro.api.delivery.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "delivery")
public class Delivery extends AuditableEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "provisional_date")
    private LocalDate provisionalDate;

    @Column(name = "definitive_date")
    private LocalDate definitiveDate;

    @Column(name = "delivered_by", length = 140)
    private String deliveredBy;

    @Column(name = "received_by", length = 140)
    private String receivedBy;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeliveryChecklistItem> checklist = new ArrayList<>();

    public Delivery() {}

    public UUID getProjectId() { return projectId; }
    public String getStatus() { return status; }
    public LocalDate getProvisionalDate() { return provisionalDate; }
    public LocalDate getDefinitiveDate() { return definitiveDate; }
    public String getDeliveredBy() { return deliveredBy; }
    public String getReceivedBy() { return receivedBy; }
    public String getNotes() { return notes; }
    public List<DeliveryChecklistItem> getChecklist() { return checklist; }

    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public void setStatus(String status) { this.status = status; }
    public void setProvisionalDate(LocalDate d) { this.provisionalDate = d; }
    public void setDefinitiveDate(LocalDate d) { this.definitiveDate = d; }
    public void setDeliveredBy(String s) { this.deliveredBy = s; }
    public void setReceivedBy(String s) { this.receivedBy = s; }
    public void setNotes(String s) { this.notes = s; }

    public void addItem(DeliveryChecklistItem item) {
        checklist.add(item);
        item.setDelivery(this);
    }
}
