package com.sinapipro.api.security.application;

import com.sinapipro.api.security.domain.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.UUID;

/**
 * Intercepts requests with {projectId} path variable and verifies
 * the authenticated user has access to that project via user_project_access.
 * Empty projectAccess set = access to all projects.
 */
@Component
public class ProjectAccessInterceptor implements HandlerInterceptor {

    private final AppUserRepository userRepo;

    public ProjectAccessInterceptor(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) return true;

        var pathVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVars == null || !pathVars.containsKey("projectId")) return true;

        String projectIdStr = pathVars.get("projectId");
        UUID projectId;
        try {
            projectId = UUID.fromString(projectIdStr);
        } catch (IllegalArgumentException e) {
            return true; // let validation handle bad UUIDs
        }

        String sub = jwtAuth.getToken().getSubject();
        var appUser = userRepo.findByExternalId(sub);
        if (appUser.isEmpty()) return true; // user not provisioned yet, allow (filter will create)

        if (!appUser.get().hasAccessToProject(projectId)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/problem+json");
            response.getWriter().write("""
                {"type":"about:blank","title":"Forbidden","status":403,"detail":"No access to this project"}""");
            return false;
        }
        return true;
    }
}
