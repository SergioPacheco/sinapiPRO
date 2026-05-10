package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.application.AbcCurveService;
import com.sinapipro.api.budget.application.BudgetCalculationService;
import com.sinapipro.api.budget.application.BudgetReportService;
import com.sinapipro.api.budget.application.PriceAdjustmentService;
import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.config.settings.AppSettings;
import com.sinapipro.api.config.settings.AppSettingsRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.application.CompositionCostService;
import com.sinapipro.api.sinapi.domain.Composition;
import com.sinapipro.api.sinapi.domain.CompositionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Budget Detail", description = "Budget stages, items, BDI and ABC curve")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}")
public class BudgetDetailController {

    private final BudgetRepository budgetRepository;
    private final BudgetStageRepository stageRepository;
    private final BudgetItemRepository itemRepository;
    private final BdiConfigRepository bdiConfigRepository;
    private final CompositionRepository compositionRepository;
    private final BudgetCalculationService calculationService;
    private final AbcCurveService abcCurveService;
    private final PriceAdjustmentService priceAdjustmentService;
    private final BudgetReportService budgetReportService;
    private final CompositionCostService compositionCostService;
    private final AppSettingsRepository settingsRepository;

    public BudgetDetailController(BudgetRepository budgetRepository, BudgetStageRepository stageRepository,
                                  BudgetItemRepository itemRepository, BdiConfigRepository bdiConfigRepository,
                                  CompositionRepository compositionRepository,
                                  BudgetCalculationService calculationService, AbcCurveService abcCurveService,
                                  PriceAdjustmentService priceAdjustmentService,
                                  BudgetReportService budgetReportService,
                                  CompositionCostService compositionCostService,
                                  AppSettingsRepository settingsRepository) {
        this.budgetRepository = budgetRepository;
        this.stageRepository = stageRepository;
        this.itemRepository = itemRepository;
        this.bdiConfigRepository = bdiConfigRepository;
        this.compositionRepository = compositionRepository;
        this.calculationService = calculationService;
        this.abcCurveService = abcCurveService;
        this.priceAdjustmentService = priceAdjustmentService;
        this.budgetReportService = budgetReportService;
        this.compositionCostService = compositionCostService;
        this.settingsRepository = settingsRepository;
    }

    // --- Stages ---

    @Operation(summary = "Get full worksheet (tree of stages + items with calculated costs)")
    @GetMapping("/worksheet")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    @Transactional(readOnly = true)
    WorksheetResponse getWorksheet(@PathVariable UUID budgetId) {
        var stages = stageRepository.findRootStages(budgetId);
        var bdi = bdiConfigRepository.findByBudgetId(budgetId).map(BdiConfig::getTotalBdi).orElse(BigDecimal.ZERO);
        var directCost = itemRepository.sumDirectCostByBudget(budgetId);
        var bdiAmount = directCost.multiply(bdi).setScale(2, java.math.RoundingMode.HALF_UP);
        var total = directCost.add(bdiAmount);
        return new WorksheetResponse(
                stages.stream().map(this::toStageNode).toList(),
                directCost, bdi, bdiAmount, total
        );
    }

    private WorksheetResponse.StageNode toStageNode(BudgetStage stage) {
        var items = stage.getItems().stream().map(i -> new WorksheetResponse.ItemNode(
                i.getId(), i.getComposition().getSinapiCode(), i.getComposition().getDescription(),
                i.getComposition().getUnit(), i.getQuantity(), i.getUnitCost(), i.getDirectCost(),
                i.getComposition().getOrigin()
        )).toList();
        var children = stage.getChildren().stream().map(this::toStageNode).toList();
        var stageTotal = stage.getItems().stream()
                .map(BudgetItem::getDirectCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new WorksheetResponse.StageNode(stage.getId(), stage.getName(), stage.getSortOrder(), items, children, stageTotal);
    }

    record WorksheetResponse(
            List<StageNode> stages, BigDecimal directCost, BigDecimal bdiPct, BigDecimal bdiAmount, BigDecimal total) {
        record StageNode(UUID id, String name, int sortOrder, List<ItemNode> items, List<StageNode> children, BigDecimal subtotal) {}
        record ItemNode(UUID id, String code, String description, String unit, BigDecimal quantity, BigDecimal unitCost, BigDecimal totalCost, String origin) {}
    }

    @Operation(summary = "List root stages of a budget")
    @GetMapping("/stages")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<StageResponse> listStages(@PathVariable UUID budgetId) {
        return stageRepository.findRootStages(budgetId).stream().map(StageResponse::from).toList();
    }

    @Operation(summary = "Create a stage")
    @PostMapping("/stages")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<StageResponse> createStage(@PathVariable UUID budgetId, @Valid @RequestBody CreateStageRequest req) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        BudgetStage parent = req.parentId() != null ? stageRepository.findById(req.parentId()).orElse(null) : null;
        BudgetStage stage = stageRepository.save(new BudgetStage(budget, parent, req.name(), req.sortOrder()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/stages/" + stage.getId()))
                .body(StageResponse.from(stage));
    }

    @Operation(summary = "Delete a stage")
    @DeleteMapping("/stages/{stageId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteStage(@PathVariable UUID budgetId, @PathVariable UUID stageId) {
        stageRepository.deleteById(stageId);
    }

    // --- Items ---

    @Operation(summary = "List items of a stage")
    @GetMapping("/stages/{stageId}/items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<ItemResponse> listItems(@PathVariable UUID budgetId, @PathVariable UUID stageId) {
        return itemRepository.findByStageId(stageId).stream().map(ItemResponse::from).toList();
    }

    @Operation(summary = "Add item to a stage (auto-calculates unit cost from SINAPI if not provided)")
    @PostMapping("/stages/{stageId}/items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    ResponseEntity<ItemResponse> createItem(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                            @Valid @RequestBody CreateItemRequest req) {
        BudgetStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new DomainNotFoundException("Stage not found: " + stageId));
        Composition composition = compositionRepository.findById(req.compositionId())
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + req.compositionId()));

        // Auto-calculate unit cost from SINAPI if not provided
        BigDecimal unitCost = req.unitCost();
        if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) == 0) {
            String state = settingsRepository.findById(AppSettings.DEFAULT_STATE).map(s -> s.getValue()).orElse("SP");
            String monthStr = settingsRepository.findById(AppSettings.DEFAULT_REFERENCE_MONTH).map(s -> s.getValue()).orElse("2024-12-01");
            LocalDate month = LocalDate.parse(monthStr);
            var costResult = compositionCostService.calculateCost(composition.getId(), state, month);
            unitCost = costResult.totalUnitCost();
        }

        BigDecimal bdiPct = req.bdiPct() != null ? req.bdiPct() :
                bdiConfigRepository.findByBudgetId(budgetId).map(BdiConfig::getTotalBdi).orElse(BigDecimal.ZERO);

        BudgetItem item = itemRepository.save(new BudgetItem(stage, composition, req.quantity(), unitCost, bdiPct));
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.from(item));
    }

    @Operation(summary = "Delete an item")
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteItem(@PathVariable UUID budgetId, @PathVariable UUID itemId) {
        itemRepository.deleteById(itemId);
    }

    @Operation(summary = "Bulk add items to a stage (fast entry mode)")
    @PostMapping("/stages/{stageId}/items/bulk")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    ResponseEntity<BulkAddResult> bulkAddItems(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                               @Valid @RequestBody List<CreateItemRequest> items) {
        BudgetStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new DomainNotFoundException("Stage not found: " + stageId));
        BigDecimal defaultBdi = bdiConfigRepository.findByBudgetId(budgetId)
                .map(BdiConfig::getTotalBdi).orElse(BigDecimal.ZERO);
        String state = settingsRepository.findById(AppSettings.DEFAULT_STATE).map(s -> s.getValue()).orElse("SP");
        String monthStr = settingsRepository.findById(AppSettings.DEFAULT_REFERENCE_MONTH).map(s -> s.getValue()).orElse("2024-12-01");
        LocalDate month = LocalDate.parse(monthStr);

        int added = 0;
        int skipped = 0;
        for (var req : items) {
            Composition composition = compositionRepository.findById(req.compositionId()).orElse(null);
            if (composition == null) { skipped++; continue; }

            BigDecimal unitCost = req.unitCost();
            if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) == 0) {
                var costResult = compositionCostService.calculateCost(composition.getId(), state, month);
                unitCost = costResult.totalUnitCost();
            }
            BigDecimal bdiPct = req.bdiPct() != null ? req.bdiPct() : defaultBdi;
            itemRepository.save(new BudgetItem(stage, composition, req.quantity(), unitCost, bdiPct));
            added++;
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new BulkAddResult(added, skipped, items.size()));
    }

    // --- BDI ---

    @Operation(summary = "Get or create BDI config for a budget")
    @GetMapping("/bdi")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    BdiResponse getBdi(@PathVariable UUID budgetId) {
        return bdiConfigRepository.findByBudgetId(budgetId)
                .map(BdiResponse::from)
                .orElse(BdiResponse.empty());
    }

    @Operation(summary = "Set BDI config for a budget")
    @PutMapping("/bdi")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    BdiResponse setBdi(@PathVariable UUID budgetId, @Valid @RequestBody BdiRequest req) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        BdiConfig config = bdiConfigRepository.findByBudgetId(budgetId).orElse(null);
        if (config == null) {
            config = new BdiConfig(budget, req.administration(), req.profit(), req.taxes(),
                    req.socialCharges(), req.financialExpenses(), req.risks());
        } else {
            config.update(req.administration(), req.profit(), req.taxes(),
                    req.socialCharges(), req.financialExpenses(), req.risks());
        }
        return BdiResponse.from(bdiConfigRepository.save(config));
    }

    // --- Summary & ABC ---

    @Operation(summary = "Budget cost summary (direct cost + BDI)")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    BudgetCalculationService.BudgetSummary summary(@PathVariable UUID budgetId) {
        return calculationService.calculateSummary(budgetId);
    }

    @Operation(summary = "ABC curve of materials for this budget")
    @GetMapping("/abc-curve")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<AbcCurveService.AbcEntry> abcCurve(@PathVariable UUID budgetId) {
        return abcCurveService.calculateAbcCurve(budgetId);
    }

    @Operation(summary = "ABC curve of services for this budget")
    @GetMapping("/abc-curve/services")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<AbcCurveService.ServiceAbcEntry> serviceAbcCurve(@PathVariable UUID budgetId) {
        return abcCurveService.calculateServiceAbcCurve(budgetId);
    }

    @Operation(summary = "Worksheet report data (structured budget breakdown)")
    @GetMapping("/reports/worksheet")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    BudgetReportService.WorksheetReport worksheetReport(@PathVariable UUID budgetId) {
        return budgetReportService.buildWorksheetReport(budgetId);
    }

    @Operation(summary = "Synthetic worksheet PDF")
    @GetMapping(value = "/reports/worksheet.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<byte[]> worksheetReportPdf(@PathVariable UUID budgetId) {
        byte[] pdf = budgetReportService.generateSyntheticWorksheetPdf(budgetId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=budget-worksheet-" + budgetId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Service ABC curve PDF")
    @GetMapping(value = "/reports/abc-services.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<byte[]> serviceAbcReportPdf(@PathVariable UUID budgetId) {
        byte[] pdf = budgetReportService.generateServiceAbcPdf(budgetId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=budget-abc-services-" + budgetId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // --- Price Adjustment ---

    @Operation(summary = "Adjust prices in batch (by percentage, value, or SINAPI reference)")
    @PostMapping("/price-adjustment")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    PriceAdjustmentService.AdjustmentResult adjustPrices(@PathVariable UUID budgetId,
                                                          @Valid @RequestBody PriceAdjustmentRequest req) {
        return switch (req.type()) {
            case PERCENTAGE -> priceAdjustmentService.adjustByPercentage(budgetId, req.percentage());
            case VALUE -> priceAdjustmentService.adjustByValue(budgetId, req.value());
            case SINAPI -> priceAdjustmentService.adjustBySinapiReference(budgetId, req.state(), req.referenceMonth());
        };
    }

    // --- DTOs ---

    record CreateStageRequest(@NotBlank String name, @NotNull Integer sortOrder, UUID parentId) {}
    record CreateItemRequest(@NotNull UUID compositionId, @NotNull @Positive BigDecimal quantity,
                             BigDecimal unitCost, BigDecimal bdiPct) {}
    record BulkAddResult(int added, int skipped, int total) {}
    record BdiRequest(@NotNull BigDecimal administration, @NotNull BigDecimal profit, @NotNull BigDecimal taxes,
                      @NotNull BigDecimal socialCharges, @NotNull BigDecimal financialExpenses, @NotNull BigDecimal risks) {}
    record PriceAdjustmentRequest(@NotNull PriceAdjustmentService.AdjustmentType type,
                                  BigDecimal percentage, BigDecimal value, String state, LocalDate referenceMonth) {}

    record StageResponse(UUID id, UUID parentId, String name, Integer sortOrder, List<StageResponse> children) {
        static StageResponse from(BudgetStage s) {
            return new StageResponse(s.getId(), s.getParentId(), s.getName(), s.getSortOrder(),
                    s.getChildren().stream().map(StageResponse::from).toList());
        }
    }

    record ItemResponse(UUID id, UUID compositionId, String compositionCode, String compositionDescription,
                        BigDecimal quantity, BigDecimal unitCost, BigDecimal bdiPct, BigDecimal directCost, BigDecimal totalWithBdi) {
        static ItemResponse from(BudgetItem i) {
            return new ItemResponse(i.getId(), i.getComposition().getId(), i.getComposition().getSinapiCode(),
                    i.getComposition().getDescription(), i.getQuantity(), i.getUnitCost(), i.getBdiPct(),
                    i.getDirectCost(), i.getTotalWithBdi());
        }
    }

    record BdiResponse(BigDecimal administration, BigDecimal profit, BigDecimal taxes,
                       BigDecimal socialCharges, BigDecimal financialExpenses, BigDecimal risks, BigDecimal totalBdi) {
        static BdiResponse from(BdiConfig c) {
            return new BdiResponse(c.getAdministration(), c.getProfit(), c.getTaxes(),
                    c.getSocialCharges(), c.getFinancialExpenses(), c.getRisks(), c.getTotalBdi());
        }
        static BdiResponse empty() {
            return new BdiResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}
