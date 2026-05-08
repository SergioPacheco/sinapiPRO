package com.sinapipro.api.analytics.application;

import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EarnedValueService {

    private final ScheduleActivityRepository scheduleRepository;
    private final CostTransactionRepository costTransactionRepository;
    private final CostCodeRepository costCodeRepository;

    public EarnedValueService(ScheduleActivityRepository scheduleRepository,
                              CostTransactionRepository costTransactionRepository,
                              CostCodeRepository costCodeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.costTransactionRepository = costTransactionRepository;
        this.costCodeRepository = costCodeRepository;
    }

    /**
     * EVM calculation (PMBOK / NBR ISO 21500):
     * BAC = Budget At Completion (sum of cost code budgeted amounts)
     * PV = Planned Value (BAC × planned progress)
     * EV = Earned Value (BAC × actual progress)
     * AC = Actual Cost (sum of ACTUAL transactions)
     * CPI = EV / AC
     * SPI = EV / PV
     * EAC = BAC / CPI
     * VAC = BAC - EAC
     */
    public EvmResult calculate(UUID budgetId) {
        List<ScheduleActivity> activities = scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId);

        // BAC from cost codes (actual monetary value)
        BigDecimal bac = costCodeRepository.findByBudgetIdOrderByCode(budgetId).stream()
                .map(CostCode::getBudgetedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (bac.compareTo(BigDecimal.ZERO) == 0) {
            return EvmResult.empty();
        }

        // Calculate progress from schedule activities
        BigDecimal totalWeight = activities.stream().map(ScheduleActivity::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal actualProgress = BigDecimal.ZERO;

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            for (ScheduleActivity a : activities) {
                BigDecimal weightFraction = a.getWeight().divide(totalWeight, 6, RoundingMode.HALF_UP);
                actualProgress = actualProgress.add(weightFraction.multiply(a.getProgressPct())
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            }
        }

        // PV = BAC (assume all planned to be done by now — simplified)
        BigDecimal pv = bac;
        // EV = BAC × actual progress
        BigDecimal ev = bac.multiply(actualProgress).setScale(2, RoundingMode.HALF_UP);
        // AC from cost transactions
        BigDecimal ac = costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL);

        BigDecimal cpi = ac.compareTo(BigDecimal.ZERO) > 0 ? ev.divide(ac, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal spi = pv.compareTo(BigDecimal.ZERO) > 0 ? ev.divide(pv, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal eac = cpi.compareTo(BigDecimal.ZERO) > 0 ? bac.divide(cpi, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal vac = bac.subtract(eac);

        return new EvmResult(bac, pv, ev, ac, cpi, spi, eac, vac);
    }

    public record EvmResult(BigDecimal bac, BigDecimal pv, BigDecimal ev, BigDecimal ac,
                            BigDecimal cpi, BigDecimal spi, BigDecimal eac, BigDecimal vac) {
        static EvmResult empty() {
            return new EvmResult(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}
