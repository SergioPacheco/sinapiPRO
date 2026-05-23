package com.sinapipro.api.shared.api;

import com.sinapipro.api.shared.domain.TrashItem;
import com.sinapipro.api.shared.domain.TrashItemRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Trash", description = "Recycle bin — list, restore and purge deleted items")
@RestController
@RequestMapping("/api/v1/trash")
public class TrashController {

    private final TrashItemRepository repository;

    public TrashController(TrashItemRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "List all trashed items")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    List<TrashItem> list(@RequestParam(required = false) String entityType) {
        if (entityType != null) return repository.findByEntityTypeOrderByDeletedAtDesc(entityType);
        return repository.findAllByOrderByDeletedAtDesc();
    }

    @Operation(summary = "Restore a trashed item (returns snapshot for client-side restoration)")
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    TrashItem restore(@PathVariable UUID id) {
        TrashItem item = repository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Trash item not found: " + id));
        repository.delete(item);
        return item;
    }

    @Operation(summary = "Permanently delete a trashed item")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void purge(@PathVariable UUID id) {
        if (!repository.existsById(id)) throw new DomainNotFoundException("Trash item not found: " + id);
        repository.deleteById(id);
    }

    @Operation(summary = "Purge all expired items")
    @DeleteMapping("/expired")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void purgeExpired() {
        repository.deleteAll(repository.findByExpiresAtBefore(Instant.now()));
    }
}
