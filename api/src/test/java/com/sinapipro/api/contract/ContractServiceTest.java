package com.sinapipro.api.contract;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.contract.application.ContractService;
import com.sinapipro.api.contract.application.ContractService.ContractFinancialSummary;
import com.sinapipro.api.contract.domain.*;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock ContractRepository contractRepository;
    @Mock BudgetRepository budgetRepository;
    @Mock SupplierRepository supplierRepository;

    ContractService service;

    @BeforeEach
    void setUp() {
        service = new ContractService(contractRepository, budgetRepository, supplierRepository);
    }

    @Test
    @DisplayName("should activate a DRAFT contract")
    void shouldActivate() {
        UUID contractId = UUID.randomUUID();
        Contract contract = createContract();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Contract result = service.activate(contractId);

        assertThat(result.getStatus()).isEqualTo(ContractStatus.ACTIVE);
    }

    @Test
    @DisplayName("should reject adding change order to DRAFT contract")
    void shouldRejectChangeOrderOnDraft() {
        UUID contractId = UUID.randomUUID();
        Contract contract = createContract(); // DRAFT

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

        assertThatThrownBy(() -> service.addChangeOrder(contractId, 1, "Extra work", new BigDecimal("5000"), "Scope change"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACTIVE");
    }

    @Test
    @DisplayName("should calculate financial summary with approved change orders")
    void shouldCalculateFinancialSummary() {
        UUID contractId = UUID.randomUUID();
        Contract contract = createContract();
        contract.activate();

        // Add and approve a change order
        ChangeOrder co = new ChangeOrder(contract, 1, "Extra foundation", new BigDecimal("15000.00"), "Soil conditions");
        contract.getChangeOrders().add(co);
        co.approve();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

        ContractFinancialSummary summary = service.financialSummary(contractId);

        // Original: 100000, Change order: 15000, Updated: 115000
        assertThat(summary.originalValue()).isEqualByComparingTo("100000.00");
        assertThat(summary.changeOrderTotal()).isEqualByComparingTo("15000.00");
        assertThat(summary.updatedValue()).isEqualByComparingTo("115000.00");
        // Retention: 5% of 115000 = 5750
        assertThat(summary.retentionAmount()).isEqualByComparingTo("5750.00");
        assertThat(summary.netPayable()).isEqualByComparingTo("109250.00");
        assertThat(summary.approvedChangeOrders()).isEqualTo(1);
    }

    @Test
    @DisplayName("should not include rejected change orders in updated value")
    void shouldExcludeRejectedChangeOrders() {
        UUID contractId = UUID.randomUUID();
        Contract contract = createContract();
        contract.activate();

        ChangeOrder approved = new ChangeOrder(contract, 1, "Approved", new BigDecimal("10000"), "ok");
        approved.approve();
        ChangeOrder rejected = new ChangeOrder(contract, 2, "Rejected", new BigDecimal("20000"), "no");
        rejected.reject();
        contract.getChangeOrders().add(approved);
        contract.getChangeOrders().add(rejected);

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));

        ContractFinancialSummary summary = service.financialSummary(contractId);

        // Only approved: 100000 + 10000 = 110000
        assertThat(summary.updatedValue()).isEqualByComparingTo("110000.00");
    }

    private Contract createContract() {
        Budget budget = mock(Budget.class);
        Supplier supplier = mock(Supplier.class);
        lenient().when(supplier.getName()).thenReturn("Construtora ABC");
        return new Contract(budget, supplier, "CT-001", "Foundation work",
                new BigDecimal("100000.00"), new BigDecimal("0.05"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30));
    }
}
