package com.sinapipro.api.delivery.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "delivery_checklist_item")
public class DeliveryChecklistItem extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false)
    private boolean checked = false;

    @Column(length = 300)
    private String notes;

    public DeliveryChecklistItem() {}

    public DeliveryChecklistItem(String description) { this.description = description; }

    public Delivery getDelivery() { return delivery; }
    public String getDescription() { return description; }
    public boolean isChecked() { return checked; }
    public String getNotes() { return notes; }

    public void setDelivery(Delivery d) { this.delivery = d; }
    public void setChecked(boolean c) { this.checked = c; }
    public void setNotes(String n) { this.notes = n; }
}
