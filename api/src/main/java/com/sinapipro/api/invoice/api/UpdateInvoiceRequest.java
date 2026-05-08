package com.sinapipro.api.invoice.api;

import com.sinapipro.api.invoice.domain.InvoiceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateInvoiceRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate dueDate,
        @NotNull InvoiceStatus status,
        String notes
) {}
