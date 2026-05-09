package com.sinapipro.api.budget.application;

import module java.base;

import com.sinapipro.api.budget.api.BudgetFilter;
import com.sinapipro.api.budget.api.CreateBudgetRequest;
import com.sinapipro.api.budget.api.UpdateBudgetRequest;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.shared.events.OperationEventPublisher;
import com.sinapipro.api.shared.events.OperationEventType;
import com.sinapipro.api.shared.observability.BusinessMetricsService;
import com.sinapipro.api.shared.observability.BusinessObservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository repository;
    private final OperationEventPublisher eventPublisher;
    private final BusinessMetricsService metricsService;
    private final BusinessObservationService observationService;

    public BudgetService(BudgetRepository repository,
                         OperationEventPublisher eventPublisher,
                         BusinessMetricsService metricsService,
                         BusinessObservationService observationService) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.metricsService = metricsService;
        this.observationService = observationService;
    }

    public Page<Budget> findAll(BudgetFilter filter, Pageable pageable) {
        return observationService.observe("budget.findAll", "budget",
                () -> repository.findFiltered(filter.status(), filter.customerName(), pageable));
    }

    public Budget findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + id));
    }

    public Budget findByCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + code));
    }

    @Transactional
    public Budget create(CreateBudgetRequest request) {
        return observationService.observe("budget.create", "budget", () -> {
            if (repository.existsByCode(request.code())) {
                throw new BudgetCodeAlreadyExistsException(request.code());
            }
            var budget = new Budget(
                    request.code(), request.title(), request.customerName(),
                    request.totalAmount(), request.status(), request.startDate(),
                    request.endDate(), request.metadata());
            var saved = repository.save(budget);
            metricsService.record("budget", OperationEventType.CREATED);
            eventPublisher.publish("budget", OperationEventType.CREATED,
                    saved.getId().toString(), "Budget created: " + saved.getCode());
            return saved;
        });
    }

    @Transactional
    public Budget update(UUID id, UpdateBudgetRequest request) {
        return observationService.observe("budget.update", "budget", () -> {
            var budget = findById(id);
            budget.update(request.title(), request.customerName(), request.totalAmount(),
                    request.status(), request.startDate(), request.endDate(), request.metadata());
            var saved = repository.save(budget);
            metricsService.record("budget", OperationEventType.UPDATED);
            eventPublisher.publish("budget", OperationEventType.UPDATED,
                    saved.getId().toString(), "Budget updated: " + saved.getCode());
            return saved;
        });
    }

    @Transactional
    public void delete(UUID id) {
        var budget = findById(id);
        repository.delete(budget);
        metricsService.record("budget", OperationEventType.DELETED);
        eventPublisher.publish("budget", OperationEventType.DELETED,
                id.toString(), "Budget deleted: " + budget.getCode());
    }
}
