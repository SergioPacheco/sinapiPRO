package com.sinapipro.api.registry.api;

import com.sinapipro.api.registry.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.domain.ContactDepartment;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Tag(name = "Client Contacts", description = "Sub-resource: contacts per client")
@RestController
@RequestMapping("/api/v1/registry/clients/{clientId}/contacts")
public class ClientContactController {

    private final ClientContactRepository contactRepository;
    private final ClientRepository clientRepository;

    public ClientContactController(ClientContactRepository contactRepository, ClientRepository clientRepository) {
        this.contactRepository = contactRepository;
        this.clientRepository = clientRepository;
    }

    @Operation(summary = "List contacts for a client")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<ClientContact> list(@PathVariable UUID clientId, @PageableDefault(size = 20) Pageable pageable) {
        ensureClientExists(clientId);
        return PageResponse.from(contactRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable));
    }

    @Operation(summary = "Create a contact for a client")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    ResponseEntity<ClientContact> create(@PathVariable UUID clientId, @Valid @RequestBody CreateContactRequest req) {
        ensureClientExists(clientId);
        var contact = contactRepository.save(new ClientContact(clientId, req.name(), req.role(), req.email(), req.phone(), req.department(), req.primary()));
        if (req.primary()) {
            contactRepository.clearPrimaryForDepartment(clientId, req.department(), contact.getId());
        }
        return ResponseEntity.created(URI.create("/api/v1/registry/clients/" + clientId + "/contacts/" + contact.getId())).body(contact);
    }

    @Operation(summary = "Update a contact")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    ClientContact update(@PathVariable UUID clientId, @PathVariable UUID id, @Valid @RequestBody CreateContactRequest req) {
        ensureClientExists(clientId);
        var contact = contactRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Contact not found: " + id));
        contact.update(req.name(), req.role(), req.email(), req.phone(), req.department(), req.primary());
        var saved = contactRepository.save(contact);
        if (req.primary()) {
            contactRepository.clearPrimaryForDepartment(clientId, req.department(), saved.getId());
        }
        return saved;
    }

    @Operation(summary = "Delete a contact")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID clientId, @PathVariable UUID id) {
        ensureClientExists(clientId);
        if (!contactRepository.existsById(id)) throw new DomainNotFoundException("Contact not found: " + id);
        contactRepository.deleteById(id);
    }

    private void ensureClientExists(UUID clientId) {
        if (!clientRepository.existsById(clientId)) throw new DomainNotFoundException("Client not found: " + clientId);
    }

    record CreateContactRequest(@NotBlank String name, String role, String email, String phone,
                                @NotNull ContactDepartment department, boolean primary) {}
}
