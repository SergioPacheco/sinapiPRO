package com.sinapipro.api.supplier.api;

import jakarta.validation.constraints.*;

public record CreateSupplierRequest(
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 140) String name,
        @Size(max = 140) String tradeName,
        @NotBlank @Size(max = 30) String taxId,
        @Email @Size(max = 140) String email,
        @Size(max = 40) String phone,
        @Size(max = 140) String contactName,
        @Size(max = 200) String website,
        @NotBlank @Size(max = 40) String category,
        @NotBlank @Size(max = 30) String qualificationStatus,
        @NotNull @Min(0) @Max(365) Integer paymentTermDays,
        @NotNull @Min(0) @Max(365) Integer leadTimeDays,
        @Size(max = 300) String address,
        @Size(max = 100) String city,
        @Pattern(regexp = "^$|^[A-Z]{2}$", message = "state must be a 2-letter UF") String state,
        @Size(max = 20) String postalCode,
        @Size(max = 1000) String notes,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotNull Boolean active
) {}
