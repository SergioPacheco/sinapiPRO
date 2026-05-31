package com.sinapipro.api.security.application;

import com.sinapipro.api.security.domain.AppUser;
import com.sinapipro.api.security.domain.AppUserRepository;
import com.sinapipro.api.security.domain.DefaultRoles;
import com.sinapipro.api.security.domain.Permissions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * SpEL permission evaluator. Usage in controllers:
 * {@code @PreAuthorize("@perm.check('budget.write')")}
 * {@code @PreAuthorize("@perm.checkProject(#projectId, 'budget.write')")}
 */
@Component("perm")
public class PermissionEvaluatorBean {

    private final AppUserRepository userRepo;

    public PermissionEvaluatorBean(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /** Check if current user has the given permission. */
    public boolean check(String permission) {
        var perms = resolvePermissions();
        if (perms.contains(Permissions.ADMIN_FULL)) return true;
        return perms.contains(permission);
    }

    /** Check permission AND project-scoped access. */
    public boolean checkProject(UUID projectId, String permission) {
        if (!check(permission)) return false;
        return hasProjectAccess(projectId);
    }

    /** Check if user has access to a specific project. */
    public boolean hasProjectAccess(UUID projectId) {
        if (projectId == null) return true;
        Optional<AppUser> user = getCurrentAppUser();
        // No AppUser yet (first request, filter hasn't committed) → allow
        return user.map(u -> u.hasAccessToProject(projectId)).orElse(true);
    }

    private Set<String> resolvePermissions() {
        // First try from AppUser in DB (has role_permission from DB)
        Optional<AppUser> appUser = getCurrentAppUser();
        if (appUser.isPresent() && !appUser.get().getRoles().isEmpty()) {
            return appUser.get().getAllPermissions();
        }
        // Fallback: resolve from JWT roles claim using DefaultRoles
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            var roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                var perms = new HashSet<String>();
                for (var roleName : roles) {
                    var def = DefaultRoles.ROLES.get(roleName);
                    if (def != null) perms.addAll(def.permissions());
                }
                return perms;
            }
        }
        return Set.of();
    }

    private Optional<AppUser> getCurrentAppUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String sub = jwtAuth.getToken().getSubject();
            if (sub != null) return userRepo.findByExternalId(sub);
        }
        return Optional.empty();
    }
}
