package com.sinapipro.api.supplier.api;

import jakarta.validation.constraints.*;

public record UpdateSupplierRequest(
        @NotBlank @Size(max = 140) String name,
        @Size(max = 140) String tradeName,
        @Email @Size(max = 140) String email,
        @Size(max = 40) String phone,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotNull Boolean active
) {}
