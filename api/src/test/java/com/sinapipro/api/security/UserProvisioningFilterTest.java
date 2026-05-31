package com.sinapipro.api.security;

import com.sinapipro.api.security.application.UserProvisioningFilter;
import com.sinapipro.api.security.domain.AppUser;
import com.sinapipro.api.security.domain.AppUserRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserProvisioningFilterTest {

    private AppUserRepository userRepo;
    private UserProvisioningFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        userRepo = Mockito.mock(AppUserRepository.class);
        filter = new UserProvisioningFilter(userRepo);
        filterChain = Mockito.mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateWith(String sub, String email, String name) {
        var builder = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject(sub)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600));
        if (email != null) builder.claim("email", email);
        if (name != null) builder.claim("name", name);
        var jwt = builder.build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
    }

    @Test
    @DisplayName("should create AppUser on first login")
    void shouldProvisionOnFirstLogin() throws Exception {
        authenticateWith("user123", "john@example.com", "John Doe");
        when(userRepo.findByExternalId("user123")).thenReturn(Optional.empty());
        when(userRepo.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);

        var captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepo).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("john@example.com");
        assertThat(captor.getValue().getName()).isEqualTo("John Doe");
        assertThat(captor.getValue().getExternalId()).isEqualTo("user123");
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @DisplayName("should not create AppUser if already exists")
    void shouldNotDuplicateUser() throws Exception {
        authenticateWith("user123", "john@example.com", "John Doe");
        when(userRepo.findByExternalId("user123")).thenReturn(Optional.of(new AppUser("john@example.com", "John", "user123")));

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);

        verify(userRepo, never()).save(any());
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @DisplayName("should use sub as email/name fallback when claims missing")
    void shouldFallbackToSub() throws Exception {
        authenticateWith("fallback-sub", null, null);
        when(userRepo.findByExternalId("fallback-sub")).thenReturn(Optional.empty());
        when(userRepo.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);

        var captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepo).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("fallback-sub");
        assertThat(captor.getValue().getName()).isEqualTo("fallback-sub");
    }

    @Test
    @DisplayName("should continue filter chain when no authentication")
    void shouldContinueWithoutAuth() throws Exception {
        // No SecurityContext set
        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), filterChain);

        verify(userRepo, never()).findByExternalId(any());
        verify(filterChain).doFilter(any(), any());
    }
}
