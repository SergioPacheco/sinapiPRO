package com.sinapipro.api.security;

import com.sinapipro.api.security.application.ProjectAccessInterceptor;
import com.sinapipro.api.security.domain.AppUser;
import com.sinapipro.api.security.domain.AppUserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.servlet.HandlerMapping;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ProjectAccessInterceptorTest {

    private AppUserRepository userRepo;
    private ProjectAccessInterceptor interceptor;

    @BeforeEach
    void setUp() {
        userRepo = Mockito.mock(AppUserRepository.class);
        interceptor = new ProjectAccessInterceptor(userRepo);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateWith(String sub) {
        var jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject(sub)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
    }

    @Test
    @DisplayName("should allow when no projectId in path")
    void noProjectIdShouldAllow() throws Exception {
        authenticateWith("user@test.com");
        var request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("id", UUID.randomUUID().toString()));
        var response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
    }

    @Test
    @DisplayName("should allow when user has empty projectAccess (all projects)")
    void emptyAccessAllowsAll() throws Exception {
        authenticateWith("user@test.com");
        var user = new AppUser("user@test.com", "User", "user@test.com");
        when(userRepo.findByExternalId("user@test.com")).thenReturn(Optional.of(user));

        UUID projectId = UUID.randomUUID();
        var request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("projectId", projectId.toString()));
        var response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("should return 403 when user has restricted access and project not in list")
    void restrictedAccessShouldBlock() throws Exception {
        authenticateWith("user@test.com");
        var user = new AppUser("user@test.com", "User", "user@test.com");
        user.grantProjectAccess(UUID.randomUUID()); // grant access to a different project
        when(userRepo.findByExternalId("user@test.com")).thenReturn(Optional.of(user));

        UUID blockedProject = UUID.randomUUID();
        var request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("projectId", blockedProject.toString()));
        var response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("should allow when user has access to the specific project")
    void allowedProjectShouldPass() throws Exception {
        authenticateWith("user@test.com");
        UUID allowedProject = UUID.randomUUID();
        var user = new AppUser("user@test.com", "User", "user@test.com");
        user.grantProjectAccess(allowedProject);
        when(userRepo.findByExternalId("user@test.com")).thenReturn(Optional.of(user));

        var request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("projectId", allowedProject.toString()));
        var response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
    }

    @Test
    @DisplayName("should allow when no authentication present")
    void noAuthShouldAllow() throws Exception {
        var request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of("projectId", UUID.randomUUID().toString()));
        var response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
    }
}
