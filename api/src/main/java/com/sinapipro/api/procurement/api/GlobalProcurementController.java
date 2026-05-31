package com.sinapipro.api.procurement.api;

import com.sinapipro.api.procurement.domain.PurchaseOrder;
import com.sinapipro.api.procurement.domain.PurchaseOrderRepository;
import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Procurement Global", description = "Cross-project purchase orders")
@RestController
@RequestMapping("/api/v1/procurement")
public class GlobalProcurementController {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProjectRepository projectRepository;

    public GlobalProcurementController(PurchaseOrderRepository purchaseOrderRepository, ProjectRepository projectRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.projectRepository = projectRepository;
    }

    @Operation(summary = "List all purchase orders across projects")
    @GetMapping("/orders")
    @PreAuthorize("@perm.check('procurement.read')")
    PageResponse<GlobalPurchaseOrderResponse> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID supplierId,
            @RequestParam(required = false) LocalDate deliveryFrom,
            @RequestParam(required = false) LocalDate deliveryTo,
            @PageableDefault(size = 20) Pageable pageable) {

        Specification<PurchaseOrder> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (supplierId != null) predicates.add(cb.equal(root.get("supplier").get("id"), supplierId));
            if (deliveryFrom != null) predicates.add(cb.greaterThanOrEqualTo(root.get("expectedDeliveryDate"), deliveryFrom));
            if (deliveryTo != null) predicates.add(cb.lessThanOrEqualTo(root.get("expectedDeliveryDate"), deliveryTo));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var page = purchaseOrderRepository.findAll(spec, pageable);
        var projectIds = page.getContent().stream()
                .map(po -> po.getBudget().getProjectId())
                .filter(java.util.Objects::nonNull).distinct().toList();
        var projectNames = projectIds.isEmpty() ? Map.<UUID, String>of() :
                projectRepository.findAllById(projectIds).stream().collect(Collectors.toMap(Project::getId, Project::getName));

        return PageResponse.from(page.map(po -> {
            UUID pid = po.getBudget().getProjectId();
            return new GlobalPurchaseOrderResponse(
                    po.getId(), po.getNumber(), po.getDescription(),
                    po.getSupplier().getName(), po.getQuantity(), po.getUnitPrice(), po.getTotalAmount(),
                    po.getStatus(), po.getExpectedDeliveryDate(),
                    pid, pid != null ? projectNames.getOrDefault(pid, "") : null
            );
        }));
    }

    record GlobalPurchaseOrderResponse(UUID id, String number, String description, String supplierName,
                                       BigDecimal quantity, BigDecimal unitPrice, BigDecimal totalAmount,
                                       String status, LocalDate expectedDeliveryDate,
                                       UUID projectId, String projectName) {}
}
