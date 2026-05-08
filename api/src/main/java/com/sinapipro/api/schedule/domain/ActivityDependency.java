package com.sinapipro.api.schedule.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "activity_dependency", uniqueConstraints = @UniqueConstraint(columnNames = {"predecessor_id", "successor_id"}))
public class ActivityDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predecessor_id", nullable = false)
    private ScheduleActivity predecessor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "successor_id", nullable = false)
    private ScheduleActivity successor;

    @Column(nullable = false, length = 10)
    private String type;

    protected ActivityDependency() {}

    public ActivityDependency(ScheduleActivity predecessor, ScheduleActivity successor, String type) {
        this.predecessor = predecessor;
        this.successor = successor;
        this.type = type;
    }

    public UUID getId() { return id; }
    public ScheduleActivity getPredecessor() { return predecessor; }
    public ScheduleActivity getSuccessor() { return successor; }
    public String getType() { return type; }
}
