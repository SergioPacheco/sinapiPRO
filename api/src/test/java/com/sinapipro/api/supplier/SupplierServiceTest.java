package com.sinapipro.api.supplier;

import com.sinapipro.api.shared.events.OperationEventPublisher;
import com.sinapipro.api.shared.events.OperationEventType;
import com.sinapipro.api.shared.observability.BusinessMetricsService;
import com.sinapipro.api.shared.observability.BusinessObservationService;
import com.sinapipro.api.supplier.api.CreateSupplierRequest;
import com.sinapipro.api.supplier.api.UpdateSupplierRequest;
import com.sinapipro.api.supplier.application.SupplierService;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock SupplierRepository repository;
    @Mock OperationEventPublisher eventPublisher;
    @Mock BusinessMetricsService metricsService;
    @Mock BusinessObservationService observationService;

    SupplierService service;

    @BeforeEach
    void setUp() {
        service = new SupplierService(repository, eventPublisher, metricsService, observationService);
        doAnswer(invocation -> ((java.util.function.Supplier<?>) invocation.getArgument(2)).get())
                .when(observationService).observe(anyString(), anyString(), any(java.util.function.Supplier.class));
    }

    @Test
    @DisplayName("should create supplier with richer master data")
    void shouldCreateSupplierWithMasterData() {
        when(repository.existsByCode("SUP-001")).thenReturn(false);
        when(repository.existsByTaxId("12.345.678/0001-99")).thenReturn(false);
        when(repository.save(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier supplier = invocation.getArgument(0);
            ReflectionTestUtils.setField(supplier, "id", UUID.randomUUID());
            return supplier;
        });

        Supplier created = service.create(new CreateSupplierRequest(
                "SUP-001",
                "Fornecedor Exemplo Ltda",
                "Fornecedor Exemplo",
                "12.345.678/0001-99",
                "contato@fornecedor.com.br",
                "(48) 3333-4444",
                "Maria Compras",
                "https://fornecedor.com.br",
                "MATERIAL",
                "APPROVED",
                28,
                5,
                "Rua das Compras, 100",
                "Florianopolis",
                "SC",
                "88000-000",
                "Homologado para concreto e acabamentos",
                5,
                true
        ));

        assertThat(created.getContactName()).isEqualTo("Maria Compras");
        assertThat(created.getCategory()).isEqualTo("MATERIAL");
        assertThat(created.getQualificationStatus()).isEqualTo("APPROVED");
        assertThat(created.getPaymentTermDays()).isEqualTo(28);
        assertThat(created.getLeadTimeDays()).isEqualTo(5);
        assertThat(created.getCity()).isEqualTo("Florianopolis");
        verify(metricsService).record("supplier", OperationEventType.CREATED);
    }

    @Test
    @DisplayName("should update supplier master data")
    void shouldUpdateSupplierMasterData() {
        UUID supplierId = UUID.randomUUID();
        Supplier current = new Supplier(
                "SUP-001",
                "Fornecedor Exemplo Ltda",
                "Fornecedor Exemplo",
                "12.345.678/0001-99",
                "contato@fornecedor.com.br",
                "(48) 3333-4444",
                "Maria Compras",
                "https://fornecedor.com.br",
                "MATERIAL",
                "APPROVED",
                28,
                5,
                "Rua das Compras, 100",
                "Florianopolis",
                "SC",
                "88000-000",
                "Homologado para concreto e acabamentos",
                5,
                true
        );

        when(repository.findById(supplierId)).thenReturn(Optional.of(current));
        ReflectionTestUtils.setField(current, "id", supplierId);
        when(repository.save(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Supplier updated = service.update(supplierId, new UpdateSupplierRequest(
                "Fornecedor Exemplo Ltda",
                "Fornecedor Exemplo",
                "suprimentos@fornecedor.com.br",
                "(48) 9999-1111",
                "Carlos Suprimentos",
                "https://portal.fornecedor.com.br",
                "SERVICE",
                "UNDER_REVIEW",
                35,
                12,
                "Avenida Industrial, 500",
                "Sao Jose",
                "SC",
                "88100-000",
                "Reavaliacao em andamento",
                4,
                true
        ));

        assertThat(updated.getEmail()).isEqualTo("suprimentos@fornecedor.com.br");
        assertThat(updated.getContactName()).isEqualTo("Carlos Suprimentos");
        assertThat(updated.getCategory()).isEqualTo("SERVICE");
        assertThat(updated.getQualificationStatus()).isEqualTo("UNDER_REVIEW");
        assertThat(updated.getPaymentTermDays()).isEqualTo(35);
        assertThat(updated.getLeadTimeDays()).isEqualTo(12);
        verify(metricsService).record("supplier", OperationEventType.UPDATED);
    }
}
