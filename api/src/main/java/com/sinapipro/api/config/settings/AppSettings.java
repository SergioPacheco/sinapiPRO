package com.sinapipro.api.config.settings;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "app_settings")
public class AppSettings {

    @Id
    private String key;

    @Column(nullable = false)
    private String value;

    protected AppSettings() {}

    public AppSettings(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() { return key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    // Well-known keys
    public static final String DEFAULT_STATE = "default.state";
    public static final String DEFAULT_REFERENCE_MONTH = "default.referenceMonth";
    public static final String DEFAULT_DESONERATED = "default.desonerated";
}
