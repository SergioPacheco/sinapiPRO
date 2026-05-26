package com.sinapipro.api.finance;

import com.sinapipro.api.finance.application.AgingReportService;
import com.sinapipro.api.finance.application.CostApportionmentService;
import com.sinapipro.api.finance.application.DreService;
import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.project.domain.Project;
import com.sinapipro.api.project.domain.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServicesTest {

    @Nested
    @DisplayName("AgingReportService")
    class AgingTests {
        @Mock PayableRepository payableRepo;
        @Mock ReceivableRepository receivableRepo;
        AgingReportService service;

        @BeforeEach
        void setUp() { service = new AgingReportService(payableRepo, receivableRepo); }

        @Test
        @DisplayName("should classify overdue payables into aging buckets")
        void should_classify_overdue_into_buckets() {
            var p1 = createPayable(LocalDate.now().minusDays(10), new BigDecimal("1000"));
            var p2 = createPayable(LocalDate.now().minusDays(45), new BigDecimal("2000"));
            var p3 = createPayable(LocalDate.now().minusDays(100), new BigDecimal("5000"));
            when(payableRepo.findAll()).thenReturn(List.of(p1, p2, p3));

            var report = service.payablesAging(null);

            assertThat(report.bucket1to30().count()).isEqualTo(1);
            assertThat(report.bucket31to60().count()).isEqualTo(1);
            assertThat(report.bucket90plus().count()).isEqualTo(1);
            assertThat(report.totalOverdue()).isEqualByComparingTo(new BigDecimal("8000"));
        }

        @Test
        @DisplayName("should return empty report when no overdue")
        void should_return_empty_when_no_overdue() {
            when(payableRepo.findAll()).thenReturn(List.of());
            var report = service.payablesAging(null);
            assertThat(report.totalCount()).isZero();
        }

        private Payable createPayable(LocalDate dueDate, BigDecimal amount) {
            var p = new Payable(UUID.randomUUID(), null, "Test", amount, dueDate, "MATERIAL");
            return p;
        }
    }

    @Nested
    @DisplayName("DreService")
    class DreTests {
        @Mock ReceivableRepository receivableRepo;
        @Mock PayableRepository payableRepo;
        DreService service;

        @BeforeEach
        void setUp() { service = new DreService(receivableRepo, payableRepo); }

        @Test
        @DisplayName("should calculate net result as revenue minus expenses")
        void should_calculate_net_result() {
            var projectId = UUID.randomUUID();
            var r = new Receivable(UUID.randomUUID(), "Medição 1", new BigDecimal("50000"), LocalDate.now(), "MEDICAO");
            setProjectId(r, projectId);
            var p = new Payable(UUID.randomUUID(), null, "Material", new BigDecimal("30000"), LocalDate.now(), "MATERIAL");
            setProjectId(p, projectId);

            when(receivableRepo.findAll()).thenReturn(List.of(r));
            when(payableRepo.findAll()).thenReturn(List.of(p));

            var dre = service.generate(projectId, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1));

            assertThat(dre.totalRevenue()).isEqualByComparingTo(new BigDecimal("50000"));
            assertThat(dre.totalExpenses()).isEqualByComparingTo(new BigDecimal("30000"));
            assertThat(dre.netResult()).isEqualByComparingTo(new BigDecimal("20000"));
        }

        private void setProjectId(Receivable r, UUID id) { r.setProjectId(id); }
        private void setProjectId(Payable p, UUID id) { p.setProjectId(id); }
    }

    @Nested
    @DisplayName("CostApportionmentService")
    class ApportionmentTests {
        @Mock PayableRepository payableRepo;
        @Mock ProjectRepository projectRepo;
        CostApportionmentService service;

        @BeforeEach
        void setUp() { service = new CostApportionmentService(payableRepo, projectRepo); }

        @Test
        @DisplayName("should reject distribution that doesn't sum to 100")
        void should_reject_invalid_distribution() {
            var dist = Map.of(UUID.randomUUID(), new BigDecimal("60"), UUID.randomUUID(), new BigDecimal("30"));
            assertThatThrownBy(() -> service.apportion("Test", new BigDecimal("10000"),
                    LocalDate.now(), "ADMIN", null, dist))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100");
        }

        @Test
        @DisplayName("should distribute amount proportionally")
        void should_distribute_proportionally() {
            var p1 = UUID.randomUUID();
            var p2 = UUID.randomUUID();
            var proj1 = new Project("P1", "Obra 1", "Cliente");
            var proj2 = new Project("P2", "Obra 2", "Cliente");

            when(projectRepo.findById(p1)).thenReturn(Optional.of(proj1));
            when(projectRepo.findById(p2)).thenReturn(Optional.of(proj2));
            when(payableRepo.saveAll(any())).thenAnswer(i -> i.getArgument(0));

            var dist = new LinkedHashMap<UUID, BigDecimal>();
            dist.put(p1, new BigDecimal("70"));
            dist.put(p2, new BigDecimal("30"));

            var result = service.apportion("Aluguel", new BigDecimal("10000"),
                    LocalDate.now(), "ADMIN", null, dist);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getAmount()).isEqualByComparingTo(new BigDecimal("7000.00"));
            assertThat(result.get(1).getAmount()).isEqualByComparingTo(new BigDecimal("3000.00"));
        }
    }
}
