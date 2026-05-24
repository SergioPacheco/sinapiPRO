package com.sinapipro.api.security.domain;

import java.util.Map;
import java.util.Set;

import static com.sinapipro.api.security.domain.Permissions.*;

/**
 * Perfis pré-definidos do sistema.
 * Cada perfil tem um conjunto de permissões que define o que o usuário pode fazer.
 */
public final class DefaultRoles {
    private DefaultRoles() {}

    public static final Map<String, RoleDefinition> ROLES = Map.of(
        "ADMIN", new RoleDefinition("Administrador",
            "Acesso total ao sistema. Gerencia usuários, configurações e todos os módulos.",
            Set.of(ADMIN_FULL)),

        "ENGENHEIRO", new RoleDefinition("Engenheiro de Obra",
            "Gerencia obras, orçamentos, medições, cronograma. Aprova medições.",
            Set.of(PROJECT_READ, PROJECT_WRITE, BUDGET_READ, BUDGET_WRITE, BUDGET_EFFECTUATE, BUDGET_EXPORT,
                   MEASUREMENT_READ, MEASUREMENT_WRITE, MEASUREMENT_APPROVE, MEASUREMENT_REJECT,
                   PROCUREMENT_READ, PROCUREMENT_WRITE, FINANCE_READ, LABOR_READ, LABOR_WRITE,
                   REGISTRY_READ, REPORT_READ, REPORT_EXPORT, SINAPI_IMPORT)),

        "COMPRADOR", new RoleDefinition("Comprador / Suprimentos",
            "Gerencia requisições, cotações, pedidos de compra. Aprova pedidos dentro da alçada.",
            Set.of(PROJECT_READ, BUDGET_READ, PROCUREMENT_READ, PROCUREMENT_WRITE, PROCUREMENT_APPROVE,
                   REGISTRY_READ, REGISTRY_WRITE, REPORT_READ)),

        "FINANCEIRO", new RoleDefinition("Financeiro / Controladoria",
            "Gerencia contas a pagar/receber, movimentação bancária, conciliação, DRE.",
            Set.of(PROJECT_READ, BUDGET_READ, FINANCE_READ, FINANCE_WRITE, FINANCE_PAY, FINANCE_RECEIVE,
                   FINANCE_RECONCILE, COMMERCIAL_READ, REPORT_READ, REPORT_EXPORT, REGISTRY_READ)),

        "MESTRE_OBRA", new RoleDefinition("Mestre de Obras",
            "Preenche diário de obra, apontamento de horas, registra medições.",
            Set.of(PROJECT_READ, BUDGET_READ, MEASUREMENT_READ, MEASUREMENT_WRITE,
                   LABOR_READ, LABOR_WRITE, PROCUREMENT_READ, REPORT_READ)),

        "COMERCIAL", new RoleDefinition("Comercial / Vendas",
            "Gerencia empreendimentos, contratos de venda, comissões.",
            Set.of(PROJECT_READ, COMMERCIAL_READ, COMMERCIAL_WRITE, COMMERCIAL_CANCEL,
                   FINANCE_READ, REGISTRY_READ, REGISTRY_WRITE, REPORT_READ)),

        "SEGURANCA", new RoleDefinition("Técnico de Segurança",
            "Gerencia inspeções, EPIs, treinamentos, incidentes.",
            Set.of(PROJECT_READ, LABOR_READ, REGISTRY_READ, REPORT_READ)),

        "VISUALIZADOR", new RoleDefinition("Visualizador",
            "Acesso somente leitura a todos os módulos. Não pode alterar nada.",
            Set.of(PROJECT_READ, BUDGET_READ, MEASUREMENT_READ, PROCUREMENT_READ,
                   FINANCE_READ, COMMERCIAL_READ, LABOR_READ, REGISTRY_READ, REPORT_READ))
    );

    public record RoleDefinition(String description, String fullDescription, Set<String> permissions) {}
}
