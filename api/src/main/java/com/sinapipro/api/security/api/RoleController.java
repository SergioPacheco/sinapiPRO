package com.sinapipro.api.security.api;

import com.sinapipro.api.security.application.PermissionService;
import com.sinapipro.api.security.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Roles & Permissions", description = "Gerenciamento de perfis, permissões e autorizações")
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleRepository repository;
    private final PermissionService permissionService;

    public RoleController(RoleRepository repository, PermissionService permissionService) {
        this.repository = repository;
        this.permissionService = permissionService;
    }

    @Operation(summary = "List all roles")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> list() {
        return repository.findAll().stream().map(RoleResponse::from).toList();
    }

    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse getById(@PathVariable UUID id) {
        return RoleResponse.from(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Operation(summary = "Create a custom role")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@Valid @RequestBody CreateRoleRequest request) {
        if (repository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists");
        }
        return RoleResponse.from(repository.save(new Role(request.name(), request.description(), request.permissions())));
    }

    @Operation(summary = "Update role permissions")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        var role = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        role.setDescription(request.description());
        role.setPermissions(request.permissions());
        return RoleResponse.from(repository.save(role));
    }

    @Operation(summary = "Delete a role")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { repository.deleteById(id); }

    @Operation(summary = "List all available permissions (for role editor)")
    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionGroup> availablePermissions() {
        return List.of(
            new PermissionGroup("Obras", List.of(Permissions.PROJECT_READ, Permissions.PROJECT_WRITE, Permissions.PROJECT_DELETE)),
            new PermissionGroup("Orçamento", List.of(Permissions.BUDGET_READ, Permissions.BUDGET_WRITE, Permissions.BUDGET_EFFECTUATE, Permissions.BUDGET_EXPORT)),
            new PermissionGroup("Medição", List.of(Permissions.MEASUREMENT_READ, Permissions.MEASUREMENT_WRITE, Permissions.MEASUREMENT_APPROVE, Permissions.MEASUREMENT_REJECT)),
            new PermissionGroup("Suprimentos", List.of(Permissions.PROCUREMENT_READ, Permissions.PROCUREMENT_WRITE, Permissions.PROCUREMENT_APPROVE)),
            new PermissionGroup("Financeiro", List.of(Permissions.FINANCE_READ, Permissions.FINANCE_WRITE, Permissions.FINANCE_PAY, Permissions.FINANCE_RECEIVE, Permissions.FINANCE_RECONCILE)),
            new PermissionGroup("Comercial", List.of(Permissions.COMMERCIAL_READ, Permissions.COMMERCIAL_WRITE, Permissions.COMMERCIAL_CANCEL)),
            new PermissionGroup("Mão de Obra", List.of(Permissions.LABOR_READ, Permissions.LABOR_WRITE, Permissions.LABOR_CLOSE_PERIOD)),
            new PermissionGroup("Cadastros", List.of(Permissions.REGISTRY_READ, Permissions.REGISTRY_WRITE)),
            new PermissionGroup("Relatórios", List.of(Permissions.REPORT_READ, Permissions.REPORT_EXPORT)),
            new PermissionGroup("Configurações", List.of(Permissions.SETTINGS_READ, Permissions.SETTINGS_WRITE, Permissions.SETTINGS_MANAGE_USERS)),
            new PermissionGroup("Sistema", List.of(Permissions.ADMIN_FULL, Permissions.SINAPI_IMPORT))
        );
    }

    @Operation(summary = "List default role templates (pre-defined profiles)")
    @GetMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleTemplate> defaultTemplates() {
        return DefaultRoles.ROLES.entrySet().stream()
                .map(e -> new RoleTemplate(e.getKey(), e.getValue().description(), e.getValue().fullDescription(), e.getValue().permissions()))
                .toList();
    }

    @Operation(summary = "Get current user's permissions (for frontend menu/access control)")
    @GetMapping("/my-permissions")
    public UserPermissions myPermissions() {
        var roles = permissionService.getCurrentUserRoles();
        var permissions = permissionService.getCurrentUserPermissions();
        return new UserPermissions(roles, permissions);
    }

    @Operation(summary = "Initialize default roles in database")
    @PostMapping("/initialize-defaults")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public List<RoleResponse> initializeDefaults() {
        var created = new ArrayList<Role>();
        DefaultRoles.ROLES.forEach((name, def) -> {
            if (!repository.existsByName(name)) {
                created.add(repository.save(new Role(name, def.description(), def.permissions())));
            }
        });
        return created.stream().map(RoleResponse::from).toList();
    }

    // DTOs
    record CreateRoleRequest(@NotBlank String name, String description, Set<String> permissions) {}
    record UpdateRoleRequest(String description, Set<String> permissions) {}
    record RoleResponse(UUID id, String name, String description, Set<String> permissions, int permissionsCount) {
        static RoleResponse from(Role r) { return new RoleResponse(r.getId(), r.getName(), r.getDescription(), r.getPermissions(), r.getPermissions().size()); }
    }
    record PermissionGroup(String module, List<String> permissions) {}
    record RoleTemplate(String name, String description, String fullDescription, Set<String> permissions) {}
    record UserPermissions(List<String> roles, Set<String> permissions) {}
}
