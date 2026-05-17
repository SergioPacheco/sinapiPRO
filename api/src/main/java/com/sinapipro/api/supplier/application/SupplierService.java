package com.sinapipro.api.supplier.application;

import module java.base;

import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.shared.events.OperationEventPublisher;
import com.sinapipro.api.shared.events.OperationEventType;
import com.sinapipro.api.shared.observability.BusinessMetricsService;
import com.sinapipro.api.shared.observability.BusinessObservationService;
import com.sinapipro.api.supplier.api.CreateSupplierRequest;
import com.sinapipro.api.supplier.api.UpdateSupplierRequest;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository repository;
    private final OperationEventPublisher eventPublisher;
    private final BusinessMetricsService metricsService;
    private final BusinessObservationService observationService;

    public SupplierService(SupplierRepository repository,
                           OperationEventPublisher eventPublisher,
                           BusinessMetricsService metricsService,
                           BusinessObservationService observationService) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.metricsService = metricsService;
        this.observationService = observationService;
    }

    public Page<Supplier> findAll(Boolean active, String name, Pageable pageable) {
        return observationService.observe("supplier.findAll", "supplier",
                () -> repository.findFiltered(active, name, pageable));
    }

    public Supplier findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Supplier not found: " + id));
    }

    @Transactional
    public Supplier create(CreateSupplierRequest request) {
        return observationService.observe("supplier.create", "supplier", () -> {
            if (repository.existsByCode(request.code())) {
                throw new IllegalArgumentException("Supplier code already exists: " + request.code());
            }
            if (repository.existsByTaxId(request.taxId())) {
                throw new IllegalArgumentException("Tax ID already registered: " + request.taxId());
            }
            var supplier = new Supplier(request.code(), request.name(), request.tradeName(),
                    request.taxId(), request.email(), request.phone(), request.contactName(),
                    request.website(), request.category(), request.qualificationStatus(),
                    request.paymentTermDays(), request.leadTimeDays(), request.address(),
                    request.city(), request.state(), request.postalCode(), request.notes(),
                    request.rating(), request.active());
            var saved = repository.save(supplier);
            metricsService.record("supplier", OperationEventType.CREATED);
            eventPublisher.publish("supplier", OperationEventType.CREATED, saved.getId().toString(), "Supplier created: " + saved.getCode());
            return saved;
        });
    }

    @Transactional
    public Supplier update(UUID id, UpdateSupplierRequest request) {
        return observationService.observe("supplier.update", "supplier", () -> {
            var supplier = findById(id);
            supplier.update(request.name(), request.tradeName(), request.email(),
                    request.phone(), request.contactName(), request.website(),
                    request.category(), request.qualificationStatus(), request.paymentTermDays(),
                    request.leadTimeDays(), request.address(), request.city(),
                    request.state(), request.postalCode(), request.notes(),
                    request.rating(), request.active());
            var saved = repository.save(supplier);
            metricsService.record("supplier", OperationEventType.UPDATED);
            eventPublisher.publish("supplier", OperationEventType.UPDATED, saved.getId().toString(), "Supplier updated: " + saved.getCode());
            return saved;
        });
    }

    @Transactional
    public void delete(UUID id) {
        var supplier = findById(id);
        repository.delete(supplier);
        metricsService.record("supplier", OperationEventType.DELETED);
        eventPublisher.publish("supplier", OperationEventType.DELETED, id.toString(), "Supplier deleted: " + supplier.getCode());
    }
}
