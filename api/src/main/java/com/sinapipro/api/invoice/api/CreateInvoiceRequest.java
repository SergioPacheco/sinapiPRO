package com.sinapipro.api.invoice.api;

import com.sinapipro.api.invoice.domain.InvoiceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotBlank @Size(max = 40) String number,
        @NotNull UUID budgetId,
        @NotNull UUID supplierId,
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate issueDate,
        @NotNull LocalDate dueDate,
        @NotNull InvoiceStatus status,
        String notes
) {}
