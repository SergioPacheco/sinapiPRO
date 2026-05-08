package com.sinapipro.api.invoice.application;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.invoice.api.CreateInvoiceRequest;
import com.sinapipro.api.invoice.api.UpdateInvoiceRequest;
import com.sinapipro.api.invoice.domain.Invoice;
import com.sinapipro.api.invoice.domain.InvoiceRepository;
import com.sinapipro.api.invoice.domain.InvoiceStatus;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.shared.events.OperationEventPublisher;
import com.sinapipro.api.shared.events.OperationEventType;
import com.sinapipro.api.shared.observability.BusinessMetricsService;
import com.sinapipro.api.shared.observability.BusinessObservationService;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BudgetRepository budgetRepository;
    private final SupplierRepository supplierRepository;
    private final OperationEventPublisher eventPublisher;
    private final BusinessMetricsService metricsService;
    private final BusinessObservationService observationService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          BudgetRepository budgetRepository,
                          SupplierRepository supplierRepository,
                          OperationEventPublisher eventPublisher,
                          BusinessMetricsService metricsService,
                          BusinessObservationService observationService) {
        this.invoiceRepository = invoiceRepository;
        this.budgetRepository = budgetRepository;
        this.supplierRepository = supplierRepository;
        this.eventPublisher = eventPublisher;
        this.metricsService = metricsService;
        this.observationService = observationService;
    }

    public Page<Invoice> findAll(InvoiceStatus status, UUID budgetId, UUID supplierId, Pageable pageable) {
        return observationService.observe("invoice.findAll", "invoice",
                () -> invoiceRepository.findFiltered(status, budgetId, supplierId, pageable));
    }

    public Invoice findById(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Invoice not found: " + id));
    }

    @Transactional
    public Invoice create(CreateInvoiceRequest request) {
        return observationService.observe("invoice.create", "invoice", () -> {
            if (invoiceRepository.existsByNumber(request.number())) {
                throw new IllegalArgumentException("Invoice number already exists: " + request.number());
            }
            Budget budget = budgetRepository.findById(request.budgetId())
                    .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + request.budgetId()));
            Supplier supplier = supplierRepository.findById(request.supplierId())
                    .orElseThrow(() -> new DomainNotFoundException("Supplier not found: " + request.supplierId()));

            Invoice invoice = new Invoice(request.number(), budget, supplier, request.amount(),
                    request.issueDate(), request.dueDate(), request.status(), request.notes());
            Invoice saved = invoiceRepository.save(invoice);
            metricsService.record("invoice", OperationEventType.CREATED);
            eventPublisher.publish("invoice", OperationEventType.CREATED, saved.getId().toString(), "Invoice created: " + saved.getNumber());
            return saved;
        });
    }

    @Transactional
    public Invoice update(UUID id, UpdateInvoiceRequest request) {
        return observationService.observe("invoice.update", "invoice", () -> {
            Invoice invoice = findById(id);
            invoice.update(request.amount(), request.dueDate(), request.status(), request.notes());
            Invoice saved = invoiceRepository.save(invoice);
            metricsService.record("invoice", OperationEventType.UPDATED);
            eventPublisher.publish("invoice", OperationEventType.UPDATED, saved.getId().toString(), "Invoice updated: " + saved.getNumber());
            return saved;
        });
    }

    @Transactional
    public void delete(UUID id) {
        Invoice invoice = findById(id);
        invoiceRepository.delete(invoice);
        metricsService.record("invoice", OperationEventType.DELETED);
        eventPublisher.publish("invoice", OperationEventType.DELETED, id.toString(), "Invoice deleted: " + invoice.getNumber());
    }
}
