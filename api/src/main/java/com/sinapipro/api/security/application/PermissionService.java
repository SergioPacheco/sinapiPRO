package com.sinapipro.api.security.application;

import com.sinapipro.api.security.domain.DefaultRoles;
import com.sinapipro.api.security.domain.Permissions;
import com.sinapipro.api.security.domain.Role;
import com.sinapipro.api.security.domain.RoleRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Serviço central de verificação de permissões.
 * Usado pelos controllers para verificar se o usuário pode executar uma ação.
 */
@Service
public class PermissionService {

    private final RoleRepository roleRepository;

    public PermissionService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Verifica se o usuário atual tem a permissão especificada.
     */
    public boolean hasPermission(String permission) {
        var userPermissions = getCurrentUserPermissions();
        // Admin tem acesso total
        if (userPermissions.contains(Permissions.ADMIN_FULL)) return true;
        return userPermissions.contains(permission);
    }

    /**
     * Lança exceção se o usuário não tem a permissão.
     */
    public void requirePermission(String permission) {
        if (!hasPermission(permission)) {
            throw new AccessDeniedException("Permissão necessária: " + permission);
        }
    }

    /**
     * Retorna todas as permissões do usuário atual (baseado nos roles do JWT).
     */
    public Set<String> getCurrentUserPermissions() {
        var roles = getCurrentUserRoles();
        var permissions = new HashSet<String>();

        for (var roleName : roles) {
            // Primeiro tenta buscar do banco (roles customizados)
            roleRepository.findByName(roleName).ifPresentOrElse(
                role -> permissions.addAll(role.getPermissions()),
                // Fallback: usa roles pré-definidos
                () -> {
                    var defaultRole = DefaultRoles.ROLES.get(roleName);
                    if (defaultRole != null) permissions.addAll(defaultRole.permissions());
                }
            );
        }

        return permissions;
    }

    /**
     * Retorna os roles do JWT do usuário atual.
     */
    public List<String> getCurrentUserRoles() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            var roles = jwt.getClaimAsStringList("roles");
            return roles != null ? roles : List.of();
        }
        return List.of();
    }

    public static class AccessDeniedException extends org.springframework.security.access.AccessDeniedException {
        public AccessDeniedException(String message) { super(message); }
    }
}
