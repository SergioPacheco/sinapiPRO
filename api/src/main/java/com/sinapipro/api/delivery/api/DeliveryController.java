package com.sinapipro.api.delivery.api;

import com.sinapipro.api.delivery.domain.Delivery;
import com.sinapipro.api.delivery.domain.DeliveryChecklistItem;
import com.sinapipro.api.delivery.domain.DeliveryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/delivery")
public class DeliveryController {

    private final DeliveryRepository repository;

    public DeliveryController(DeliveryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Delivery get(@PathVariable UUID projectId) {
        return repository.findByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No delivery record for this project"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Delivery create(@PathVariable UUID projectId, @RequestBody CreateDeliveryRequest req) {
        var delivery = new Delivery();
        delivery.setProjectId(projectId);
        delivery.setDeliveredBy(req.deliveredBy());
        delivery.setReceivedBy(req.receivedBy());
        delivery.setNotes(req.notes());
        if (req.checklistItems() != null) {
            req.checklistItems().forEach(desc -> delivery.addItem(new DeliveryChecklistItem(desc)));
        }
        return repository.save(delivery);
    }

    @PostMapping("/provisional")
    public Delivery provisionalAcceptance(@PathVariable UUID projectId) {
        var delivery = findOrThrow(projectId);
        delivery.setStatus("PROVISIONAL");
        delivery.setProvisionalDate(LocalDate.now());
        return repository.save(delivery);
    }

    @PostMapping("/definitive")
    public Delivery definitiveAcceptance(@PathVariable UUID projectId) {
        var delivery = findOrThrow(projectId);
        delivery.setStatus("DEFINITIVE");
        delivery.setDefinitiveDate(LocalDate.now());
        return repository.save(delivery);
    }

    @PostMapping("/checklist/{itemId}/check")
    public Delivery checkItem(@PathVariable UUID projectId, @PathVariable UUID itemId) {
        var delivery = findOrThrow(projectId);
        delivery.getChecklist().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .ifPresent(i -> i.setChecked(true));
        return repository.save(delivery);
    }

    private Delivery findOrThrow(UUID projectId) {
        return repository.findByProjectId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    record CreateDeliveryRequest(String deliveredBy, String receivedBy, String notes, List<String> checklistItems) {}
}
