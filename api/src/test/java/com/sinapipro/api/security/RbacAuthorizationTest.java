package com.sinapipro.api.security;

import com.sinapipro.api.TestcontainersConfiguration;
import com.sinapipro.api.config.SecurityProperties;
import com.sinapipro.api.security.domain.AppUser;
import com.sinapipro.api.security.domain.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class RbacAuthorizationTest {

    @Autowired WebTestClient webTestClient;
    @Autowired AppUserRepository userRepo;
    @Autowired SecurityProperties securityProperties;

    private String adminToken;

    @BeforeEach
    void setUp() {
        // Get admin token
        adminToken = webTestClient.post().uri("/api/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"grantType":"PASSWORD","username":"admin@sinapipro.dev","password":"SinapiPro#2026"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody() != null
                ? extractToken(webTestClient.post().uri("/api/v1/auth/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {"grantType":"PASSWORD","username":"admin@sinapipro.dev","password":"SinapiPro#2026"}
                            """)
                    .exchange()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody())
                : null;
    }

    private String extractToken(String body) {
        // Simple extraction: {"accessToken":"xxx",...}
        int start = body.indexOf("\"accessToken\":\"") + 15;
        int end = body.indexOf("\"", start);
        return body.substring(start, end);
    }

    @Nested
    @DisplayName("Unauthenticated requests")
    class Unauthenticated {

        @Test
        @DisplayName("should return 401 for protected endpoints without token")
        void shouldReturn401WithoutToken() {
            webTestClient.get().uri("/api/v1/projects")
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("should allow public endpoints without token")
        void shouldAllowPublicEndpoints() {
            webTestClient.get().uri("/actuator/health")
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @DisplayName("should allow swagger without token")
        void shouldAllowSwagger() {
            webTestClient.get().uri("/v3/api-docs")
                    .exchange()
                    .expectStatus().isOk();
        }
    }

    @Nested
    @DisplayName("Authenticated requests")
    class Authenticated {

        @Test
        @DisplayName("admin should access all endpoints")
        void adminShouldAccessAll() {
            webTestClient.get().uri("/api/v1/users")
                    .headers(h -> h.setBearerAuth(adminToken))
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @DisplayName("admin should access role management")
        void adminShouldAccessRoles() {
            webTestClient.get().uri("/api/v1/roles")
                    .headers(h -> h.setBearerAuth(adminToken))
                    .exchange()
                    .expectStatus().isOk();
        }
    }

    @Nested
    @DisplayName("User auto-provisioning")
    class AutoProvisioning {

        @Test
        @DisplayName("should auto-create AppUser on first authenticated request")
        void shouldAutoProvisionUser() {
            // The admin token has sub=admin@sinapipro.dev
            webTestClient.get().uri("/api/v1/users")
                    .headers(h -> h.setBearerAuth(adminToken))
                    .exchange()
                    .expectStatus().isOk();

            // Verify user was provisioned
            var user = userRepo.findByExternalId("admin@sinapipro.dev");
            assert user.isPresent() : "AppUser should be auto-provisioned";
        }
    }

    @Nested
    @DisplayName("Project-scoped access")
    class ProjectAccess {

        @Test
        @DisplayName("user with empty projectAccess should access any project")
        void emptyProjectAccessMeansAll() {
            UUID projectId = UUID.randomUUID();
            webTestClient.get().uri("/api/v1/projects/{projectId}/budgets", projectId)
                    .headers(h -> h.setBearerAuth(adminToken))
                    .exchange()
                    .expectStatus().isNotFound(); // 404 = passed auth, project just doesn't exist
        }

        @Test
        @DisplayName("user with restricted projectAccess should get 403 for other projects")
        void restrictedProjectAccessShouldBlock() {
            // First, ensure user is provisioned
            webTestClient.get().uri("/api/v1/users")
                    .headers(h -> h.setBearerAuth(adminToken))
                    .exchange()
                    .expectStatus().isOk();

            // Restrict user to a specific project
            var user = userRepo.findByExternalId("admin@sinapipro.dev").orElseThrow();
            UUID allowedProject = UUID.randomUUID();
            user.grantProjectAccess(allowedProject);
            userRepo.save(user);

            // Access to allowed project should pass auth (404 because project doesn't exist in DB)
            webTestClient.get().uri("/api/v1/projects/{projectId}/budgets", allowedProject)
                    .headers(h -> h.setBearerAuth(adminToken))
                    .exchange()
                    .expectStatus().isNotFound();

            // Access to different project should be 403
            UUID blockedProject = UUID.randomUUID();
            webTestClient.get().uri("/api/v1/projects/{projectId}/budgets", blockedProject)
                    .headers(h -> h.setBearerAuth(adminToken))
                    .exchange()
                    .expectStatus().isForbidden();

            // Cleanup: remove restriction
            user.revokeProjectAccess(allowedProject);
            userRepo.save(user);
        }
    }
}
