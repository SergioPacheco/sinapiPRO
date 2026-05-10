package com.sinapipro.api.registry.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bank_account")
public class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "bank_code", nullable = false, length = 10) private String bankCode;
    @Column(name = "bank_name", nullable = false, length = 100) private String bankName;
    @Column(nullable = false, length = 20) private String agency;
    @Column(name = "account_number", nullable = false, length = 30) private String accountNumber;
    @Column(name = "account_type", nullable = false, length = 20) private String accountType;
    @Column(name = "holder_name", length = 200) private String holderName;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected BankAccount() {}
    public BankAccount(String bankCode, String bankName, String agency, String accountNumber, String accountType, String holderName) {
        this.bankCode = bankCode; this.bankName = bankName; this.agency = agency;
        this.accountNumber = accountNumber; this.accountType = accountType; this.holderName = holderName;
    }
    public UUID getId() { return id; }
    public String getBankCode() { return bankCode; }
    public String getBankName() { return bankName; }
    public String getAgency() { return agency; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public String getHolderName() { return holderName; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
}
