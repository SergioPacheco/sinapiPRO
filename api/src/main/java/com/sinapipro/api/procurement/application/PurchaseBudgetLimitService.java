package com.sinapipro.api.procurement.application;

import com.sinapipro.api.procurement.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class PurchaseBudgetLimitService {

    private final PurchaseBudgetLimitRepository limitRepository;

    public PurchaseBudgetLimitService(PurchaseBudgetLimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    public PurchaseBudgetLimit create(UUID projectId, LocalDate periodStart, LocalDate periodEnd,
                                       BigDecimal limitAmount, BigDecimal requiresAuthAbove) {
        return limitRepository.save(new PurchaseBudgetLimit(projectId, periodStart, periodEnd, limitAmount, requiresAuthAbove));
    }

    /**
     * Verifica se uma compra pode ser realizada dentro do limite.
     * Retorna resultado com flag de autorização necessária.
     */
    public LimitCheckResult checkLimit(UUID projectId, BigDecimal amount) {
        var limit = limitRepository.findActiveForProject(projectId, LocalDate.now());
        if (limit.isEmpty()) {
            return new LimitCheckResult(true, false, null, null);
        }

        var budgetLimit = limit.get();
        var canProceed = budgetLimit.canConsume(amount);
        var needsAuth = budgetLimit.requiresAuthorization(amount);
        return new LimitCheckResult(canProceed, needsAuth, budgetLimit.getAvailableAmount(), budgetLimit.getLimitAmount());
    }

    /**
     * Consome valor do limite (chamado após aprovação do pedido).
     */
    public void consumeLimit(UUID projectId, BigDecimal amount) {
        var limit = limitRepository.findActiveForProject(projectId, LocalDate.now());
        limit.ifPresent(l -> {
            l.consume(amount);
            limitRepository.save(l);
        });
    }

    public record LimitCheckResult(boolean canProceed, boolean requiresAuthorization,
                                    BigDecimal available, BigDecimal total) {}
}
