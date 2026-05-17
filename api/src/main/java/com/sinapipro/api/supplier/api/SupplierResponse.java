package com.sinapipro.api.supplier.api;

import com.sinapipro.api.supplier.domain.Supplier;

import java.time.Instant;
import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String code,
        String name,
        String tradeName,
        String taxId,
        String email,
        String phone,
        String contactName,
        String website,
        String category,
        String qualificationStatus,
        Integer paymentTermDays,
        Integer leadTimeDays,
        String address,
        String city,
        String state,
        String postalCode,
        String notes,
        Integer rating,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static SupplierResponse from(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(), supplier.getCode(), supplier.getName(), supplier.getTradeName(),
                supplier.getTaxId(), supplier.getEmail(), supplier.getPhone(),
                supplier.getContactName(), supplier.getWebsite(), supplier.getCategory(),
                supplier.getQualificationStatus(), supplier.getPaymentTermDays(),
                supplier.getLeadTimeDays(), supplier.getAddress(), supplier.getCity(),
                supplier.getState(), supplier.getPostalCode(), supplier.getNotes(),
                supplier.getRating(), supplier.getActive(), supplier.getCreatedAt(), supplier.getUpdatedAt());
    }
}
