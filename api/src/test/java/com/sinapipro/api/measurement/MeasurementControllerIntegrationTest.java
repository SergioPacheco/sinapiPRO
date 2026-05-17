package com.sinapipro.api.measurement;

import com.sinapipro.api.TestcontainersConfiguration;
import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.budget.domain.BudgetStatus;
import com.sinapipro.api.config.SecurityProperties;
import com.sinapipro.api.measurement.application.MeasurementService;
import com.sinapipro.api.security.api.TokenResponse;
import com.sinapipro.api.security.application.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class MeasurementControllerIntegrationTest {

    @Autowired WebTestClient webTestClient;
    @Autowired JwtTokenService jwtTokenService;
    @Autowired SecurityProperties securityProperties;
    @Autowired BudgetRepository budgetRepository;
    @Autowired MeasurementService measurementService;

    private String accessToken;

    @BeforeEach
    void setUp() {
        SecurityProperties.DemoUser demoUser = securityProperties.demoUser();
        TokenResponse tokenResponse = jwtTokenService.issueFromPassword(demoUser.username(), demoUser.password());
        accessToken = tokenResponse.accessToken();
    }

    @Test
    @DisplayName("should reject measurement and expose approval history")
    void shouldRejectMeasurementAndExposeHistory() {
        Budget budget = budgetRepository.save(new Budget("MEAS-CTRL-" + UUID.randomUUID(), "Measurement Test", "Client",
                new BigDecimal("100000"), BudgetStatus.DRAFT, LocalDate.now(), null, Map.of()));
        var measurement = measurementService.create(
                budget.getId(),
                1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                new BigDecimal("0.05"),
                List.of(new MeasurementService.ItemInput(null, null, "Servico", new BigDecimal("2"), new BigDecimal("100")))
        );
        measurementService.submit(measurement.getId());

        webTestClient.post()
                .uri("/api/v1/projects/{projectId}/measurements/{id}/reject", budget.getId(), measurement.getId())
                .headers(h -> h.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"reason":"Divergencia de quantidade","performedBy":"Fiscal QA"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("DRAFT")
                .jsonPath("$.rejectionReason").isEqualTo("Divergencia de quantidade");

        webTestClient.get()
                .uri("/api/v1/projects/{projectId}/measurements/{id}/history", budget.getId(), measurement.getId())
                .headers(h -> h.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].action").isEqualTo("REJECT")
                .jsonPath("$[0].performedBy").isEqualTo("Fiscal QA")
                .jsonPath("$[0].reason").isEqualTo("Divergencia de quantidade");
    }

    @Test
    @DisplayName("should save and fetch memo for measurement item")
    void shouldSaveAndFetchMemoForItem() {
        Budget budget = budgetRepository.save(new Budget("MEAS-MEMO-" + UUID.randomUUID(), "Measurement Memo", "Client",
                new BigDecimal("100000"), BudgetStatus.DRAFT, LocalDate.now(), null, Map.of()));
        var measurement = measurementService.create(
                budget.getId(),
                1,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 28),
                new BigDecimal("0.05"),
                List.of(new MeasurementService.ItemInput(null, null, "Servico memo", new BigDecimal("3"), new BigDecimal("120")))
        );
        UUID itemId = measurement.getItems().getFirst().getId();

        webTestClient.put()
                .uri("/api/v1/projects/{projectId}/measurements/{id}/items/{itemId}/memo", budget.getId(), measurement.getId(), itemId)
                .headers(h -> h.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"lines":[{"description":"Trecho 1","formula":"1+1","value":2.0},{"description":"Trecho 2","formula":"2+2","value":4.0}]}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.measurementItemId").isEqualTo(itemId.toString())
                .jsonPath("$.result").isEqualTo(6);

        webTestClient.get()
                .uri("/api/v1/projects/{projectId}/measurements/{id}/items/{itemId}/memo", budget.getId(), measurement.getId(), itemId)
                .headers(h -> h.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.lines.length()").isEqualTo(2)
                .jsonPath("$.lines[0].description").isEqualTo("Trecho 1")
                .jsonPath("$.result").isEqualTo(6);
    }

    @Test
    @DisplayName("should add extra item to measurement")
    void shouldAddExtraItemToMeasurement() {
        Budget budget = budgetRepository.save(new Budget("MEAS-EXTRA-" + UUID.randomUUID(), "Measurement Extra", "Client",
                new BigDecimal("100000"), BudgetStatus.DRAFT, LocalDate.now(), null, Map.of()));
        var measurement = measurementService.create(
                budget.getId(),
                1,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31),
                new BigDecimal("0.05"),
                List.of(new MeasurementService.ItemInput(null, null, "Servico base", new BigDecimal("1"), new BigDecimal("200")))
        );

        webTestClient.post()
                .uri("/api/v1/projects/{projectId}/measurements/{id}/extra-items", budget.getId(), measurement.getId())
                .headers(h -> h.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"description":"Servico extra","quantity":5,"unitPrice":50,"contractorName":"Empreiteira X"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items.length()").isEqualTo(2)
                .jsonPath("$.items[1].extra").isEqualTo(true)
                .jsonPath("$.items[1].contractorName").isEqualTo("Empreiteira X");
    }
}
