package com.sinapipro.api.finance.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "expense_type")
public class ExpenseType extends TenantAwareEntity {
    @Column(nullable = false, unique = true, length = 20) private String code;
    @Column(nullable = false, length = 100) private String name;
    @Column(length = 50) private String category;
    @Column(name = "accounting_code", length = 30) private String accountingCode;
    @Column(nullable = false) private boolean active = true;

    protected ExpenseType() {}
    public ExpenseType(String code, String name, String category, String accountingCode) {
        this.code = code; this.name = name; this.category = category; this.accountingCode = accountingCode;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getAccountingCode() { return accountingCode; }
    public boolean isActive() { return active; }
}
