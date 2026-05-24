package com.sinapipro.api.commercial.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "development_unit", uniqueConstraints = @UniqueConstraint(columnNames = {"development_id", "code"}))
public class DevelopmentUnit extends TenantAwareEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "development_id", nullable = false) private Development development;
    @Column(nullable = false, length = 30) private String code;
    @Column(nullable = false, length = 40) private String type;
    @Column(precision = 10, scale = 2) private BigDecimal area;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal price;
    @Column(nullable = false, length = 20) private String status;
    private Integer floor;
    private Integer bedrooms;
    @Column(length = 300) private String notes;

    protected DevelopmentUnit() {}
    public DevelopmentUnit(Development development, String code, String type, BigDecimal area, BigDecimal price, Integer floor, Integer bedrooms) {
        this.development = development; this.code = code; this.type = type; this.area = area;
        this.price = price; this.status = "AVAILABLE"; this.floor = floor; this.bedrooms = bedrooms;
    }

    public Development getDevelopment() { return development; }
    public String getCode() { return code; }
    public String getType() { return type; }
    public BigDecimal getArea() { return area; }
    public BigDecimal getPrice() { return price; }
    public String getStatus() { return status; }
    public Integer getFloor() { return floor; }
    public Integer getBedrooms() { return bedrooms; }
    public String getNotes() { return notes; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void reserve() { this.status = "RESERVED"; }
    public void sell() { this.status = "SOLD"; }
    public void release() { this.status = "AVAILABLE"; }
}
