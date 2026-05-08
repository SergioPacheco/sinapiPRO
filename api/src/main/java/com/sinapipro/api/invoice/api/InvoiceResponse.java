package com.sinapipro.api.invoice.api;

import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        String number,
        UUID budgetId,
        String budgetCode,
        UUID supplierId,
        String supplierName,
        BigDecimal amount,
        LocalDate issueDate,
        LocalDate dueDate,
        InvoiceStatus status,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(), invoice.getNumber(),
                invoice.getBudget().getId(), invoice.getBudget().getCode(),
                invoice.getSupplier().getId(), invoice.getSupplier().getName(),
                invoice.getAmount(), invoice.getIssueDate(), invoice.getDueDate(),
                invoice.getStatus(), invoice.getNotes(),
                invoice.getCreatedAt(), invoice.getUpdatedAt());
    }
}
