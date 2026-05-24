package com.sinapipro.api.report;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FinanceReportService {

    private final ReportService reportService;
    private final PayableInstallmentRepository payableInstallmentRepo;
    private final ReceivableInstallmentRepository receivableInstallmentRepo;
    private final BankTransactionRepository bankTransactionRepo;
    private final PayableRepository payableRepo;
    private final ReceivableRepository receivableRepo;

    public FinanceReportService(ReportService reportService, PayableInstallmentRepository payableInstallmentRepo,
                                 ReceivableInstallmentRepository receivableInstallmentRepo,
                                 BankTransactionRepository bankTransactionRepo,
                                 PayableRepository payableRepo, ReceivableRepository receivableRepo) {
        this.reportService = reportService; this.payableInstallmentRepo = payableInstallmentRepo;
        this.receivableInstallmentRepo = receivableInstallmentRepo; this.bankTransactionRepo = bankTransactionRepo;
        this.payableRepo = payableRepo; this.receivableRepo = receivableRepo;
    }

    public byte[] boleto(UUID installmentId) { return reportService.generatePdf("reports/finance/boleto.jte", Map.of("installment", receivableInstallmentRepo.findById(installmentId).orElseThrow())); }
    public byte[] recibo(UUID installmentId) { return reportService.generatePdf("reports/finance/recibo.jte", Map.of("installment", payableInstallmentRepo.findById(installmentId).orElseThrow())); }
    public byte[] extratoConta(UUID bankAccountId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/extrato-conta.jte", Map.of("transactions", bankTransactionRepo.findByBankAccountIdAndTransactionDateBetween(bankAccountId, from, to), "from", from, "to", to)); }
    public byte[] extratoMovBancaria(UUID bankAccountId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/extrato-mov-bancaria.jte", Map.of("transactions", bankTransactionRepo.findByBankAccountIdAndTransactionDateBetween(bankAccountId, from, to), "from", from, "to", to)); }
    public byte[] agingPagar() { return reportService.generatePdf("reports/finance/aging-pagar.jte", Map.of("overdue", payableInstallmentRepo.findByStatusAndDueDateBefore(InstallmentStatus.OPEN, LocalDate.now()), "date", LocalDate.now())); }
    public byte[] agingReceber() { return reportService.generatePdf("reports/finance/aging-receber.jte", Map.of("overdue", receivableInstallmentRepo.findByStatusAndDueDateBefore(InstallmentStatus.OPEN, LocalDate.now()), "date", LocalDate.now())); }
    public byte[] dre(UUID budgetId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/dre.jte", Map.of("payables", payableRepo.findByBudgetIdAndDueDateBetween(budgetId, from, to), "receivables", receivableRepo.findByBudgetIdAndDueDateBetween(budgetId, from, to), "from", from, "to", to)); }
    public byte[] fluxoCaixa(int months) { return reportService.generatePdf("reports/finance/fluxo-caixa.jte", Map.of("months", months)); }
    public byte[] balancete(UUID budgetId, LocalDate from, LocalDate to) { return reportService.generatePdf("reports/finance/balancete.jte", Map.of("payables", payableRepo.findByBudgetIdAndDueDateBetween(budgetId, from, to), "receivables", receivableRepo.findByBudgetIdAndDueDateBetween(budgetId, from, to), "from", from, "to", to)); }
    public byte[] mapaCustos(UUID budgetId) { return reportService.generatePdf("reports/finance/mapa-custos.jte", Map.of("budgetId", budgetId)); }
}
