package com.sinapipro.api.finance.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "monetary_index")
public class MonetaryIndex {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true, length = 20) private String code;
    @Column(nullable = false, length = 100) private String name;
    @Column(length = 50) private String source;
    @Column(nullable = false) private boolean active = true;

    protected MonetaryIndex() {}
    public MonetaryIndex(String code, String name, String source) {
        this.code = code; this.name = name; this.source = source;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getSource() { return source; }
    public boolean isActive() { return active; }
}
