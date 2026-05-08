package com.sinapipro.api.budget;

import com.sinapipro.api.budget.api.BudgetFilter;
import com.sinapipro.api.budget.api.CreateBudgetRequest;
import com.sinapipro.api.budget.api.UpdateBudgetRequest;
import com.sinapipro.api.budget.application.BudgetCodeAlreadyExistsException;
import com.sinapipro.api.budget.application.BudgetService;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.budget.domain.BudgetStatus;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.shared.events.OperationEventPublisher;
import com.sinapipro.api.shared.observability.BusinessMetricsService;
import com.sinapipro.api.shared.observability.BusinessObservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock BudgetRepository repository;
    @Mock OperationEventPublisher eventPublisher;
    @Mock BusinessMetricsService metricsService;
    @Mock BusinessObservationService observationService;

    BudgetService service;

    @BeforeEach
    void setUp() {
        service = new BudgetService(repository, eventPublisher, metricsService, observationService);
        // Make observationService pass-through
        lenient().when(observationService.observe(anyString(), anyString(), any()))
                .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(2)).get());
    }

    @Test
    @DisplayName("should create budget when code is unique")
    void shouldCreateBudget() throws Exception {
        var request = new CreateBudgetRequest("BUD-001", "Title", "Customer",
                BigDecimal.valueOf(100000), BudgetStatus.DRAFT, LocalDate.now(), null, Map.of());

        when(repository.existsByCode("BUD-001")).thenReturn(false);
        when(repository.save(any(Budget.class))).thenAnswer(invocation -> {
            Budget b = invocation.getArgument(0);
            setId(b, UUID.randomUUID());
            return b;
        });

        Budget result = service.create(request);

        assertThat(result.getCode()).isEqualTo("BUD-001");
        assertThat(result.getTitle()).isEqualTo("Title");
        verify(repository).save(any(Budget.class));
    }

    @Test
    @DisplayName("should throw when code already exists")
    void shouldThrowWhenCodeExists() {
        var request = new CreateBudgetRequest("BUD-001", "Title", "Customer",
                BigDecimal.valueOf(100000), BudgetStatus.DRAFT, LocalDate.now(), null, Map.of());

        when(repository.existsByCode("BUD-001")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BudgetCodeAlreadyExistsException.class)
                .hasMessageContaining("BUD-001");
    }

    @Test
    @DisplayName("should throw DomainNotFoundException when budget not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(DomainNotFoundException.class);
    }

    @Test
    @DisplayName("should return filtered page")
    void shouldReturnFilteredPage() {
        var filter = new BudgetFilter(BudgetStatus.APPROVED, null);
        var pageable = PageRequest.of(0, 20);
        var budget = new Budget("BUD-001", "Title", "Customer",
                BigDecimal.valueOf(100000), BudgetStatus.APPROVED, LocalDate.now(), null, Map.of());
        Page<Budget> page = new PageImpl<>(List.of(budget));

        when(repository.findFiltered(BudgetStatus.APPROVED, null, pageable)).thenReturn(page);

        Page<Budget> result = service.findAll(filter, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getStatus()).isEqualTo(BudgetStatus.APPROVED);
    }

    @Test
    @DisplayName("should update budget fields")
    void shouldUpdateBudget() throws Exception {
        UUID id = UUID.randomUUID();
        var budget = new Budget("BUD-001", "Old Title", "Old Customer",
                BigDecimal.valueOf(100000), BudgetStatus.DRAFT, LocalDate.now(), null, Map.of());
        setId(budget, id);

        when(repository.findById(id)).thenReturn(Optional.of(budget));
        when(repository.save(any(Budget.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new UpdateBudgetRequest("New Title", "New Customer",
                BigDecimal.valueOf(200000), BudgetStatus.APPROVED, LocalDate.now(), LocalDate.now().plusMonths(6), Map.of("key", "value"));

        Budget result = service.update(id, request);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getStatus()).isEqualTo(BudgetStatus.APPROVED);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(200000));
    }

    private void setId(Object entity, UUID id) throws Exception {
        var field = entity.getClass().getSuperclass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
