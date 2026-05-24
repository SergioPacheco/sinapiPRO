package com.sinapipro.api.registry.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "city", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "state"}))
public class City {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 100) private String name;
    @Column(nullable = false, length = 2) private String state;
    @Column(name = "ibge_code", length = 10) private String ibgeCode;

    protected City() {}
    public City(String name, String state, String ibgeCode) {
        this.name = name; this.state = state; this.ibgeCode = ibgeCode;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getState() { return state; }
    public String getIbgeCode() { return ibgeCode; }
}
