package com.sinapipro.api.security;

import com.sinapipro.api.security.application.PermissionEvaluatorBean;
import com.sinapipro.api.security.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PermissionEvaluatorBeanTest {

    private AppUserRepository userRepo;
    private PermissionEvaluatorBean perm;

    @BeforeEach
    void setUp() {
        userRepo = Mockito.mock(AppUserRepository.class);
        perm = new PermissionEvaluatorBean(userRepo);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateWith(String sub, List<String> roles) {
        var jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject(sub)
                .claim("roles", roles)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        var auth = new JwtAuthenticationToken(jwt, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("should grant access when user has ADMIN_FULL permission")
    void adminHasFullAccess() {
        authenticateWith("admin@test.com", List.of("ADMIN"));
        when(userRepo.findByExternalId("admin@test.com")).thenReturn(Optional.empty());

        assertThat(perm.check("budget.write")).isTrue();
        assertThat(perm.check("finance.pay")).isTrue();
        assertThat(perm.check("anything.at.all")).isTrue();
    }

    @Test
    @DisplayName("should deny access when user lacks permission")
    void shouldDenyWithoutPermission() {
        authenticateWith("buyer@test.com", List.of("COMPRADOR"));
        when(userRepo.findByExternalId("buyer@test.com")).thenReturn(Optional.empty());

        assertThat(perm.check("procurement.read")).isTrue();
        assertThat(perm.check("finance.pay")).isFalse();
    }

    @Test
    @DisplayName("should use AppUser roles from DB when available")
    void shouldUseDbRoles() {
        authenticateWith("user@test.com", List.of("CUSTOM_ROLE"));
        var user = new AppUser("user@test.com", "User", "user@test.com");
        var role = new Role("CUSTOM", "Custom", Set.of("budget.read", "budget.write"));
        user.setRoles(Set.of(role));
        when(userRepo.findByExternalId("user@test.com")).thenReturn(Optional.of(user));

        assertThat(perm.check("budget.write")).isTrue();
        assertThat(perm.check("finance.pay")).isFalse();
    }

    @Test
    @DisplayName("checkProject should allow when projectAccess is empty (all projects)")
    void emptyProjectAccessMeansAll() {
        authenticateWith("eng@test.com", List.of("ENGENHEIRO"));
        var user = new AppUser("eng@test.com", "Eng", "eng@test.com");
        when(userRepo.findByExternalId("eng@test.com")).thenReturn(Optional.of(user));

        UUID anyProject = UUID.randomUUID();
        assertThat(perm.checkProject(anyProject, "budget.read")).isTrue();
    }

    @Test
    @DisplayName("checkProject should deny when user has restricted access to different project")
    void restrictedProjectAccessShouldBlock() {
        authenticateWith("eng@test.com", List.of("ENGENHEIRO"));
        var user = new AppUser("eng@test.com", "Eng", "eng@test.com");
        UUID allowedProject = UUID.randomUUID();
        user.grantProjectAccess(allowedProject);
        when(userRepo.findByExternalId("eng@test.com")).thenReturn(Optional.of(user));

        assertThat(perm.checkProject(allowedProject, "budget.read")).isTrue();
        assertThat(perm.checkProject(UUID.randomUUID(), "budget.read")).isFalse();
    }

    @Test
    @DisplayName("should return false when no authentication present")
    void noAuthShouldDeny() {
        // No SecurityContext set
        assertThat(perm.check("budget.read")).isFalse();
    }
}
