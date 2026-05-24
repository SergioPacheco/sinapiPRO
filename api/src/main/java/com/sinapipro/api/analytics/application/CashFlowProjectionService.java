package com.sinapipro.api.analytics.application;

import com.sinapipro.api.finance.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class CashFlowProjectionService {

    private final PayableInstallmentRepository payableInstallmentRepository;
    private final ReceivableInstallmentRepository receivableInstallmentRepository;

    public CashFlowProjectionService(PayableInstallmentRepository payableInstallmentRepository,
                                      ReceivableInstallmentRepository receivableInstallmentRepository) {
        this.payableInstallmentRepository = payableInstallmentRepository;
        this.receivableInstallmentRepository = receivableInstallmentRepository;
    }

    /**
     * Fluxo de caixa projetado: entradas × saídas × saldo por mês.
     */
    public List<MonthlyFlow> project(int monthsAhead) {
        var today = LocalDate.now();
        var endDate = today.plusMonths(monthsAhead);

        var payables = payableInstallmentRepository.findByStatusAndDueDateBefore(InstallmentStatus.OPEN, endDate);
        var receivables = receivableInstallmentRepository.findByStatusAndDueDateBefore(InstallmentStatus.OPEN, endDate);

        var flows = new TreeMap<YearMonth, MonthlyFlow>();

        for (int i = 0; i <= monthsAhead; i++) {
            var month = YearMonth.from(today.plusMonths(i));
            flows.put(month, new MonthlyFlow(month, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        }

        for (var p : payables) {
            var month = YearMonth.from(p.getDueDate());
            if (flows.containsKey(month)) {
                var f = flows.get(month);
                flows.put(month, new MonthlyFlow(month, f.inflows(), f.outflows().add(p.getAmount()), BigDecimal.ZERO));
            }
        }

        for (var r : receivables) {
            var month = YearMonth.from(r.getDueDate());
            if (flows.containsKey(month)) {
                var f = flows.get(month);
                flows.put(month, new MonthlyFlow(month, f.inflows().add(r.getAmount()), f.outflows(), BigDecimal.ZERO));
            }
        }

        // Calcular saldo acumulado
        var result = new ArrayList<MonthlyFlow>();
        var accumulated = BigDecimal.ZERO;
        for (var f : flows.values()) {
            accumulated = accumulated.add(f.inflows()).subtract(f.outflows());
            result.add(new MonthlyFlow(f.month(), f.inflows(), f.outflows(), accumulated));
        }

        return result;
    }

    public record MonthlyFlow(YearMonth month, BigDecimal inflows, BigDecimal outflows, BigDecimal balance) {}
}
