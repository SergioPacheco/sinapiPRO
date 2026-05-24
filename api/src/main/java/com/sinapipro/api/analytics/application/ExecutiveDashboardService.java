package com.sinapipro.api.analytics.application;

import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.budget.domain.BudgetStatus;
import com.sinapipro.api.finance.domain.*;
import com.sinapipro.api.project.domain.ProjectRepository;
import com.sinapipro.api.project.domain.ProjectStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class ExecutiveDashboardService {

    private final ProjectRepository projectRepository;
    private final BudgetRepository budgetRepository;
    private final PayableInstallmentRepository payableInstallmentRepository;
    private final ReceivableInstallmentRepository receivableInstallmentRepository;
    private final AgingReportService agingReportService;

    public ExecutiveDashboardService(ProjectRepository projectRepository, BudgetRepository budgetRepository,
                                      PayableInstallmentRepository payableInstallmentRepository,
                                      ReceivableInstallmentRepository receivableInstallmentRepository,
                                      AgingReportService agingReportService) {
        this.projectRepository = projectRepository;
        this.budgetRepository = budgetRepository;
        this.payableInstallmentRepository = payableInstallmentRepository;
        this.receivableInstallmentRepository = receivableInstallmentRepository;
        this.agingReportService = agingReportService;
    }

    /**
     * Dashboard executivo: KPIs consolidados multi-obra.
     */
    public ExecutiveDashboard generate() {
        var activeProjects = projectRepository.countByStatus(ProjectStatus.IN_PROGRESS);
        var activeBudgets = budgetRepository.countByStatus(BudgetStatus.IN_EXECUTION);

        var receivableAging = agingReportService.receivableAging();
        var payableAging = agingReportService.payableAging();

        return new ExecutiveDashboard(
                activeProjects, activeBudgets,
                receivableAging.total(), payableAging.total(),
                receivableAging.count(), payableAging.count()
        );
    }

    public record ExecutiveDashboard(long activeProjects, long activeBudgets,
                                      BigDecimal totalReceivableOverdue, BigDecimal totalPayableOverdue,
                                      int overdueReceivableCount, int overduePayableCount) {}
}
