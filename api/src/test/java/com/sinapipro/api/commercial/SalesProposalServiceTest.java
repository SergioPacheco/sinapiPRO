package com.sinapipro.api.commercial;

import com.sinapipro.api.commercial.application.SalesProposalService;
import com.sinapipro.api.commercial.application.SalesProposalService.InstallmentSimulation;
import com.sinapipro.api.commercial.domain.SaleContractRepository;
import com.sinapipro.api.commercial.domain.SaleInstallmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SalesProposalServiceTest {

    @Mock SaleContractRepository contractRepo;
    @Mock SaleInstallmentRepository installmentRepo;
    SalesProposalService service;

    @BeforeEach
    void setUp() { service = new SalesProposalService(contractRepo, installmentRepo); }

    @Test
    @DisplayName("should simulate PRICE installments without interest")
    void should_simulate_price_no_interest() {
        var result = service.simulate(
                new BigDecimal("300000"), new BigDecimal("30000"),
                36, null, "PRICE", LocalDate.of(2026, 7, 1));

        assertThat(result.installments()).hasSize(37); // 1 down + 36 monthly
        assertThat(result.installments().getFirst().type()).isEqualTo("ENTRADA");
        assertThat(result.installments().getFirst().amount()).isEqualByComparingTo(new BigDecimal("30000"));
        // 270000 / 36 = 7500
        assertThat(result.installments().get(1).amount()).isEqualByComparingTo(new BigDecimal("7500.00"));
    }

    @Test
    @DisplayName("should simulate SAC installments with decreasing values")
    void should_simulate_sac_with_interest() {
        var result = service.simulate(
                new BigDecimal("200000"), BigDecimal.ZERO,
                24, new BigDecimal("1.0"), "SAC", LocalDate.of(2026, 7, 1));

        assertThat(result.installments()).hasSize(24);
        // SAC: first installment > last installment (decreasing)
        var first = result.installments().getFirst().amount();
        var last = result.installments().getLast().amount();
        assertThat(first).isGreaterThan(last);
    }

    @Test
    @DisplayName("should calculate total interest in simulation")
    void should_calculate_total_interest() {
        var result = service.simulate(
                new BigDecimal("100000"), BigDecimal.ZERO,
                12, new BigDecimal("1.0"), "PRICE", LocalDate.of(2026, 7, 1));

        assertThat(result.totalInterest()).isPositive();
        assertThat(result.totalPaid()).isGreaterThan(new BigDecimal("100000"));
    }
}
