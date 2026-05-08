package com.sinapipro.api.supplier.api;

import jakarta.validation.constraints.*;

public record CreateSupplierRequest(
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 140) String name,
        @Size(max = 140) String tradeName,
        @NotBlank @Size(max = 30) String taxId,
        @Email @Size(max = 140) String email,
        @Size(max = 40) String phone,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotNull Boolean active
) {}
