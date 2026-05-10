package com.sinapipro.api.dailylog.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "daily_log_material")
public class DailyLogMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    @Column(nullable = false, length = 10)
    private String type; // IN or OUT

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal quantity;

    @Column(length = 20)
    private String unit;

    @Column(name = "invoice_number", length = 40)
    private String invoiceNumber;

    @Column(length = 300)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public DailyLogMaterial() {}

    public UUID getId() { return id; }
    public DailyLog getDailyLog() { return dailyLog; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getNotes() { return notes; }

    public void setDailyLog(DailyLog d) { this.dailyLog = d; }
    public void setType(String t) { this.type = t; }
    public void setDescription(String d) { this.description = d; }
    public void setQuantity(BigDecimal q) { this.quantity = q; }
    public void setUnit(String u) { this.unit = u; }
    public void setInvoiceNumber(String n) { this.invoiceNumber = n; }
    public void setNotes(String n) { this.notes = n; }
}
