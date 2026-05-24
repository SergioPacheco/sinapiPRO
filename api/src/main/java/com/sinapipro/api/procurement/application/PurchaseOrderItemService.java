package com.sinapipro.api.procurement.application;

import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PurchaseOrderItemService {

    private final PurchaseOrderItemRepository itemRepository;
    private final PurchaseOrderRepository orderRepository;

    public PurchaseOrderItemService(PurchaseOrderItemRepository itemRepository,
                                     PurchaseOrderRepository orderRepository) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
    }

    public PurchaseOrderItem addItem(UUID orderId, UUID materialId, String description,
                                      String unit, BigDecimal quantity, BigDecimal unitPrice) {
        return itemRepository.save(new PurchaseOrderItem(orderId, materialId, description, unit, quantity, unitPrice));
    }

    public List<PurchaseOrderItem> addItems(UUID orderId, List<ItemEntry> entries) {
        var items = entries.stream()
                .map(e -> new PurchaseOrderItem(orderId, e.materialId(), e.description(), e.unit(), e.quantity(), e.unitPrice()))
                .toList();
        return itemRepository.saveAll(items);
    }

    public List<PurchaseOrderItem> listByOrder(UUID orderId) {
        return itemRepository.findByOrderId(orderId);
    }

    /**
     * Recebimento parcial de um item. Atualiza status do pedido.
     */
    public PurchaseOrderItem receiveItem(UUID itemId, BigDecimal quantityReceived) {
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DomainNotFoundException("Order item not found: " + itemId));

        if (quantityReceived.compareTo(item.getPendingQuantity()) > 0) {
            throw new IllegalStateException("Cannot receive more than pending: " + item.getPendingQuantity());
        }

        item.receiveQuantity(quantityReceived);
        itemRepository.save(item);

        // Atualizar status do pedido
        updateOrderStatus(item.getOrderId());

        return item;
    }

    private void updateOrderStatus(UUID orderId) {
        var items = itemRepository.findByOrderId(orderId);
        var allReceived = items.stream().allMatch(PurchaseOrderItem::isFullyReceived);
        var anyReceived = items.stream().anyMatch(i -> i.getReceivedQuantity().signum() > 0);

        orderRepository.findById(orderId).ifPresent(order -> {
            if (allReceived) order.markFullyReceived();
            else if (anyReceived) order.markPartiallyReceived();
            orderRepository.save(order);
        });
    }

    public record ItemEntry(UUID materialId, String description, String unit,
                             BigDecimal quantity, BigDecimal unitPrice) {}
}
