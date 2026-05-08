package com.sinapipro.api.budget.api;

import com.sinapipro.api.budget.application.AbcCurveService;
import com.sinapipro.api.budget.application.BudgetCalculationService;
import com.sinapipro.api.budget.application.PriceAdjustmentService;
import com.sinapipro.api.budget.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.domain.Composition;
import com.sinapipro.api.sinapi.domain.CompositionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public BudgetDetailController(BudgetRepository budgetRepository, BudgetStageRepository stageRepository,
                                  BudgetItemRepository itemRepository, BdiConfigRepository bdiConfigRepository,
                                  CompositionRepository compositionRepository,
                                  BudgetCalculationService calculationService, AbcCurveService abcCurveService,
                                  PriceAdjustmentService priceAdjustmentService) {
        this.budgetRepository = budgetRepository;
        this.stageRepository = stageRepository;
        this.itemRepository = itemRepository;
        this.bdiConfigRepository = bdiConfigRepository;
        this.compositionRepository = compositionRepository;
        this.calculationService = calculationService;
        this.abcCurveService = abcCurveService;
        this.priceAdjustmentService = priceAdjustmentService;
    }

    // --- Stages ---

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

    @Operation(summary = "Add item to a stage")
    @PostMapping("/stages/{stageId}/items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<ItemResponse> createItem(@PathVariable UUID budgetId, @PathVariable UUID stageId,
                                            @Valid @RequestBody CreateItemRequest req) {
        BudgetStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new DomainNotFoundException("Stage not found: " + stageId));
        Composition composition = compositionRepository.findById(req.compositionId())
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + req.compositionId()));
        BudgetItem item = itemRepository.save(new BudgetItem(stage, composition, req.quantity(), req.unitCost(), req.bdiPct()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.from(item));
    }

    @Operation(summary = "Delete an item")
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteItem(@PathVariable UUID budgetId, @PathVariable UUID itemId) {
        itemRepository.deleteById(itemId);
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
                             @NotNull @Positive BigDecimal unitCost, @NotNull BigDecimal bdiPct) {}
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
