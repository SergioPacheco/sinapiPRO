package com.sinapipro.api.budget;

import com.sinapipro.api.TestcontainersConfiguration;
import com.sinapipro.api.config.SecurityProperties;
import com.sinapipro.api.security.application.JwtTokenService;
import com.sinapipro.api.security.api.TokenResponse;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class BudgetControllerIntegrationTest {

    @Autowired WebTestClient webTestClient;
    @Autowired JwtTokenService jwtTokenService;
    @Autowired SecurityProperties securityProperties;

    private String accessToken;

    @BeforeEach
    void setUp() {
        SecurityProperties.DemoUser demoUser = securityProperties.demoUser();
        TokenResponse tokenResponse = jwtTokenService.issueFromPassword(demoUser.username(), demoUser.password());
        accessToken = tokenResponse.accessToken();
    }

    @Nested
    @DisplayName("GET /api/v1/budgets")
    class ListBudgets {

        @Test
        @DisplayName("should return paginated budgets")
        void shouldReturnPaginatedBudgets() {
            webTestClient.get().uri("/api/v1/budgets")
                    .headers(h -> h.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isNumber()
                    .jsonPath("$.content").isArray();
        }

        @Test
        @DisplayName("should return 401 without token")
        void shouldReturn401WithoutToken() {
            webTestClient.get().uri("/api/v1/budgets")
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("POST /api/v1/budgets")
    class CreateBudget {

        @Test
        @DisplayName("should create a new budget and return 201")
        void shouldCreateBudget() {
            webTestClient.post().uri("/api/v1/budgets")
                    .headers(h -> h.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {"code":"BUD-IT-001","title":"Test","customerName":"Customer","totalAmount":100000,"status":"DRAFT","startDate":"2026-06-01","metadata":{}}
                            """)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().exists("Location")
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("BUD-IT-001");
        }
    }

    @Nested
    @DisplayName("GET /api/v1/budgets/{id}")
    class GetBudgetById {

        @Test
        @DisplayName("should return 404 for non-existent budget")
        void shouldReturn404ForNonExistent() {
            webTestClient.get().uri("/api/v1/budgets/00000000-0000-0000-0000-000000000000")
                    .headers(h -> h.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}
