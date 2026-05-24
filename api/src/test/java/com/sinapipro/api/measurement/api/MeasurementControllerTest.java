package com.sinapipro.api.measurement.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.report.MeasurementReportService;
import com.sinapipro.api.measurement.application.MeasurementService;
import com.sinapipro.api.measurement.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementControllerTest {

    @Mock MeasurementRepository measurementRepository;
    @Mock MeasurementService measurementService;
    @Mock MeasurementReportService measurementReportService;
    @Mock MeasurementItemMemoRepository memoRepository;
    @Mock MeasurementHistoryRepository historyRepository;

    private MeasurementController controller;

    @BeforeEach
    void setUp() {
        controller = new MeasurementController(
                measurementRepository,
                measurementService,
                measurementReportService,
                memoRepository,
                historyRepository
        );
    }

    @Test
    @DisplayName("should reject a submitted measurement and persist history")
    void shouldRejectAndPersistHistory() {
        UUID projectId = UUID.randomUUID();
        UUID measurementId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(projectId);
        Measurement measurement = new Measurement(budget, 1, LocalDate.now().minusDays(10), LocalDate.now(), new BigDecimal("0.05"));
        measurement.submit();

        when(measurementRepository.findById(measurementId)).thenReturn(Optional.of(measurement));
        when(measurementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = controller.reject(projectId, measurementId, new MeasurementController.RejectRequest("Ajustar quantidade", "Fiscal A"));

        assertThat(response.status()).isEqualTo(MeasurementStatus.DRAFT);
        assertThat(response.rejectionReason()).isEqualTo("Ajustar quantidade");
        verify(historyRepository).save(any(MeasurementHistory.class));
    }

    @Test
    @DisplayName("should return history entries ordered by repository contract")
    void shouldReturnHistory() {
        UUID projectId = UUID.randomUUID();
        UUID measurementId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(projectId);
        Measurement measurement = new Measurement(budget, 1, LocalDate.now().minusDays(10), LocalDate.now(), new BigDecimal("0.05"));
        MeasurementHistory history = new MeasurementHistory(measurementId, "REJECT", "SUBMITTED", "DRAFT", "Fiscal A", "Motivo");

        when(measurementRepository.findById(measurementId)).thenReturn(Optional.of(measurement));
        when(historyRepository.findByMeasurementIdOrderByCreatedAtDesc(measurementId)).thenReturn(List.of(history));

        var response = controller.history(projectId, measurementId);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().action()).isEqualTo("REJECT");
        assertThat(response.getFirst().reason()).isEqualTo("Motivo");
    }

    @Test
    @DisplayName("should save memo and compute result when omitted")
    void shouldSaveMemoWithComputedResult() {
        UUID projectId = UUID.randomUUID();
        UUID measurementId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(projectId);
        Measurement measurement = new Measurement(budget, 1, LocalDate.now().minusDays(10), LocalDate.now(), new BigDecimal("0.05"));
        MeasurementItem item = new MeasurementItem(measurement, (UUID) null, "Item", new BigDecimal("2"), new BigDecimal("10"));
        measurement.getItems().add(item);
        forceItemId(item, itemId);

        when(measurementRepository.findById(measurementId)).thenReturn(Optional.of(measurement));
        when(memoRepository.findByMeasurementItemId(itemId)).thenReturn(Optional.empty());
        when(memoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var request = new MeasurementController.MemoRequest(List.of(
                new MeasurementController.MemoLineRequest("Parcela 1", "1+1", new BigDecimal("2")),
                new MeasurementController.MemoLineRequest("Parcela 2", "2+2", new BigDecimal("4"))
        ), null);

        var response = controller.saveMemo(projectId, measurementId, itemId, request);

        assertThat(response.measurementItemId()).isEqualTo(itemId);
        assertThat(response.result()).isEqualByComparingTo("6");
    }

    @Test
    @DisplayName("should return memo when present")
    void shouldGetMemo() {
        UUID projectId = UUID.randomUUID();
        UUID measurementId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(projectId);
        Measurement measurement = new Measurement(budget, 1, LocalDate.now().minusDays(10), LocalDate.now(), new BigDecimal("0.05"));
        MeasurementItem item = new MeasurementItem(measurement, (UUID) null, "Item", new BigDecimal("2"), new BigDecimal("10"));
        measurement.getItems().add(item);
        forceItemId(item, itemId);

        MeasurementItemMemo memo = new MeasurementItemMemo();
        memo.setMeasurementItemId(itemId);
        memo.setLines(List.of(new MeasurementItemMemo.MemoLine("L1", "1+1", new BigDecimal("2"))));
        memo.setResult(new BigDecimal("2"));

        when(measurementRepository.findById(measurementId)).thenReturn(Optional.of(measurement));
        when(memoRepository.findByMeasurementItemId(itemId)).thenReturn(Optional.of(memo));

        ResponseEntity<MeasurementController.MemoResponse> response = controller.getMemo(projectId, measurementId, itemId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().result()).isEqualByComparingTo("2");
    }

    @Test
    @DisplayName("should add extra item with contractor name")
    void shouldAddExtraItem() {
        UUID projectId = UUID.randomUUID();
        UUID measurementId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(projectId);
        Measurement measurement = new Measurement(budget, 1, LocalDate.now().minusDays(10), LocalDate.now(), new BigDecimal("0.05"));
        measurement.getItems().add(new MeasurementItem(measurement, (UUID) null, "Base", BigDecimal.ONE, new BigDecimal("100")));

        when(measurementRepository.findById(measurementId)).thenReturn(Optional.of(measurement));
        when(measurementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = controller.addExtraItem(
                projectId,
                measurementId,
                new MeasurementController.ExtraItemRequest(null, "Extra", new BigDecimal("3"), new BigDecimal("20"), "Empreiteira Y")
        );

        assertThat(response.items()).hasSize(2);
        var last = response.items().get(1);
        assertThat(last.extra()).isTrue();
        assertThat(last.contractorName()).isEqualTo("Empreiteira Y");
    }

    @Test
    @DisplayName("should reject access when measurement does not belong to project")
    void shouldRejectWhenMeasurementBelongsToAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID measurementId = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(anotherProjectId);
        Measurement measurement = new Measurement(budget, 1, LocalDate.now().minusDays(10), LocalDate.now(), new BigDecimal("0.05"));
        when(measurementRepository.findById(measurementId)).thenReturn(Optional.of(measurement));

        assertThatThrownBy(() -> controller.get(projectId, measurementId))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Measurement not found in project");
    }

    private void forceItemId(MeasurementItem item, UUID id) {
        try {
            var field = MeasurementItem.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(item, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
