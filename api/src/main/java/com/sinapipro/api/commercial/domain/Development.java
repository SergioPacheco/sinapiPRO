package com.sinapipro.api.commercial.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "development")
public class Development extends AuditableEntity {
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 400) private String address;
    @Column(length = 100) private String city;
    @Column(length = 2) private String state;
    @Column(name = "total_units", nullable = false) private int totalUnits;
    @Column(nullable = false, length = 20) private String status;
    @Column(name = "launch_date") private LocalDate launchDate;

    @OneToMany(mappedBy = "development", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DevelopmentUnit> units = new ArrayList<>();

    protected Development() {}
    public Development(String name, String address, String city, String state, int totalUnits, LocalDate launchDate) {
        this.name = name; this.address = address; this.city = city; this.state = state;
        this.totalUnits = totalUnits; this.status = "PLANNING"; this.launchDate = launchDate;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public int getTotalUnits() { return totalUnits; }
    public String getStatus() { return status; }
    public LocalDate getLaunchDate() { return launchDate; }
    public List<DevelopmentUnit> getUnits() { return units; }
    public void setStatus(String status) { this.status = status; }
}
