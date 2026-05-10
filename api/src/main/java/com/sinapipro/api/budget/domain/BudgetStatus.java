package com.sinapipro.api.budget.domain;

public enum BudgetStatus {
    DRAFT,          // Rascunho
    IN_REVIEW,      // Em análise
    APPROVED,       // Aprovado
    REJECTED,       // Reprovado
    SUPERSEDED,     // Substituído (por versão mais nova)
    IN_EXECUTION,   // Em execução
    COMPLETED,      // Concluído
    CANCELLED       // Cancelado
}
