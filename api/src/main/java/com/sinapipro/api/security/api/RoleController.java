package com.sinapipro.api.security.api;

import com.sinapipro.api.security.domain.Role;
import com.sinapipro.api.security.domain.RoleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleRepository repository;

    public RoleController(RoleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Role> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Role getById(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Role create(@Valid @RequestBody CreateRoleRequest request) {
        if (repository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists");
        }
        return repository.save(new Role(request.name(), request.description(), request.permissions()));
    }

    @PutMapping("/{id}")
    public Role update(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        var role = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        role.setDescription(request.description());
        role.setPermissions(request.permissions());
        return repository.save(role);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        repository.deleteById(id);
    }

    @GetMapping("/permissions")
    public List<String> availablePermissions() {
        return List.of(
            "projects.read", "projects.write",
            "budgets.read", "budgets.write", "budgets.approve",
            "contracts.read", "contracts.write",
            "measurements.read", "measurements.write", "measurements.approve",
            "procurement.read", "procurement.write", "procurement.approve",
            "finance.read", "finance.write",
            "safety.read", "safety.write",
            "documents.read", "documents.write",
            "registry.read", "registry.write",
            "settings.read", "settings.write",
            "analytics.read"
        );
    }

    record CreateRoleRequest(@NotBlank String name, String description, Set<String> permissions) {}
    record UpdateRoleRequest(String description, Set<String> permissions) {}
}
