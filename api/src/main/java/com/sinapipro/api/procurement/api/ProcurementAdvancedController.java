package com.sinapipro.api.procurement.api;

import com.sinapipro.api.procurement.application.*;
import com.sinapipro.api.procurement.application.PurchaseBudgetLimitService.LimitCheckResult;
import com.sinapipro.api.procurement.application.PurchaseOrderItemService.ItemEntry;
import com.sinapipro.api.procurement.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Procurement Advanced", description = "Limites, cronograma, multi-item, recebimento parcial")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/procurement")
@PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
public class ProcurementAdvancedController {

    private final PurchaseBudgetLimitService limitService;
    private final ProcurementScheduleService scheduleService;
    private final PurchaseOrderItemService itemService;

    public ProcurementAdvancedController(PurchaseBudgetLimitService limitService,
                                          ProcurementScheduleService scheduleService,
                                          PurchaseOrderItemService itemService) {
        this.limitService = limitService;
        this.scheduleService = scheduleService;
        this.itemService = itemService;
    }

    // --- Budget Limits ---

    @Operation(summary = "Check purchase limit for project")
    @GetMapping("/limits/check")
    LimitCheckResult checkLimit(@PathVariable UUID projectId, @RequestParam BigDecimal amount) {
        return limitService.checkLimit(projectId, amount);
    }

    @Operation(summary = "Create budget limit for project")
    @PostMapping("/limits")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    LimitResponse createLimit(@PathVariable UUID projectId, @Valid @RequestBody CreateLimitRequest req) {
        var limit = limitService.create(projectId, req.periodStart(), req.periodEnd(),
                req.limitAmount(), req.requiresAuthAbove());
        return LimitResponse.from(limit);
    }

    // --- Procurement Schedule ---

    @Operation(summary = "List procurement schedule")
    @GetMapping("/schedule")
    List<ScheduleResponse> listSchedule(@PathVariable UUID projectId,
                                         @RequestParam(required = false) String status) {
        var items = "PLANNED".equals(status)
                ? scheduleService.listPlanned(projectId)
                : scheduleService.listByProject(projectId);
        return items.stream().map(ScheduleResponse::from).toList();
    }

    @Operation(summary = "Add item to procurement schedule")
    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    ScheduleResponse createScheduleItem(@PathVariable UUID projectId,
                                         @Valid @RequestBody CreateScheduleRequest req) {
        var item = scheduleService.create(projectId, req.materialDescription(),
                req.plannedDate(), req.quantity(), req.estimatedCost());
        return ScheduleResponse.from(item);
    }

    @Operation(summary = "Link schedule item to purchase order")
    @PostMapping("/schedule/{scheduleId}/link-order")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ScheduleResponse linkOrder(@PathVariable UUID projectId, @PathVariable UUID scheduleId,
                                @RequestBody LinkOrderRequest req) {
        return ScheduleResponse.from(scheduleService.linkToOrder(scheduleId, req.purchaseOrderId()));
    }

    // --- Order Items ---

    @Operation(summary = "Add items to a purchase order")
    @PostMapping("/orders/{orderId}/items")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.CREATED)
    List<OrderItemResponse> addItems(@PathVariable UUID projectId, @PathVariable UUID orderId,
                                      @RequestBody List<ItemEntry> entries) {
        return itemService.addItems(orderId, entries).stream().map(OrderItemResponse::from).toList();
    }

    @Operation(summary = "List items of a purchase order")
    @GetMapping("/orders/{orderId}/items")
    List<OrderItemResponse> listItems(@PathVariable UUID projectId, @PathVariable UUID orderId) {
        return itemService.listByOrder(orderId).stream().map(OrderItemResponse::from).toList();
    }

    @Operation(summary = "Receive quantity for an order item")
    @PostMapping("/orders/items/{itemId}/receive")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    OrderItemResponse receiveItem(@PathVariable UUID projectId, @PathVariable UUID itemId,
                                   @RequestBody ReceiveItemRequest req) {
        return OrderItemResponse.from(itemService.receiveItem(itemId, req.quantity()));
    }

    // DTOs
    record CreateLimitRequest(@NotNull LocalDate periodStart, @NotNull LocalDate periodEnd,
                               @NotNull BigDecimal limitAmount, BigDecimal requiresAuthAbove) {}
    record CreateScheduleRequest(@NotBlank String materialDescription, @NotNull LocalDate plannedDate,
                                  @NotNull BigDecimal quantity, BigDecimal estimatedCost) {}
    record LinkOrderRequest(@NotNull UUID purchaseOrderId) {}
    record ReceiveItemRequest(@NotNull BigDecimal quantity) {}

    record LimitResponse(UUID id, UUID projectId, LocalDate periodStart, LocalDate periodEnd,
                          BigDecimal limitAmount, BigDecimal consumedAmount, BigDecimal available) {
        static LimitResponse from(PurchaseBudgetLimit l) {
            return new LimitResponse(l.getId(), l.getProjectId(), l.getPeriodStart(), l.getPeriodEnd(),
                    l.getLimitAmount(), l.getConsumedAmount(), l.getAvailableAmount());
        }
    }

    record ScheduleResponse(UUID id, String materialDescription, LocalDate plannedDate,
                              BigDecimal quantity, BigDecimal estimatedCost, String status, UUID purchaseOrderId) {
        static ScheduleResponse from(ProcurementSchedule s) {
            return new ScheduleResponse(s.getId(), s.getMaterialDescription(), s.getPlannedDate(),
                    s.getQuantity(), s.getEstimatedCost(), s.getStatus(), s.getPurchaseOrderId());
        }
    }

    record OrderItemResponse(UUID id, UUID orderId, String description, String unit,
                              BigDecimal quantity, BigDecimal unitPrice, BigDecimal totalPrice,
                              BigDecimal receivedQuantity, BigDecimal pendingQuantity) {
        static OrderItemResponse from(PurchaseOrderItem i) {
            return new OrderItemResponse(i.getId(), i.getOrderId(), i.getDescription(), i.getUnit(),
                    i.getQuantity(), i.getUnitPrice(), i.getTotalPrice(),
                    i.getReceivedQuantity(), i.getPendingQuantity());
        }
    }
}
