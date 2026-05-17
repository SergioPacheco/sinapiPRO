package com.sinapipro.api.measurement.api;

import com.sinapipro.api.measurement.application.MeasurementService;
import com.sinapipro.api.measurement.application.MeasurementService.*;
import com.sinapipro.api.measurement.application.MeasurementReportService;
import com.sinapipro.api.measurement.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Measurements", description = "Periodic work measurements with approval workflow")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/measurements")
public class MeasurementController {

    private final MeasurementRepository measurementRepository;
    private final MeasurementService measurementService;
    private final MeasurementReportService measurementReportService;
    private final MeasurementItemMemoRepository memoRepository;
    private final MeasurementHistoryRepository historyRepository;

    public MeasurementController(MeasurementRepository measurementRepository, MeasurementService measurementService,
                                 MeasurementReportService measurementReportService,
                                 MeasurementItemMemoRepository memoRepository,
                                 MeasurementHistoryRepository historyRepository) {
        this.measurementRepository = measurementRepository;
        this.measurementService = measurementService;
        this.measurementReportService = measurementReportService;
        this.memoRepository = memoRepository;
        this.historyRepository = historyRepository;
    }

    @Operation(summary = "List measurements for a budget")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<MeasurementResponse> list(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(measurementRepository.findByBudgetId(projectId, pageable).map(MeasurementResponse::from));
    }

    @Operation(summary = "Get measurement detail")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    MeasurementResponse get(@PathVariable UUID projectId, @PathVariable UUID id) {
        return MeasurementResponse.from(findMeasurementInProject(projectId, id));
    }

    @Operation(summary = "Get measurement detail with contracted, previous, period, cumulative and balance quantities")
    @GetMapping("/{id}/detail")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    MeasurementDetail detail(@PathVariable UUID projectId, @PathVariable UUID id) {
        findMeasurementInProject(projectId, id);
        return measurementService.detail(id);
    }

    @Operation(summary = "List budget items available for measurement")
    @GetMapping("/available-items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<AvailableBudgetItem> availableItems(@PathVariable UUID projectId) {
        return measurementService.availableBudgetItems(projectId);
    }

    @Operation(summary = "Create a measurement")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<MeasurementResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateMeasurementRequest req) {
        List<ItemInput> items = req.items().stream()
                .map(i -> new ItemInput(i.costCodeId(), i.budgetItemId(), i.description(), i.quantity(), i.unitPrice()))
                .toList();
        Measurement saved = measurementService.create(projectId, req.number(), req.periodStart(), req.periodEnd(),
                req.retentionPct(), items);
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + projectId + "/measurements/" + saved.getId()))
                .body(MeasurementResponse.from(saved));
    }

    @Operation(summary = "Submit measurement for approval")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    MeasurementResponse submit(@PathVariable UUID projectId, @PathVariable UUID id) {
        Measurement current = findMeasurementInProject(projectId, id);
        String fromStatus = current.getStatus().name();
        Measurement saved = measurementService.submit(id);
        historyRepository.save(new MeasurementHistory(saved.getId(), "SUBMIT", fromStatus,
                saved.getStatus().name(), null, null));
        return MeasurementResponse.from(saved);
    }

    @Operation(summary = "Approve measurement")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    MeasurementResponse approve(@PathVariable UUID projectId, @PathVariable UUID id) {
        Measurement current = findMeasurementInProject(projectId, id);
        String fromStatus = current.getStatus().name();
        Measurement saved = measurementService.approve(id);
        historyRepository.save(new MeasurementHistory(saved.getId(), "APPROVE", fromStatus,
                saved.getStatus().name(), null, null));
        return MeasurementResponse.from(saved);
    }

    @Operation(summary = "Reject measurement with reason")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    MeasurementResponse reject(@PathVariable UUID projectId, @PathVariable UUID id,
                               @Valid @RequestBody RejectRequest req) {
        Measurement m = findMeasurementInProject(projectId, id);
        String fromStatus = m.getStatus().name();
        m.reject(req.reason());
        Measurement saved = measurementRepository.save(m);
        historyRepository.save(new MeasurementHistory(saved.getId(), "REJECT", fromStatus,
                saved.getStatus().name(), req.performedBy(), req.reason()));
        return MeasurementResponse.from(saved);
    }

    @Operation(summary = "Get measurement approval history")
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<HistoryResponse> history(@PathVariable UUID projectId, @PathVariable UUID id) {
        findMeasurementInProject(projectId, id);
        return historyRepository.findByMeasurementIdOrderByCreatedAtDesc(id).stream()
                .map(h -> new HistoryResponse(h.getId(), h.getAction(), h.getFromStatus(), h.getToStatus(),
                        h.getPerformedBy(), h.getReason(), h.getCreatedAt() != null ? h.getCreatedAt().toString() : null))
                .toList();
    }

    @Operation(summary = "Get memo for a measurement item")
    @GetMapping("/{id}/items/{itemId}/memo")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<MemoResponse> getMemo(@PathVariable UUID projectId, @PathVariable UUID id, @PathVariable UUID itemId) {
        Measurement m = findMeasurementInProject(projectId, id);
        boolean belongs = m.getItems().stream().anyMatch(i -> i.getId().equals(itemId));
        if (!belongs) throw new DomainNotFoundException("Measurement item not found in measurement: " + itemId);

        return memoRepository.findByMeasurementItemId(itemId)
                .map(memo -> ResponseEntity.ok(new MemoResponse(
                        memo.getMeasurementItemId(),
                        memo.getLines().stream().map(l -> new MemoLineRequest(l.description(), l.formula(), l.value())).toList(),
                        memo.getResult()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Save memo for a measurement item")
    @PutMapping("/{id}/items/{itemId}/memo")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    MemoResponse saveMemo(@PathVariable UUID projectId, @PathVariable UUID id, @PathVariable UUID itemId,
                          @Valid @RequestBody MemoRequest req) {
        Measurement m = findMeasurementInProject(projectId, id);
        boolean belongs = m.getItems().stream().anyMatch(i -> i.getId().equals(itemId));
        if (!belongs) throw new DomainNotFoundException("Measurement item not found in measurement: " + itemId);

        MeasurementItemMemo memo = memoRepository.findByMeasurementItemId(itemId).orElseGet(MeasurementItemMemo::new);
        memo.setMeasurementItemId(itemId);
        memo.setLines(req.lines().stream()
                .map(l -> new MeasurementItemMemo.MemoLine(l.description(), l.formula(), l.value()))
                .toList());
        BigDecimal result = req.result() != null
                ? req.result()
                : req.lines().stream().map(MemoLineRequest::value).reduce(BigDecimal.ZERO, BigDecimal::add);
        memo.setResult(result);
        MeasurementItemMemo saved = memoRepository.save(memo);
        return new MemoResponse(
                saved.getMeasurementItemId(),
                saved.getLines().stream().map(l -> new MemoLineRequest(l.description(), l.formula(), l.value())).toList(),
                saved.getResult()
        );
    }

    @Operation(summary = "Add extra service item to measurement")
    @PostMapping("/{id}/extra-items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    MeasurementResponse addExtraItem(@PathVariable UUID projectId, @PathVariable UUID id,
                                     @Valid @RequestBody ExtraItemRequest req) {
        Measurement m = findMeasurementInProject(projectId, id);
        MeasurementItem item = new MeasurementItem(m, req.costCodeId(), req.description(), req.quantity(), req.unitPrice());
        item.setExtra(true);
        if (req.contractorName() != null && !req.contractorName().isBlank()) item.setContractorName(req.contractorName());
        m.getItems().add(item);
        return MeasurementResponse.from(measurementRepository.save(m));
    }

    @Operation(summary = "Summary of all measurements for a budget (approved totals)")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    MeasurementSummary summary(@PathVariable UUID projectId) {
        return measurementService.summary(projectId);
    }

    @Operation(summary = "Cumulative amounts up to a specific measurement")
    @GetMapping("/{id}/cumulative")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    CumulativeResult cumulative(@PathVariable UUID projectId, @PathVariable UUID id) {
        findMeasurementInProject(projectId, id);
        return measurementService.cumulative(projectId, id);
    }

    @Operation(summary = "Balance remaining to measure against contracted total")
    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    BalanceResult balance(@PathVariable UUID projectId, @RequestParam BigDecimal contractedTotal) {
        return measurementService.balance(projectId, contractedTotal);
    }

    @Operation(summary = "Measurement bulletin PDF")
    @GetMapping(value = "/{id}/reports/bulletin.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<byte[]> bulletinReport(@PathVariable UUID projectId, @PathVariable UUID id) {
        findMeasurementInProject(projectId, id);
        byte[] pdf = measurementReportService.generateBulletinPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=measurement-bulletin-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private Measurement findMeasurementInProject(UUID projectId, UUID measurementId) {
        Measurement measurement = measurementRepository.findById(measurementId)
                .orElseThrow(() -> new DomainNotFoundException("Measurement not found: " + measurementId));
        if (!projectId.equals(measurement.getBudget().getId())) {
            throw new DomainNotFoundException("Measurement not found in project: " + measurementId);
        }
        return measurement;
    }

    // --- DTOs ---
    record CreateMeasurementRequest(@NotNull Integer number, @NotNull LocalDate periodStart,
                                    @NotNull LocalDate periodEnd, @NotNull BigDecimal retentionPct,
                                    @NotNull List<ItemRequest> items) {}
    record ItemRequest(UUID costCodeId, UUID budgetItemId, String description, @NotNull @Positive BigDecimal quantity,
                       BigDecimal unitPrice) {}
    record RejectRequest(@NotNull String reason, String performedBy) {}
    record HistoryResponse(UUID id, String action, String fromStatus, String toStatus, String performedBy, String reason, String createdAt) {}
    record MemoLineRequest(@NotNull String description, @NotNull String formula, @NotNull BigDecimal value) {}
    record MemoRequest(@NotNull List<MemoLineRequest> lines, BigDecimal result) {}
    record MemoResponse(UUID measurementItemId, List<MemoLineRequest> lines, BigDecimal result) {}
    record ExtraItemRequest(UUID costCodeId, @NotNull String description, @NotNull @Positive BigDecimal quantity,
                            @NotNull @Positive BigDecimal unitPrice, String contractorName) {}

    record MeasurementResponse(UUID id, Integer number, LocalDate periodStart, LocalDate periodEnd,
                               MeasurementStatus status, BigDecimal retentionPct,
                               BigDecimal grossAmount, BigDecimal netAmount, String rejectionReason, List<ItemResponse> items) {
        static MeasurementResponse from(Measurement m) {
            return new MeasurementResponse(m.getId(), m.getNumber(), m.getPeriodStart(), m.getPeriodEnd(),
                    m.getStatus(), m.getRetentionPct(), m.getGrossAmount(), m.getNetAmount(), m.getRejectionReason(),
                    m.getItems().stream().map(i -> new ItemResponse(i.getId(), i.getBudgetItemId(), i.getDescription(),
                            i.getQuantity(), i.getUnitPrice(), i.getAmount(), i.isExtra(), i.getContractorName())).toList());
        }
    }

    record ItemResponse(UUID id, UUID budgetItemId, String description, BigDecimal quantity, BigDecimal unitPrice,
                        BigDecimal amount, boolean extra, String contractorName) {}
}
