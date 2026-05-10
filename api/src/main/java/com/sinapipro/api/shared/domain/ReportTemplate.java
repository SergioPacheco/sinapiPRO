package com.sinapipro.api.shared.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Template de personalização de relatórios (logo, cabeçalho, rodapé, cores).
 */
@Entity
@Table(name = "report_template")
public class ReportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @Column(name = "header_text", length = 500)
    private String headerText;

    @Column(name = "footer_text", length = 500)
    private String footerText;

    @Column(name = "primary_color", length = 7)
    private String primaryColor = "#1e3a5f";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public ReportTemplate() {}

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getLogoPath() { return logoPath; }
    public String getHeaderText() { return headerText; }
    public String getFooterText() { return footerText; }
    public String getPrimaryColor() { return primaryColor; }
    public Map<String, Object> getSettings() { return settings; }

    public void setName(String n) { this.name = n; }
    public void setLogoPath(String p) { this.logoPath = p; }
    public void setHeaderText(String t) { this.headerText = t; }
    public void setFooterText(String t) { this.footerText = t; }
    public void setPrimaryColor(String c) { this.primaryColor = c; }
    public void setSettings(Map<String, Object> s) { this.settings = s; }
}
