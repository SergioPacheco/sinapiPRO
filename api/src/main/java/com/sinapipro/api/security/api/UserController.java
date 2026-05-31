package com.sinapipro.api.security.api;

import com.sinapipro.api.security.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "User Management", description = "CRUD de usuários, atribuição de roles e acesso por obra")
@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final AppUserRepository userRepo;
    private final RoleRepository roleRepo;

    public UserController(AppUserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo; this.roleRepo = roleRepo;
    }

    @Operation(summary = "Get current authenticated user profile")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    UserResponse me(org.springframework.security.core.Authentication authentication) {
        if (authentication instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {
            String sub = jwtAuth.getToken().getSubject();
            var user = userRepo.findByExternalId(sub)
                    .orElseThrow(() -> new DomainNotFoundException("User not provisioned yet"));
            return UserResponse.from(user);
        }
        throw new DomainNotFoundException("No JWT authentication");
    }

    @Operation(summary = "List all users")
    @GetMapping
    List<UserResponse> list() {
        return userRepo.findByActiveTrue().stream().map(UserResponse::from).toList();
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    UserResponse get(@PathVariable UUID id) {
        return UserResponse.from(userRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("User not found")));
    }

    @Operation(summary = "Create user")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse create(@Valid @RequestBody CreateUserRequest req) {
        var user = new AppUser(req.email(), req.name(), req.externalId());
        if (req.roleIds() != null && !req.roleIds().isEmpty()) {
            user.setRoles(new HashSet<>(roleRepo.findAllById(req.roleIds())));
        }
        return UserResponse.from(userRepo.save(user));
    }

    @Operation(summary = "Update user roles")
    @PutMapping("/{id}/roles")
    UserResponse updateRoles(@PathVariable UUID id, @RequestBody Set<UUID> roleIds) {
        var user = userRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("User not found"));
        user.setRoles(new HashSet<>(roleRepo.findAllById(roleIds)));
        return UserResponse.from(userRepo.save(user));
    }

    @Operation(summary = "Grant project access to user")
    @PostMapping("/{id}/projects/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void grantProject(@PathVariable UUID id, @PathVariable UUID projectId) {
        var user = userRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("User not found"));
        user.grantProjectAccess(projectId);
        userRepo.save(user);
    }

    @Operation(summary = "Revoke project access from user")
    @DeleteMapping("/{id}/projects/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void revokeProject(@PathVariable UUID id, @PathVariable UUID projectId) {
        var user = userRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("User not found"));
        user.revokeProjectAccess(projectId);
        userRepo.save(user);
    }

    @Operation(summary = "Deactivate user")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivate(@PathVariable UUID id) {
        var user = userRepo.findById(id).orElseThrow(() -> new DomainNotFoundException("User not found"));
        user.deactivate();
        userRepo.save(user);
    }

    // DTOs
    record CreateUserRequest(@NotBlank String email, @NotBlank String name, String externalId, Set<UUID> roleIds) {}
    record UserResponse(UUID id, String email, String name, boolean active, List<String> roles, Set<UUID> projectAccess, Set<String> permissions) {
        static UserResponse from(AppUser u) {
            return new UserResponse(u.getId(), u.getEmail(), u.getName(), u.isActive(),
                u.getRoles().stream().map(Role::getName).toList(), u.getProjectAccess(), u.getAllPermissions());
        }
    }
}
