package com.sinapipro.api.supplier.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "supplier_bank_account")
public class SupplierBankAccount {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "supplier_id", nullable = false) private UUID supplierId;
    @Column(name = "bank_code", nullable = false, length = 10) private String bankCode;
    @Column(name = "bank_name", nullable = false, length = 100) private String bankName;
    @Column(nullable = false, length = 20) private String agency;
    @Column(name = "account_number", nullable = false, length = 30) private String accountNumber;
    @Column(name = "account_type", nullable = false, length = 20) private String accountType = "CORRENTE";
    @Column(name = "holder_name", length = 200) private String holderName;
    @Column(name = "holder_document", length = 20) private String holderDocument;
    @Column(name = "pix_key", length = 100) private String pixKey;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected SupplierBankAccount() {}
    public SupplierBankAccount(UUID supplierId, String bankCode, String bankName, String agency, String accountNumber, String accountType, String holderName, String holderDocument, String pixKey) {
        this.supplierId = supplierId; this.bankCode = bankCode; this.bankName = bankName; this.agency = agency; this.accountNumber = accountNumber; this.accountType = accountType; this.holderName = holderName; this.holderDocument = holderDocument; this.pixKey = pixKey;
    }

    public UUID getId() { return id; }
    public UUID getSupplierId() { return supplierId; }
    public String getBankCode() { return bankCode; }
    public String getBankName() { return bankName; }
    public String getAgency() { return agency; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public String getHolderName() { return holderName; }
    public String getHolderDocument() { return holderDocument; }
    public String getPixKey() { return pixKey; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String bankCode, String bankName, String agency, String accountNumber, String accountType, String holderName, String holderDocument, String pixKey, boolean active) {
        this.bankCode = bankCode; this.bankName = bankName; this.agency = agency; this.accountNumber = accountNumber; this.accountType = accountType; this.holderName = holderName; this.holderDocument = holderDocument; this.pixKey = pixKey; this.active = active; this.updatedAt = Instant.now();
    }
}
