package com.sinapipro.api.security.application;

import com.sinapipro.api.security.domain.AppUser;
import com.sinapipro.api.security.domain.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Auto-provisions an AppUser on first JWT login.
 * Links JWT "sub" claim to AppUser.externalId.
 * Registered in SecurityFilterChain after BearerTokenAuthenticationFilter.
 */
@Component
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final AppUserRepository userRepo;

    public UserProvisioningFilter(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String sub = jwt.getSubject();
            if (sub != null && userRepo.findByExternalId(sub).isEmpty()) {
                try {
                    String email = jwt.getClaimAsString("email");
                    String name = jwt.getClaimAsString("name");
                    if (email == null) email = sub;
                    if (name == null) name = email;
                    var existing = userRepo.findByEmail(email);
                    if (existing.isPresent()) {
                        var user = existing.get();
                        if (user.getExternalId() == null) {
                            user.setExternalId(sub);
                            userRepo.save(user);
                        }
                    } else {
                        userRepo.save(new AppUser(email, name, sub));
                    }
                } catch (Exception ignored) {
                    // Race condition: another thread already created the user
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
