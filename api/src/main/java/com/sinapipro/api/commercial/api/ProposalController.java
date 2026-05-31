package com.sinapipro.api.commercial.api;

import com.sinapipro.api.commercial.domain.Proposal;
import com.sinapipro.api.commercial.domain.ProposalRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/proposals")
public class ProposalController {

    private final ProposalRepository repository;

    public ProposalController(ProposalRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<Proposal> list(@RequestParam(required = false) String status, Pageable pageable) {
        if (status != null) return repository.findByStatus(status, pageable);
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Proposal getById(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Proposal create(@Valid @RequestBody CreateProposalRequest req) {
        var p = new Proposal();
        p.setTitle(req.title());
        p.setClientName(req.clientName());
        p.setClientId(req.clientId());
        p.setScope(req.scope());
        p.setTotalValue(req.totalValue());
        p.setValidityDays(req.validityDays());
        p.setProposalDate(req.proposalDate() != null ? req.proposalDate() : LocalDate.now());
        p.setConditions(req.conditions());
        p.setNotes(req.notes());
        return repository.save(p);
    }

    @PostMapping("/{id}/send")
    public Proposal send(@PathVariable UUID id) {
        var p = findOrThrow(id);
        p.setStatus("SENT");
        return repository.save(p);
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("@perm.check('commercial.write')")
    public Proposal accept(@PathVariable UUID id) {
        var p = findOrThrow(id);
        p.setStatus("ACCEPTED");
        return repository.save(p);
    }

    @PostMapping("/{id}/reject")
    public Proposal reject(@PathVariable UUID id) {
        var p = findOrThrow(id);
        p.setStatus("REJECTED");
        return repository.save(p);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        repository.deleteById(id);
    }

    private Proposal findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    record CreateProposalRequest(
            @NotBlank String title,
            @NotBlank String clientName,
            UUID clientId,
            String scope,
            BigDecimal totalValue,
            Integer validityDays,
            LocalDate proposalDate,
            String conditions,
            String notes
    ) {}
}
