package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.application.AbcCurveService;
import com.sinapipro.api.budget.application.BudgetCalculationService;
import com.sinapipro.api.report.BudgetReportService;
import com.sinapipro.api.report.ExcelExportService;
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
import jakarta.validation.constraints.PositiveOrZero;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Budget Detail", description = "Budget stages, items, BDI and ABC curve")
@RestController
@RequestMapping("/api/v1/budgets/{budgetId}")
public class BudgetDetailController {
    private static final String DEFAULT_BDI_ITEM_TYPE = "ALL";

    private final BudgetRepository budgetRepository;
    private final BudgetStageRepository stageRepository;
    private final BudgetItemRepository itemRepository;
    private final BdiConfigRepository bdiConfigRepository;
    private final BudgetItemMemoRepository memoRepository;
    private final BudgetProposalRepository proposalRepository;
    private final BudgetItemTagRepository tagRepository;
    private final SocialChargesConfigRepository socialChargesRepository;
    private final CompositionRepository compositionRepository;
    private final BudgetCalculationService calculationService;
    private final AbcCurveService abcCurveService;
    private final PriceAdjustmentService priceAdjustmentService;
    private final BudgetReportService budgetReportService;
    private final ExcelExportService excelExportService;
    private final CompositionCostService compositionCostService;
    private final AppSettingsRepository settingsRepository;

    public BudgetDetailController(BudgetRepository budgetRepository, BudgetStageRepository stageRepository,
                                  BudgetItemRepository itemRepository, BdiConfigRepository bdiConfigRepository,
                                  BudgetItemMemoRepository memoRepository,
                                  BudgetProposalRepository proposalRepository,
                                  BudgetItemTagRepository tagRepository,
                                  SocialChargesConfigRepository socialChargesRepository,
                                  CompositionRepository compositionRepository,
                                  BudgetCalculationService calculationService, AbcCurveService abcCurveService,
                                  PriceAdjustmentService priceAdjustmentService,
                                  BudgetReportService budgetReportService,
                                  ExcelExportService excelExportService,
                                  CompositionCostService compositionCostService,
                                  AppSettingsRepository settingsRepository) {
        this.budgetRepository = budgetRepository;
        this.stageRepository = stageRepository;
        this.itemRepository = itemRepository;
        this.bdiConfigRepository = bdiConfigRepository;
        this.memoRepository = memoRepository;
        this.proposalRepository = proposalRepository;
        this.tagRepository = tagRepository;
        this.socialChargesRepository = socialChargesRepository;
        this.compositionRepository = compositionRepository;
        this.calculationService = calculationService;
        this.abcCurveService = abcCurveService;
        this.priceAdjustmentService = priceAdjustmentService;
        this.budgetReportService = budgetReportService;
        this.excelExportService = excelExportService;
        this.compositionCostService = compositionCostService;
        this.settingsRepository = settingsRepository;
    }

    // --- Budget Info ---

    @Operation(summary = "Get budget basic info (status, code, title)")
    @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    BudgetResponse getBudget(@PathVariable UUID budgetId) {
        return BudgetResponse.from(budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId)));
    }

    @Operation(summary = "Update budget worksheet settings")
    @PutMapping
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    BudgetResponse updateBudgetSettings(@PathVariable UUID budgetId, @RequestBody BudgetSettingsRequest req) {
        Budget budget = ensureBudgetEditable(budgetId);
        if (req.referenceDate() != null) budget.setReferenceDate(req.referenceDate());
        if (req.state() != null && !req.state().isBlank()) budget.setState(req.state().toUpperCase());
        if (req.roundingMethod() != null && !req.roundingMethod().isBlank()) budget.setRoundingMethod(req.roundingMethod());
        if (req.decimalPlaces() != null) budget.setDecimalPlaces(req.decimalPlaces());
        if (req.itemMask() != null) budget.setItemMask(req.itemMask());
        budgetRepository.save(budget);
        syncBudgetTotal(budgetId);
        return BudgetResponse.from(budgetRepository.findById(budgetId).orElse(budget));
    }

    // --- Stages ---

    @Operation(summary = "Get full worksheet (tree of stages + items with calculated costs)")
    @GetMapping("/worksheet")
    @PreAuthorize("@perm.check('budget.read')")
    @Transactional(readOnly = true)
    WorksheetResponse getWorksheet(@PathVariable UUID budgetId) {
        var budget = budgetRepository.findById(budgetId).orElseThrow();
        String method = budget.getRoundingMethod();
        int decimals = budget.getDecimalPlaces() != null ? budget.getDecimalPlaces() : 4;

        var stages = stageRepository.findRootStages(budgetId);
        var bdi = bdiConfigRepository.findByBudgetIdAndItemType(budgetId, DEFAULT_BDI_ITEM_TYPE)
                .map(BdiConfig::getTotalBdi)
                .orElse(BigDecimal.ZERO);
        var directCost = itemRepository.sumDirectCostByBudget(budgetId);
        var bdiAmount = com.sinapipro.api.budget.application.RoundingUtil.apply(
                directCost.multiply(bdi), method, decimals);
        var total = directCost.add(bdiAmount);
        return new WorksheetResponse(
                stages.stream().map(this::toStageNode).toList(),
                directCost, bdi, bdiAmount, total
        );
    }

    private WorksheetResponse.StageNode toStageNode(BudgetStage stage) {
        var items = stage.getItems().stream().map(i -> new WorksheetResponse.ItemNode(
                i.getId(), i.getComposition().getId(), i.getComposition().getSinapiCode(), i.getComposition().getDescription(),
                i.getComposition().getUnit(), i.getQuantity(), i.getUnitCost(), i.getDirectCost(),
                i.getBdiPct(), i.getComposition().getOrigin()
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
        record ItemNode(UUID id, UUID compositionId, String code, String description, String unit, BigDecimal quantity,
                        BigDecimal unitCost, BigDecimal totalCost, BigDecimal bdiPct, String origin) {}
    }

    @Operation(summary = "List root stages of a budget")
    @GetMapping("/stages")
    @PreAuthorize("@perm.check('budget.read')")
    List<StageResponse> listStages(@PathVariable UUID budgetId) {
        return stageRepository.findRootStages(budgetId).stream().map(StageResponse::from).toList();
    }

    @Operation(summary = "Create a stage")
    @PostMapping("/stages")
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<StageResponse> createStage(@PathVariable UUID budgetId, @Valid @RequestBody CreateStageRequest req) {
        Budget budget = ensureBudgetEditable(budgetId);
        BudgetStage parent = req.parentId() != null ? ensureStageInBudget(budgetId, req.parentId()) : null;
        BudgetStage stage = stageRepository.save(new BudgetStage(budget, parent, req.name(), req.sortOrder()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/stages/" + stage.getId()))
                .body(StageResponse.from(stage));
    }

    @Operation(summary = "Delete a stage")
    @DeleteMapping("/stages/{stageId}")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteStage(@PathVariable UUID budgetId, @PathVariable UUID stageId) {
        ensureBudgetEditable(budgetId);
        ensureStageInBudget(budgetId, stageId);
        stageRepository.deleteById(stageId);
        syncBudgetTotal(budgetId);
    }

    // --- Items ---

    @Operation(summary = "List items of a stage")
    @GetMapping("/stages/{stageId}/items")
    @PreAuthorize("@perm.check('budget.read')")
    List<ItemResponse> listItems(@PathVariable UUID budgetId, @PathVariable UUID stageId) {
        ensureStageInBudget(budgetId, stageId);
        return itemRepository.findByStageId(stageId).stream().map(ItemResponse::from).toList();
    }

    @Operation(summary = "Add item to a stage (auto-calculates unit cost from SINAPI if not provided)")
    @PostMapping("/stages/{stageId}/items")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    ResponseEntity<ItemResponse> createItem(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                            @Valid @RequestBody CreateItemRequest req) {
        Budget budget = ensureBudgetEditable(budgetId);
        BudgetStage stage = ensureStageInBudget(budgetId, stageId);
        Composition composition = compositionRepository.findById(req.compositionId())
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + req.compositionId()));

        // Auto-calculate unit cost from SINAPI if not provided
        BigDecimal unitCost = req.unitCost();
        if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) == 0) {
            String state = resolveBudgetState(budget);
            LocalDate month = resolveBudgetReferenceDate(budget);
            var costResult = compositionCostService.calculateCost(composition.getId(), state, month);
            unitCost = costResult.totalUnitCost();
        }

        BigDecimal bdiPct = req.bdiPct() != null ? req.bdiPct() :
                bdiConfigRepository.findByBudgetIdAndItemType(budgetId, DEFAULT_BDI_ITEM_TYPE)
                        .map(BdiConfig::getTotalBdi).orElse(BigDecimal.ZERO);

        BudgetItem item = itemRepository.save(new BudgetItem(stage, composition, req.quantity(), unitCost, bdiPct));
        syncBudgetTotal(budgetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.from(item));
    }

    @Operation(summary = "Update quantity, unit cost and BDI of a budget item")
    @PutMapping("/items/{itemId}")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    ItemResponse updateItem(@PathVariable UUID budgetId, @PathVariable UUID itemId,
                            @Valid @RequestBody UpdateItemRequest req) {
        ensureBudgetEditable(budgetId);
        ensureItemInBudget(budgetId, itemId);
        var item = itemRepository.findById(itemId).orElseThrow();
        BigDecimal bdiPct = req.bdiPct() != null ? req.bdiPct() : item.getBdiPct();
        item.update(req.quantity(), req.unitCost(), bdiPct);
        var saved = itemRepository.save(item);
        syncBudgetTotal(budgetId);
        return ItemResponse.from(saved);
    }

    @Operation(summary = "Delete an item")
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteItem(@PathVariable UUID budgetId, @PathVariable UUID itemId) {
        ensureBudgetEditable(budgetId);
        ensureItemInBudget(budgetId, itemId);
        itemRepository.deleteById(itemId);
        syncBudgetTotal(budgetId);
    }

    @Operation(summary = "Update custom code (mask) for a budget item")
    @PatchMapping("/items/{itemId}/custom-code")
    @PreAuthorize("@perm.check('budget.write')")
    ItemResponse updateCustomCode(@PathVariable UUID budgetId, @PathVariable UUID itemId,
                                  @RequestBody CustomCodeRequest req) {
        ensureItemInBudget(budgetId, itemId);
        var item = itemRepository.findById(itemId).orElseThrow();
        // Validate against budget mask if configured
        var budget = budgetRepository.findById(budgetId).orElseThrow();
        if (req.customCode() != null && budget.getItemMask() != null && !budget.getItemMask().isBlank()) {
            String regex = budget.getItemMask()
                    .replace("#", "\\d")
                    .replace("A", "[A-Za-z]")
                    .replace(".", "\\.");
            if (!req.customCode().matches(regex)) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Custom code '" + req.customCode() + "' does not match mask '" + budget.getItemMask() + "'");
            }
        }
        item.setCustomCode(req.customCode());
        return ItemResponse.from(itemRepository.save(item));
    }

    record CustomCodeRequest(String customCode) {}

    @Operation(summary = "Get memo for a budget item")
    @GetMapping("/items/{itemId}/memo")
    @PreAuthorize("@perm.check('budget.read')")
    ResponseEntity<ItemMemoResponse> getItemMemo(@PathVariable UUID budgetId, @PathVariable UUID itemId) {
        ensureItemInBudget(budgetId, itemId);
        return memoRepository.findByBudgetItemId(itemId)
                .map(memo -> ResponseEntity.ok(new ItemMemoResponse(
                        memo.getBudgetItemId(),
                        memo.getLines().stream().map(l -> new MemoLineRequest(l.description(), l.formula(), l.value())).toList(),
                        memo.getResult(),
                        memo.getNotes()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Save memo for a budget item")
    @PutMapping("/items/{itemId}/memo")
    @PreAuthorize("@perm.check('budget.write')")
    ItemMemoResponse saveItemMemo(@PathVariable UUID budgetId, @PathVariable UUID itemId,
                                  @Valid @RequestBody ItemMemoRequest req) {
        ensureItemInBudget(budgetId, itemId);
        BudgetItemMemo memo = memoRepository.findByBudgetItemId(itemId).orElseGet(BudgetItemMemo::new);
        memo.setBudgetItemId(itemId);
        memo.setLines(req.lines().stream()
                .map(l -> new BudgetItemMemo.MemoLine(l.description(), l.formula(), l.value()))
                .toList());
        BigDecimal result = req.result() != null
                ? req.result()
                : req.lines().stream().map(MemoLineRequest::value).reduce(BigDecimal.ZERO, BigDecimal::add);
        memo.setResult(result);
        memo.setNotes(req.notes());
        BudgetItemMemo saved = memoRepository.save(memo);
        return new ItemMemoResponse(
                saved.getBudgetItemId(),
                saved.getLines().stream().map(l -> new MemoLineRequest(l.description(), l.formula(), l.value())).toList(),
                saved.getResult(),
                saved.getNotes()
        );
    }

    @Operation(summary = "Bulk add items to a stage (fast entry mode)")
    @PostMapping("/stages/{stageId}/items/bulk")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    ResponseEntity<BulkAddResult> bulkAddItems(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                               @Valid @RequestBody List<CreateItemRequest> items) {
        Budget budget = ensureBudgetEditable(budgetId);
        BudgetStage stage = ensureStageInBudget(budgetId, stageId);
        BigDecimal defaultBdi = bdiConfigRepository.findByBudgetIdAndItemType(budgetId, DEFAULT_BDI_ITEM_TYPE)
                .map(BdiConfig::getTotalBdi).orElse(BigDecimal.ZERO);
        String state = resolveBudgetState(budget);
        LocalDate month = resolveBudgetReferenceDate(budget);

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
        syncBudgetTotal(budgetId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BulkAddResult(added, skipped, items.size()));
    }

    // --- BDI ---

    @Operation(summary = "Get or create BDI config for a budget")
    @GetMapping("/bdi")
    @PreAuthorize("@perm.check('budget.read')")
    BdiResponse getBdi(@PathVariable UUID budgetId,
                       @RequestParam(defaultValue = DEFAULT_BDI_ITEM_TYPE) String itemType) {
        String normalizedItemType = itemType.toUpperCase();
        return bdiConfigRepository.findByBudgetIdAndItemType(budgetId, normalizedItemType)
                .map(BdiResponse::from)
                .orElse(BdiResponse.empty(normalizedItemType));
    }

    @Operation(summary = "Set BDI config for a budget")
    @PutMapping("/bdi")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    BdiResponse setBdi(@PathVariable UUID budgetId, @Valid @RequestBody BdiRequest req) {
        Budget budget = ensureBudgetEditable(budgetId);
        String itemType = req.itemType() != null ? req.itemType().toUpperCase() : DEFAULT_BDI_ITEM_TYPE;
        BdiConfig config = bdiConfigRepository.findByBudgetIdAndItemType(budgetId, itemType).orElse(null);
        if (config == null) {
            config = new BdiConfig(budget, req.administration(), req.profit(), req.taxes(),
                    req.socialCharges(), req.financialExpenses(), req.risks());
            config.setItemType(itemType);
        } else {
            config.update(req.administration(), req.profit(), req.taxes(),
                    req.socialCharges(), req.financialExpenses(), req.risks());
        }
        var saved = bdiConfigRepository.save(config);
        if (DEFAULT_BDI_ITEM_TYPE.equals(itemType)) {
            applyBdiToItems(budgetId, saved.getTotalBdi());
        }
        syncBudgetTotal(budgetId);
        return BdiResponse.from(saved);
    }

    @Operation(summary = "Set BDI configs in batch (multiple item types at once)")
    @PutMapping("/bdi/batch")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    List<BdiResponse> setBdiBatch(@PathVariable UUID budgetId, @Valid @RequestBody List<BdiRequest> requests) {
        Budget budget = ensureBudgetEditable(budgetId);
        List<BdiResponse> results = new java.util.ArrayList<>();
        for (var req : requests) {
            String itemType = req.itemType() != null ? req.itemType().toUpperCase() : DEFAULT_BDI_ITEM_TYPE;
            BdiConfig config = bdiConfigRepository.findByBudgetIdAndItemType(budgetId, itemType).orElse(null);
            if (config == null) {
                config = new BdiConfig(budget, req.administration(), req.profit(), req.taxes(),
                        req.socialCharges(), req.financialExpenses(), req.risks());
                config.setItemType(itemType);
            } else {
                config.update(req.administration(), req.profit(), req.taxes(),
                        req.socialCharges(), req.financialExpenses(), req.risks());
            }
            var saved = bdiConfigRepository.save(config);
            if (DEFAULT_BDI_ITEM_TYPE.equals(itemType)) {
                applyBdiToItems(budgetId, saved.getTotalBdi());
            }
            results.add(BdiResponse.from(saved));
        }
        syncBudgetTotal(budgetId);
        return results;
    }

    // --- Summary & ABC ---

    @Operation(summary = "Budget cost summary (direct cost + BDI)")
    @GetMapping("/summary")
    @PreAuthorize("@perm.check('budget.read')")
    BudgetCalculationService.BudgetSummary summary(@PathVariable UUID budgetId) {
        return calculationService.calculateSummary(budgetId);
    }

    @Operation(summary = "ABC curve of materials for this budget")
    @GetMapping("/abc-curve")
    @PreAuthorize("@perm.check('budget.read')")
    List<AbcCurveService.AbcEntry> abcCurve(@PathVariable UUID budgetId) {
        return abcCurveService.calculateAbcCurve(budgetId);
    }

    @Operation(summary = "ABC curve of services for this budget")
    @GetMapping("/abc-curve/services")
    @PreAuthorize("@perm.check('budget.read')")
    List<AbcCurveService.ServiceAbcEntry> serviceAbcCurve(@PathVariable UUID budgetId) {
        return abcCurveService.calculateServiceAbcCurve(budgetId);
    }

    @Operation(summary = "Worksheet report data (structured budget breakdown)")
    @GetMapping("/reports/worksheet")
    @PreAuthorize("@perm.check('budget.read')")
    Object worksheetReport(@PathVariable UUID budgetId) {
        return null;
    }

    @Operation(summary = "Synthetic worksheet PDF")
    @GetMapping(value = "/reports/worksheet.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("@perm.check('budget.read')")
    ResponseEntity<byte[]> worksheetReportPdf(@PathVariable UUID budgetId) {
        byte[] pdf = budgetReportService.sintetico(budgetId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=budget-worksheet-" + budgetId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Service ABC curve PDF")
    @GetMapping(value = "/reports/abc-services.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("@perm.check('budget.read')")
    ResponseEntity<byte[]> serviceAbcReportPdf(@PathVariable UUID budgetId) {
        byte[] pdf = budgetReportService.cpu(budgetId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=budget-abc-services-" + budgetId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Analytical report PDF — compositions with inputs and coefficients")
    @GetMapping(value = "/reports/analytical.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("@perm.check('budget.read')")
    ResponseEntity<byte[]> analyticalReportPdf(@PathVariable UUID budgetId) {
        byte[] pdf = budgetReportService.analitico(budgetId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=budget-analytical-" + budgetId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @Operation(summary = "Synthetic worksheet Excel")
    @GetMapping(value = "/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("@perm.check('budget.read')")
    @Transactional(readOnly = true)
    ResponseEntity<byte[]> exportExcel(@PathVariable UUID budgetId) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        List<String> headers = List.of("Item", "Codigo", "Descricao", "Un", "Quantidade",
                "Valor Unit.", "BDI %", "Total Direto", "Total c/ BDI");
        List<Map<String, Object>> rows = new ArrayList<>();
        appendStageRows(rows, stageRepository.findRootStages(budgetId), "", 0);
        byte[] excel = excelExportService.export("orcamento", headers, rows);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamento-" + budget.getCode() + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    // --- Price Adjustment ---

    @Operation(summary = "Adjust prices in batch (by percentage, value, or SINAPI reference)")
    @PostMapping("/price-adjustment")
    @PreAuthorize("@perm.check('budget.write')")
    PriceAdjustmentService.AdjustmentResult adjustPrices(@PathVariable UUID budgetId,
                                                          @Valid @RequestBody PriceAdjustmentRequest req) {
        ensureBudgetEditable(budgetId);
        var result = switch (req.type()) {
            case PERCENTAGE -> priceAdjustmentService.adjustByPercentage(budgetId, req.percentage());
            case VALUE -> priceAdjustmentService.adjustByValue(budgetId, req.value());
            case SINAPI -> priceAdjustmentService.adjustBySinapiReference(budgetId, req.state(), req.referenceMonth());
        };
        syncBudgetTotal(budgetId);
        return result;
    }

    // --- DTOs ---

    record CreateStageRequest(@NotBlank String name, @NotNull Integer sortOrder, UUID parentId) {}
    record BudgetSettingsRequest(LocalDate referenceDate, String state, String roundingMethod,
                                 Integer decimalPlaces, String itemMask) {}
    record CreateItemRequest(@NotNull UUID compositionId, @NotNull @Positive BigDecimal quantity,
                             BigDecimal unitCost, BigDecimal bdiPct) {}
    record UpdateItemRequest(@NotNull @Positive BigDecimal quantity, @NotNull @PositiveOrZero BigDecimal unitCost,
                             BigDecimal bdiPct) {}
    record BulkAddResult(int added, int skipped, int total) {}
    record BdiRequest(String itemType, @NotNull BigDecimal administration, @NotNull BigDecimal profit, @NotNull BigDecimal taxes,
                      @NotNull BigDecimal socialCharges, @NotNull BigDecimal financialExpenses, @NotNull BigDecimal risks) {}
    record PriceAdjustmentRequest(@NotNull PriceAdjustmentService.AdjustmentType type,
                                  BigDecimal percentage, BigDecimal value, String state, LocalDate referenceMonth) {}
    record MemoLineRequest(@NotBlank String description, @NotBlank String formula, @NotNull BigDecimal value) {}
    record ItemMemoRequest(@NotNull List<MemoLineRequest> lines, BigDecimal result, String notes) {}
    record ItemMemoResponse(UUID budgetItemId, List<MemoLineRequest> lines, BigDecimal result, String notes) {}

    record StageResponse(UUID id, UUID parentId, String name, Integer sortOrder, List<StageResponse> children) {
        static StageResponse from(BudgetStage s) {
            return new StageResponse(s.getId(), s.getParentId(), s.getName(), s.getSortOrder(),
                    s.getChildren().stream().map(StageResponse::from).toList());
        }
    }

    record ItemResponse(UUID id, UUID compositionId, String compositionCode, String compositionDescription,
                        BigDecimal quantity, BigDecimal unitCost, BigDecimal bdiPct, BigDecimal directCost,
                        BigDecimal totalWithBdi, String customCode) {
        static ItemResponse from(BudgetItem i) {
            return new ItemResponse(i.getId(), i.getComposition().getId(), i.getComposition().getSinapiCode(),
                    i.getComposition().getDescription(), i.getQuantity(), i.getUnitCost(), i.getBdiPct(),
                    i.getDirectCost(), i.getTotalWithBdi(), i.getCustomCode());
        }
    }

    record BdiResponse(String itemType, BigDecimal administration, BigDecimal profit, BigDecimal taxes,
                       BigDecimal socialCharges, BigDecimal financialExpenses, BigDecimal risks, BigDecimal totalBdi) {
        static BdiResponse from(BdiConfig c) {
            return new BdiResponse(c.getItemType(), c.getAdministration(), c.getProfit(), c.getTaxes(),
                    c.getSocialCharges(), c.getFinancialExpenses(), c.getRisks(), c.getTotalBdi());
        }
        static BdiResponse empty(String itemType) {
            return new BdiResponse(itemType, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }

    // === Task 1.1: Atualização de Data Base ===

    @Operation(summary = "Update reference date — recalculates all prices from material_price table")
    @PostMapping("/update-base-date")
    @PreAuthorize("@perm.check('budget.write')")
    @Transactional
    public UpdateBaseDateResponse updateBaseDate(@PathVariable UUID budgetId, @RequestBody UpdateBaseDateRequest request) {
        var budget = ensureBudgetEditable(budgetId);

        budget.setReferenceDate(request.referenceDate());
        budget.setState(request.state());
        budgetRepository.save(budget);

        // Recalculate all item costs with new reference date
        var items = itemRepository.findAllByBudgetId(budgetId);
        int updated = 0;
        for (var item : items) {
            try {
                var result = compositionCostService.calculateCost(item.getComposition().getId(), request.state(), request.referenceDate());
                if (result.totalUnitCost() != null && result.totalUnitCost().compareTo(BigDecimal.ZERO) > 0) {
                    item.update(item.getQuantity(), result.totalUnitCost(), item.getBdiPct());
                    itemRepository.save(item);
                    updated++;
                }
            } catch (Exception e) {
                // Price not found for this composition — divergent
            }
        }

        syncBudgetTotal(budgetId);
        return new UpdateBaseDateResponse(updated, items.size() - updated, items.size());
    }

    record UpdateBaseDateRequest(LocalDate referenceDate, String state) {}
    record UpdateBaseDateResponse(int updatedPrices, int divergentPrices, int totalItems) {}

    // === Task 6.1: Propostas para pregão ===

    @Operation(summary = "List proposals for a budget")
    @GetMapping("/proposals")
    @PreAuthorize("@perm.check('budget.read')")
    List<BudgetProposal> listProposals(@PathVariable UUID budgetId) {
        return proposalRepository.findByBudgetIdOrderByCreatedAtDesc(budgetId);
    }

    @Operation(summary = "Generate a proposal with discount")
    @PostMapping("/proposals")
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<BudgetProposal> createProposal(@PathVariable UUID budgetId,
                                                   @Valid @RequestBody CreateProposalRequest req) {
        BigDecimal originalValue = itemRepository.sumDirectCostByBudget(budgetId);
        var proposal = proposalRepository.save(new BudgetProposal(budgetId, req.description(), req.discountPct(), originalValue));
        return ResponseEntity.status(HttpStatus.CREATED).body(proposal);
    }

    @Operation(summary = "Delete a proposal")
    @DeleteMapping("/proposals/{proposalId}")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProposal(@PathVariable UUID budgetId, @PathVariable UUID proposalId) {
        proposalRepository.deleteById(proposalId);
    }

    record CreateProposalRequest(@NotBlank String description, @NotNull BigDecimal discountPct) {}

    // === Task 6.2: Tags em itens do orçamento ===

    @Operation(summary = "List tags for a budget item")
    @GetMapping("/items/{itemId}/tags")
    @PreAuthorize("@perm.check('budget.read')")
    List<BudgetItemTag> listTags(@PathVariable UUID budgetId, @PathVariable UUID itemId) {
        ensureItemInBudget(budgetId, itemId);
        return tagRepository.findByBudgetItemId(itemId);
    }

    @Operation(summary = "Add tag to a budget item")
    @PostMapping("/items/{itemId}/tags")
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<BudgetItemTag> addTag(@PathVariable UUID budgetId, @PathVariable UUID itemId,
                                         @Valid @RequestBody AddTagRequest req) {
        ensureItemInBudget(budgetId, itemId);
        var tag = tagRepository.save(new BudgetItemTag(itemId, req.tag(), req.color()));
        return ResponseEntity.status(HttpStatus.CREATED).body(tag);
    }

    @Operation(summary = "Remove tag from a budget item")
    @DeleteMapping("/items/{itemId}/tags/{tagId}")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    void removeTag(@PathVariable UUID budgetId, @PathVariable UUID itemId, @PathVariable UUID tagId) {
        ensureItemInBudget(budgetId, itemId);
        tagRepository.deleteById(tagId);
    }

    record AddTagRequest(@NotBlank String tag, String color) {}

    // === Task 6.5: Encargos sociais configuráveis ===

    @Operation(summary = "List social charges configs for a budget")
    @GetMapping("/social-charges")
    @PreAuthorize("@perm.check('budget.read')")
    List<SocialChargesConfig> listSocialCharges(@PathVariable UUID budgetId) {
        return socialChargesRepository.findByBudgetId(budgetId);
    }

    @Operation(summary = "Set social charges config for a worker type")
    @PutMapping("/social-charges")
    @PreAuthorize("@perm.check('budget.write')")
    SocialChargesConfig setSocialCharges(@PathVariable UUID budgetId, @Valid @RequestBody SocialChargesRequest req) {
        var config = socialChargesRepository.findByBudgetIdAndWorkerType(budgetId, req.workerType()).orElseGet(SocialChargesConfig::new);
        config.setBudgetId(budgetId);
        config.setWorkerType(req.workerType());
        config.setTaxRegime(req.taxRegime() != null ? req.taxRegime() : "NORMAL");
        config.setInssPct(req.inssPct());
        config.setFgtsPct(req.fgtsPct());
        config.setVacationPct(req.vacationPct());
        config.setThirteenthPct(req.thirteenthPct());
        config.setOtherPct(req.otherPct());
        return socialChargesRepository.save(config);
    }

    record SocialChargesRequest(@NotBlank String workerType, String taxRegime,
                                @NotNull BigDecimal inssPct, @NotNull BigDecimal fgtsPct,
                                @NotNull BigDecimal vacationPct, @NotNull BigDecimal thirteenthPct,
                                @NotNull BigDecimal otherPct) {}

    private void ensureItemInBudget(UUID budgetId, UUID itemId) {
        BudgetItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DomainNotFoundException("Budget item not found: " + itemId));
        if (!budgetId.equals(item.getStage().getBudget().getId())) {
            throw new DomainNotFoundException("Budget item not found in budget: " + itemId);
        }
    }

    private BudgetStage ensureStageInBudget(UUID budgetId, UUID stageId) {
        BudgetStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new DomainNotFoundException("Stage not found: " + stageId));
        if (!budgetId.equals(stage.getBudget().getId())) {
            throw new DomainNotFoundException("Stage not found in budget: " + stageId);
        }
        return stage;
    }

    private Budget ensureBudgetEditable(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        if (budget.getStatus() == BudgetStatus.IN_EXECUTION
                || budget.getStatus() == BudgetStatus.COMPLETED
                || budget.getStatus() == BudgetStatus.CANCELLED
                || budget.getStatus() == BudgetStatus.SUPERSEDED) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Budget is locked for editing: " + budget.getStatus());
        }
        return budget;
    }

    private void syncBudgetTotal(UUID budgetId) {
        var budget = budgetRepository.findById(budgetId).orElse(null);
        if (budget == null) {
            return;
        }
        var summary = calculationService.calculateSummary(budgetId);
        if (summary != null) {
            budget.setTotalAmount(summary.totalWithBdi());
            budgetRepository.save(budget);
        }
    }

    private void applyBdiToItems(UUID budgetId, BigDecimal bdiPct) {
        itemRepository.findAllByBudgetId(budgetId).forEach(item -> {
            item.update(item.getQuantity(), item.getUnitCost(), bdiPct);
            itemRepository.save(item);
        });
    }

    private String resolveBudgetState(Budget budget) {
        if (budget.getState() != null && !budget.getState().isBlank()) {
            return budget.getState();
        }
        return settingsRepository.findById(AppSettings.DEFAULT_STATE).map(s -> s.getValue()).orElse("SP");
    }

    private LocalDate resolveBudgetReferenceDate(Budget budget) {
        if (budget.getReferenceDate() != null) {
            return budget.getReferenceDate();
        }
        String monthStr = settingsRepository.findById(AppSettings.DEFAULT_REFERENCE_MONTH)
                .map(s -> s.getValue()).orElse("2024-12-01");
        return LocalDate.parse(monthStr);
    }

    private void appendStageRows(List<Map<String, Object>> rows, List<BudgetStage> stages, String prefix, int level) {
        for (int index = 0; index < stages.size(); index++) {
            var stage = stages.get(index);
            String stageCode = prefix + String.format("%02d.", index + 1);
            var stageRow = new LinkedHashMap<String, Object>();
            stageRow.put("Item", stageCode);
            stageRow.put("Codigo", "");
            stageRow.put("Descricao", stage.getName());
            stageRow.put("Un", "");
            stageRow.put("Quantidade", "");
            stageRow.put("Valor Unit.", "");
            stageRow.put("BDI %", "");
            stageRow.put("Total Direto", stage.getItems().stream()
                    .map(BudgetItem::getDirectCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            stageRow.put("Total c/ BDI", stage.getItems().stream()
                    .map(BudgetItem::getTotalWithBdi)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            rows.add(stageRow);

            int itemIndex = 1;
            for (var item : stage.getItems()) {
                var itemRow = new LinkedHashMap<String, Object>();
                itemRow.put("Item", stageCode + String.format("%03d", itemIndex++));
                itemRow.put("Codigo", item.getComposition().getSinapiCode());
                itemRow.put("Descricao", item.getComposition().getDescription());
                itemRow.put("Un", item.getComposition().getUnit());
                itemRow.put("Quantidade", item.getQuantity());
                itemRow.put("Valor Unit.", item.getUnitCost());
                itemRow.put("BDI %", item.getBdiPct().multiply(new BigDecimal("100")));
                itemRow.put("Total Direto", item.getDirectCost());
                itemRow.put("Total c/ BDI", item.getTotalWithBdi());
                rows.add(itemRow);
            }
            appendStageRows(rows, stage.getChildren(), stageCode, level + 1);
        }
    }
}
