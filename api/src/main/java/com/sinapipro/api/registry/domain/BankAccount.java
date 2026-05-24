package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bank_account")
public class BankAccount extends TenantAwareEntity {
    @Column(name = "bank_code", nullable = false, length = 10) private String bankCode;
    @Column(name = "bank_name", nullable = false, length = 100) private String bankName;
    @Column(nullable = false, length = 20) private String agency;
    @Column(name = "account_number", nullable = false, length = 30) private String accountNumber;
    @Column(name = "account_type", nullable = false, length = 20) private String accountType;
    @Column(name = "holder_name", length = 200) private String holderName;
    @Column(nullable = false) private boolean active = true;

    // V11 enrichment fields
    @Column(name = "initial_balance", precision = 18, scale = 2) private java.math.BigDecimal initialBalance;
    @Column(name = "initial_balance_date") private java.time.LocalDate initialBalanceDate;
    @Column(name = "cnab_layout", length = 10) private String cnabLayout;
    @Column(name = "covenant_code", length = 30) private String covenantCode;
    @Column(name = "wallet_code", length = 10) private String walletCode;
    @Column(name = "our_number_sequence") private Long ourNumberSequence;
    @Column(name = "project_id") private UUID projectId;

    protected BankAccount() {}
    public BankAccount(String bankCode, String bankName, String agency, String accountNumber, String accountType, String holderName) {
        this.bankCode = bankCode; this.bankName = bankName; this.agency = agency;
        this.accountNumber = accountNumber; this.accountType = accountType; this.holderName = holderName;
    }
    public String getBankCode() { return bankCode; }
    public String getBankName() { return bankName; }
    public String getAgency() { return agency; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public String getHolderName() { return holderName; }
    public boolean isActive() { return active; }
    public java.math.BigDecimal getInitialBalance() { return initialBalance; }
    public java.time.LocalDate getInitialBalanceDate() { return initialBalanceDate; }
    public String getCnabLayout() { return cnabLayout; }
    public String getCovenantCode() { return covenantCode; }
    public String getWalletCode() { return walletCode; }
    public Long getOurNumberSequence() { return ourNumberSequence; }
    public UUID getProjectId() { return projectId; }
    public void deactivate() { this.active = false; }
}
