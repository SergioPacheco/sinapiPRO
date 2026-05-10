package com.sinapipro.api.dailylog.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_log", uniqueConstraints = @UniqueConstraint(columnNames = {"budget_id", "log_date"}))
public class DailyLog extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "weather_morning", length = 30)
    private String weatherMorning;

    @Column(name = "weather_afternoon", length = 30)
    private String weatherAfternoon;

    @Column(columnDefinition = "text")
    private String observations;

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyLogLabor> laborEntries = new ArrayList<>();

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyLogEquipment> equipmentEntries = new ArrayList<>();

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyLogOccurrence> occurrences = new ArrayList<>();

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyLogPhoto> photos = new ArrayList<>();

    protected DailyLog() {}

    public DailyLog(Budget budget, LocalDate logDate, String weatherMorning, String weatherAfternoon, String observations) {
        this.budget = budget;
        this.logDate = logDate;
        this.weatherMorning = weatherMorning;
        this.weatherAfternoon = weatherAfternoon;
        this.observations = observations;
    }

    public Budget getBudget() { return budget; }
    public LocalDate getLogDate() { return logDate; }
    public String getWeatherMorning() { return weatherMorning; }
    public String getWeatherAfternoon() { return weatherAfternoon; }
    public String getObservations() { return observations; }
    public List<DailyLogLabor> getLaborEntries() { return laborEntries; }
    public List<DailyLogEquipment> getEquipmentEntries() { return equipmentEntries; }
    public List<DailyLogOccurrence> getOccurrences() { return occurrences; }
    public List<DailyLogPhoto> getPhotos() { return photos; }
}
