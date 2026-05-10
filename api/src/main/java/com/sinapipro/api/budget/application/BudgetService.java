package com.sinapipro.api.budget.application;

import module java.base;

import com.sinapipro.api.budget.api.BudgetFilter;
import com.sinapipro.api.budget.api.CreateBudgetRequest;
import com.sinapipro.api.budget.api.UpdateBudgetRequest;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BdiConfig;
import com.sinapipro.api.budget.domain.BdiConfigRepository;
import com.sinapipro.api.budget.domain.BudgetItem;
import com.sinapipro.api.budget.domain.BudgetItemRepository;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.budget.domain.BudgetStage;
import com.sinapipro.api.budget.domain.BudgetStageRepository;
import com.sinapipro.api.budget.domain.BudgetStatus;
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
    private final BudgetStageRepository stageRepository;
    private final BudgetItemRepository itemRepository;
    private final BdiConfigRepository bdiConfigRepository;
    private final OperationEventPublisher eventPublisher;
    private final BusinessMetricsService metricsService;
    private final BusinessObservationService observationService;

    public BudgetService(BudgetRepository repository,
                         BudgetStageRepository stageRepository,
                         BudgetItemRepository itemRepository,
                         BdiConfigRepository bdiConfigRepository,
                         OperationEventPublisher eventPublisher,
                         BusinessMetricsService metricsService,
                         BusinessObservationService observationService) {
        this.repository = repository;
        this.stageRepository = stageRepository;
        this.itemRepository = itemRepository;
        this.bdiConfigRepository = bdiConfigRepository;
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
    public Budget copy(UUID sourceId, String code, String title) {
        return observationService.observe("budget.copy", "budget", () -> {
            if (repository.existsByCode(code)) {
                throw new BudgetCodeAlreadyExistsException(code);
            }
            var source = findById(sourceId);
            var copy = repository.save(new Budget(
                    code, title, source.getCustomerName(), source.getTotalAmount(),
                    BudgetStatus.DRAFT, source.getStartDate(), source.getEndDate(), source.getMetadata()));

            bdiConfigRepository.findByBudgetId(sourceId).ifPresent(bdi -> bdiConfigRepository.save(new BdiConfig(
                    copy, bdi.getAdministration(), bdi.getProfit(), bdi.getTaxes(),
                    bdi.getSocialCharges(), bdi.getFinancialExpenses(), bdi.getRisks())));

            stageRepository.findRootStages(sourceId).forEach(stage -> copyStage(stage, copy, null));

            metricsService.record("budget", OperationEventType.CREATED);
            eventPublisher.publish("budget", OperationEventType.CREATED,
                    copy.getId().toString(), "Budget copied: " + copy.getCode());
            return copy;
        });
    }

    @Transactional
    public Budget activate(UUID id) {
        return observationService.observe("budget.activate", "budget", () -> {
            var selected = findById(id);
            repository.findAll().forEach(budget -> {
                boolean wasActive = budget.isActive();
                budget.setActive(budget.getId().equals(id));
                if (budget.getId().equals(id)) {
                    budget.setStatus(BudgetStatus.IN_EXECUTION);
                } else if (wasActive) {
                    budget.setStatus(BudgetStatus.SUPERSEDED);
                }
            });
            var saved = repository.save(selected);
            metricsService.record("budget", OperationEventType.UPDATED);
            eventPublisher.publish("budget", OperationEventType.UPDATED,
                    saved.getId().toString(), "Budget activated: " + saved.getCode());
            return saved;
        });
    }

    private void copyStage(BudgetStage source, Budget budget, BudgetStage parent) {
        var copiedStage = stageRepository.save(new BudgetStage(budget, parent, source.getName(), source.getSortOrder()));
        source.getItems().forEach(item -> itemRepository.save(new BudgetItem(
                copiedStage, item.getComposition(), item.getQuantity(), item.getUnitCost(), item.getBdiPct())));
        source.getChildren().forEach(child -> copyStage(child, budget, copiedStage));
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
