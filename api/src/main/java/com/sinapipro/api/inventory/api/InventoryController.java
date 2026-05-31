package com.sinapipro.api.inventory.api;

import com.sinapipro.api.inventory.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Inventory", description = "Stock position, movements and requisitions")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/inventory")
public class InventoryController {

    private final StockItemRepository itemRepository;
    private final StockMovementRepository movementRepository;
    private final StockRequisitionRepository requisitionRepository;

    public InventoryController(StockItemRepository itemRepository, StockMovementRepository movementRepository,
                               StockRequisitionRepository requisitionRepository) {
        this.itemRepository = itemRepository;
        this.movementRepository = movementRepository;
        this.requisitionRepository = requisitionRepository;
    }

    // --- Stock Items ---

    @Operation(summary = "Stock position (all items with current quantity)")
    @GetMapping("/items")
    @PreAuthorize("@perm.check('procurement.read')")
    List<StockItemResponse> stockPosition(@PathVariable UUID projectId) {
        return itemRepository.findByBudgetIdOrderByDescription(projectId).stream().map(StockItemResponse::from).toList();
    }

    @Operation(summary = "Items below minimum stock")
    @GetMapping("/items/below-minimum")
    @PreAuthorize("@perm.check('procurement.read')")
    List<StockItemResponse> belowMinimum(@PathVariable UUID projectId) {
        return itemRepository.findByBudgetIdOrderByDescription(projectId).stream()
                .filter(StockItem::isBelowMinimum).map(StockItemResponse::from).toList();
    }

    @Operation(summary = "Create a stock item")
    @PostMapping("/items")
    @PreAuthorize("@perm.check('procurement.write')")
    ResponseEntity<StockItemResponse> createItem(@PathVariable UUID projectId, @Valid @RequestBody CreateStockItemRequest req) {
        var item = itemRepository.save(new StockItem(projectId, req.description(), req.unit(), req.minQuantity(), req.location()));
        return ResponseEntity.created(URI.create("/api/v1/projects/" + projectId + "/inventory/items/" + item.getId()))
                .body(StockItemResponse.from(item));
    }

    // --- Movements ---

    @Operation(summary = "Record stock entry (IN)")
    @PostMapping("/items/{itemId}/entry")
    @PreAuthorize("@perm.check('procurement.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    MovementResponse recordEntry(@PathVariable UUID projectId, @PathVariable UUID itemId, @Valid @RequestBody MovementRequest req) {
        var item = findItem(itemId);
        item.addQuantity(req.quantity());
        itemRepository.save(item);
        var movement = movementRepository.save(new StockMovement(item, "IN", req.quantity(), req.referenceId(), req.referenceType(), req.notes()));
        return MovementResponse.from(movement);
    }

    @Operation(summary = "Record stock exit (OUT)")
    @PostMapping("/items/{itemId}/exit")
    @PreAuthorize("@perm.check('procurement.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    MovementResponse recordExit(@PathVariable UUID projectId, @PathVariable UUID itemId, @Valid @RequestBody MovementRequest req) {
        var item = findItem(itemId);
        item.removeQuantity(req.quantity());
        itemRepository.save(item);
        var movement = movementRepository.save(new StockMovement(item, "OUT", req.quantity(), req.referenceId(), req.referenceType(), req.notes()));
        return MovementResponse.from(movement);
    }

    @Operation(summary = "Movement history for a stock item")
    @GetMapping("/items/{itemId}/movements")
    @PreAuthorize("@perm.check('procurement.read')")
    List<MovementResponse> movements(@PathVariable UUID projectId, @PathVariable UUID itemId) {
        return movementRepository.findByStockItemIdOrderByMovedAtDesc(itemId).stream().map(MovementResponse::from).toList();
    }

    // --- Requisitions ---

    @Operation(summary = "List requisitions")
    @GetMapping("/requisitions")
    @PreAuthorize("@perm.check('procurement.read')")
    PageResponse<RequisitionResponse> listRequisitions(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(requisitionRepository.findByBudgetId(projectId, pageable).map(RequisitionResponse::from));
    }

    @Operation(summary = "Create a stock requisition")
    @PostMapping("/requisitions")
    @PreAuthorize("@perm.check('procurement.write')")
    @Transactional
    ResponseEntity<RequisitionResponse> createRequisition(@PathVariable UUID projectId, @Valid @RequestBody CreateRequisitionRequest req) {
        var requisition = new StockRequisition(projectId, req.requestedBy(), req.notes());
        for (var ri : req.items()) {
            var item = findItem(ri.stockItemId());
            requisition.getItems().add(new StockRequisitionItem(requisition, item, ri.quantity()));
        }
        var saved = requisitionRepository.save(requisition);
        return ResponseEntity.created(URI.create("/api/v1/projects/" + projectId + "/inventory/requisitions/" + saved.getId()))
                .body(RequisitionResponse.from(saved));
    }

    @Operation(summary = "Approve and deliver a requisition (removes from stock)")
    @PostMapping("/requisitions/{reqId}/deliver")
    @PreAuthorize("@perm.check('procurement.write')")
    @Transactional
    RequisitionResponse deliverRequisition(@PathVariable UUID projectId, @PathVariable UUID reqId) {
        var requisition = requisitionRepository.findById(reqId)
                .orElseThrow(() -> new DomainNotFoundException("Requisition not found: " + reqId));
        requisition.approve();
        for (var ri : requisition.getItems()) {
            var item = ri.getStockItem();
            item.removeQuantity(ri.getQuantity());
            ri.deliver(ri.getQuantity());
            itemRepository.save(item);
            movementRepository.save(new StockMovement(item, "OUT", ri.getQuantity(), requisition.getId(), "REQUISITION", null));
        }
        requisition.deliver();
        return RequisitionResponse.from(requisitionRepository.save(requisition));
    }

    private StockItem findItem(UUID id) {
        return itemRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Stock item not found: " + id));
    }

    // --- DTOs ---
    record CreateStockItemRequest(@NotBlank String description, @NotBlank String unit,
                                  @NotNull BigDecimal minQuantity, String location) {}
    record MovementRequest(@NotNull @Positive BigDecimal quantity, UUID referenceId, String referenceType, String notes) {}
    record CreateRequisitionRequest(@NotBlank String requestedBy, String notes, @NotNull List<RequisitionItemReq> items) {}
    record RequisitionItemReq(@NotNull UUID stockItemId, @NotNull @Positive BigDecimal quantity) {}

    record StockItemResponse(UUID id, String description, String unit, BigDecimal currentQuantity,
                             BigDecimal minQuantity, String location, boolean belowMinimum) {
        static StockItemResponse from(StockItem i) {
            return new StockItemResponse(i.getId(), i.getDescription(), i.getUnit(), i.getCurrentQuantity(),
                    i.getMinQuantity(), i.getLocation(), i.isBelowMinimum());
        }
    }

    record MovementResponse(UUID id, String type, BigDecimal quantity, String referenceType, String notes, String movedAt) {
        static MovementResponse from(StockMovement m) {
            return new MovementResponse(m.getId(), m.getType(), m.getQuantity(), m.getReferenceType(),
                    m.getNotes(), m.getMovedAt() != null ? m.getMovedAt().toString() : null);
        }
    }

    record RequisitionResponse(UUID id, String requestedBy, String status, int itemCount) {
        static RequisitionResponse from(StockRequisition r) {
            return new RequisitionResponse(r.getId(), r.getRequestedBy(), r.getStatus(), r.getItems().size());
        }
    }
}
