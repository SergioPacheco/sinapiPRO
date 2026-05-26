package com.sinapipro.api.registry.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "transporter")
public class Transporter extends TenantAwareEntity {
    @Column(nullable = false, length = 200) private String name;
    @Column(length = 20) private String document;
    @Column(name = "vehicle_plate", length = 10) private String vehiclePlate;
    @Column(name = "vehicle_type", length = 50) private String vehicleType;
    @Column(length = 30) private String phone;
    @Column(name = "cell_phone", length = 30) private String cellPhone;
    @Column(length = 30) private String whatsapp;
    @Column(length = 200) private String email;
    @Column(length = 400) private String address;
    @Column(length = 100) private String city;
    @Column(length = 2) private String state;
    @Column(name = "postal_code", length = 10) private String postalCode;
    @Column(length = 500) private String notes;
    @Column(nullable = false) private boolean active = true;

    protected Transporter() {}

    public Transporter(String name, String document, String vehiclePlate, String vehicleType,
                       String phone, String cellPhone, String whatsapp, String email,
                       String address, String city, String state, String postalCode, String notes) {
        this.name = name; this.document = document; this.vehiclePlate = vehiclePlate;
        this.vehicleType = vehicleType; this.phone = phone; this.cellPhone = cellPhone;
        this.whatsapp = whatsapp; this.email = email; this.address = address;
        this.city = city; this.state = state; this.postalCode = postalCode; this.notes = notes;
    }

    public String getName() { return name; }
    public String getDocument() { return document; }
    public String getVehiclePlate() { return vehiclePlate; }
    public String getVehicleType() { return vehicleType; }
    public String getPhone() { return phone; }
    public String getCellPhone() { return cellPhone; }
    public String getWhatsapp() { return whatsapp; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void deactivate() { this.active = false; }

    public void update(String name, String document, String vehiclePlate, String vehicleType,
                       String phone, String cellPhone, String whatsapp, String email,
                       String address, String city, String state, String postalCode, String notes) {
        this.name = name; this.document = document; this.vehiclePlate = vehiclePlate;
        this.vehicleType = vehicleType; this.phone = phone; this.cellPhone = cellPhone;
        this.whatsapp = whatsapp; this.email = email; this.address = address;
        this.city = city; this.state = state; this.postalCode = postalCode; this.notes = notes;
    }
}
