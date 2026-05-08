package com.sinapipro.api.security;

import com.sinapipro.api.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
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
class AuthControllerIntegrationTest {

    @Autowired WebTestClient webTestClient;

    @Test
    @DisplayName("should issue access and refresh tokens with valid credentials")
    void shouldIssueTokens() {
        webTestClient.post().uri("/api/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"grantType":"PASSWORD","username":"admin@sinapipro.dev","password":"SinapiPro#2026"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isNotEmpty()
                .jsonPath("$.refreshToken").isNotEmpty()
                .jsonPath("$.tokenType").isEqualTo("Bearer");
    }

    @Test
    @DisplayName("should return 401 for invalid credentials")
    void shouldReturn401ForInvalidCredentials() {
        webTestClient.post().uri("/api/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"grantType":"PASSWORD","username":"wrong@email.com","password":"wrong"}
                        """)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
