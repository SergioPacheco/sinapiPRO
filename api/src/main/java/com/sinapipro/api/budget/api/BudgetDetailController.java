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
        this.compositionCostService = compositionCostService;
        this.settingsRepository = settingsRepository;
    }

    // --- Stages ---

    @Operation(summary = "Get full worksheet (tree of stages + items with calculated costs)")
    @GetMapping("/worksheet")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
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
        BudgetStage parent = req.parentId() != null ? ensureStageInBudget(budgetId, req.parentId()) : null;
        BudgetStage stage = stageRepository.save(new BudgetStage(budget, parent, req.name(), req.sortOrder()));
        return ResponseEntity.created(URI.create("/api/v1/budgets/" + budgetId + "/stages/" + stage.getId()))
                .body(StageResponse.from(stage));
    }

    @Operation(summary = "Delete a stage")
    @DeleteMapping("/stages/{stageId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteStage(@PathVariable UUID budgetId, @PathVariable UUID stageId) {
        ensureStageInBudget(budgetId, stageId);
        stageRepository.deleteById(stageId);
    }

    // --- Items ---

    @Operation(summary = "List items of a stage")
    @GetMapping("/stages/{stageId}/items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<ItemResponse> listItems(@PathVariable UUID budgetId, @PathVariable UUID stageId) {
        ensureStageInBudget(budgetId, stageId);
        return itemRepository.findByStageId(stageId).stream().map(ItemResponse::from).toList();
    }

    @Operation(summary = "Add item to a stage (auto-calculates unit cost from SINAPI if not provided)")
    @PostMapping("/stages/{stageId}/items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    ResponseEntity<ItemResponse> createItem(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                            @Valid @RequestBody CreateItemRequest req) {
        BudgetStage stage = ensureStageInBudget(budgetId, stageId);
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
                bdiConfigRepository.findByBudgetIdAndItemType(budgetId, DEFAULT_BDI_ITEM_TYPE)
                        .map(BdiConfig::getTotalBdi).orElse(BigDecimal.ZERO);

        BudgetItem item = itemRepository.save(new BudgetItem(stage, composition, req.quantity(), unitCost, bdiPct));
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.from(item));
    }

    @Operation(summary = "Delete an item")
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteItem(@PathVariable UUID budgetId, @PathVariable UUID itemId) {
        ensureItemInBudget(budgetId, itemId);
        itemRepository.deleteById(itemId);
    }

    @Operation(summary = "Update custom code (mask) for a budget item")
    @PatchMapping("/items/{itemId}/custom-code")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
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
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
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
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
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
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    ResponseEntity<BulkAddResult> bulkAddItems(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                               @Valid @RequestBody List<CreateItemRequest> items) {
        BudgetStage stage = ensureStageInBudget(budgetId, stageId);
        BigDecimal defaultBdi = bdiConfigRepository.findByBudgetIdAndItemType(budgetId, DEFAULT_BDI_ITEM_TYPE)
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
    BdiResponse getBdi(@PathVariable UUID budgetId,
                       @RequestParam(defaultValue = DEFAULT_BDI_ITEM_TYPE) String itemType) {
        String normalizedItemType = itemType.toUpperCase();
        return bdiConfigRepository.findByBudgetIdAndItemType(budgetId, normalizedItemType)
                .map(BdiResponse::from)
                .orElse(BdiResponse.empty(normalizedItemType));
    }

    @Operation(summary = "Set BDI config for a budget")
    @PutMapping("/bdi")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    BdiResponse setBdi(@PathVariable UUID budgetId, @Valid @RequestBody BdiRequest req) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
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
        return BdiResponse.from(bdiConfigRepository.save(config));
    }

    @Operation(summary = "Set BDI configs in batch (multiple item types at once)")
    @PutMapping("/bdi/batch")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    List<BdiResponse> setBdiBatch(@PathVariable UUID budgetId, @Valid @RequestBody List<BdiRequest> requests) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
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
            results.add(BdiResponse.from(bdiConfigRepository.save(config)));
        }
        return results;
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

    @Operation(summary = "Analytical report PDF — compositions with inputs and coefficients")
    @GetMapping(value = "/reports/analytical.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<byte[]> analyticalReportPdf(@PathVariable UUID budgetId) {
        byte[] pdf = budgetReportService.generateAnalyticalPdf(budgetId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=budget-analytical-" + budgetId + ".pdf")
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
    public UpdateBaseDateResponse updateBaseDate(@PathVariable UUID budgetId, @RequestBody UpdateBaseDateRequest request) {
        var budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));

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

        return new UpdateBaseDateResponse(updated, items.size() - updated, items.size());
    }

    record UpdateBaseDateRequest(LocalDate referenceDate, String state) {}
    record UpdateBaseDateResponse(int updatedPrices, int divergentPrices, int totalItems) {}

    // === Task 6.1: Propostas para pregão ===

    @Operation(summary = "List proposals for a budget")
    @GetMapping("/proposals")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<BudgetProposal> listProposals(@PathVariable UUID budgetId) {
        return proposalRepository.findByBudgetIdOrderByCreatedAtDesc(budgetId);
    }

    @Operation(summary = "Generate a proposal with discount")
    @PostMapping("/proposals")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<BudgetProposal> createProposal(@PathVariable UUID budgetId,
                                                   @Valid @RequestBody CreateProposalRequest req) {
        BigDecimal originalValue = itemRepository.sumDirectCostByBudget(budgetId);
        var proposal = proposalRepository.save(new BudgetProposal(budgetId, req.description(), req.discountPct(), originalValue));
        return ResponseEntity.status(HttpStatus.CREATED).body(proposal);
    }

    @Operation(summary = "Delete a proposal")
    @DeleteMapping("/proposals/{proposalId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProposal(@PathVariable UUID budgetId, @PathVariable UUID proposalId) {
        proposalRepository.deleteById(proposalId);
    }

    record CreateProposalRequest(@NotBlank String description, @NotNull BigDecimal discountPct) {}

    // === Task 6.2: Tags em itens do orçamento ===

    @Operation(summary = "List tags for a budget item")
    @GetMapping("/items/{itemId}/tags")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<BudgetItemTag> listTags(@PathVariable UUID budgetId, @PathVariable UUID itemId) {
        ensureItemInBudget(budgetId, itemId);
        return tagRepository.findByBudgetItemId(itemId);
    }

    @Operation(summary = "Add tag to a budget item")
    @PostMapping("/items/{itemId}/tags")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<BudgetItemTag> addTag(@PathVariable UUID budgetId, @PathVariable UUID itemId,
                                         @Valid @RequestBody AddTagRequest req) {
        ensureItemInBudget(budgetId, itemId);
        var tag = tagRepository.save(new BudgetItemTag(itemId, req.tag(), req.color()));
        return ResponseEntity.status(HttpStatus.CREATED).body(tag);
    }

    @Operation(summary = "Remove tag from a budget item")
    @DeleteMapping("/items/{itemId}/tags/{tagId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
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
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<SocialChargesConfig> listSocialCharges(@PathVariable UUID budgetId) {
        return socialChargesRepository.findByBudgetId(budgetId);
    }

    @Operation(summary = "Set social charges config for a worker type")
    @PutMapping("/social-charges")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
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
}
