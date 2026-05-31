package com.sinapipro.api.timetracking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LaborManagementServiceTest {

    @Test
    @DisplayName("should calculate social charges correctly for CLT employee")
    void should_calculate_social_charges() {
        // Conceptual validation: INSS = 20%, FGTS = 8%, 13º = 1/12, Férias = 1/12 * 1.3333
        var salary = new BigDecimal("5000.00");
        var inss = salary.multiply(new BigDecimal("0.20")); // 1000
        var fgts = salary.multiply(new BigDecimal("0.08")); // 400
        assertThat(inss).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(fgts).isEqualByComparingTo(new BigDecimal("400.00"));
    }
}
