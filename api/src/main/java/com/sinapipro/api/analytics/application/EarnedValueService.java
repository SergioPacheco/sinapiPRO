package com.sinapipro.api.analytics.application;

import module java.base;

import com.sinapipro.api.jobcosting.domain.CostCode;
import com.sinapipro.api.jobcosting.domain.CostCodeRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionRepository;
import com.sinapipro.api.jobcosting.domain.CostTransactionType;
import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public EvmResult calculate(UUID budgetId) {
        var activities = scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId);

        var bac = costCodeRepository.findByBudgetIdOrderByCode(budgetId).stream()
                .map(CostCode::getBudgetedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (bac.compareTo(BigDecimal.ZERO) == 0) {
            return EvmResult.empty();
        }

        var totalWeight = activities.stream().map(ScheduleActivity::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        var actualProgress = BigDecimal.ZERO;

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            for (var a : activities) {
                var weightFraction = a.getWeight().divide(totalWeight, 6, RoundingMode.HALF_UP);
                actualProgress = actualProgress.add(weightFraction.multiply(a.getProgressPct())
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            }
        }

        var pv = bac;
        var ev = bac.multiply(actualProgress).setScale(2, RoundingMode.HALF_UP);
        var ac = costTransactionRepository.sumByBudgetAndType(budgetId, CostTransactionType.ACTUAL);

        var cpi = ac.compareTo(BigDecimal.ZERO) > 0 ? ev.divide(ac, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        var spi = pv.compareTo(BigDecimal.ZERO) > 0 ? ev.divide(pv, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        var eac = cpi.compareTo(BigDecimal.ZERO) > 0 ? bac.divide(cpi, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        var vac = bac.subtract(eac);

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
