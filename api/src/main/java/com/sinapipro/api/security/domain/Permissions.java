package com.sinapipro.api.security.domain;

/**
 * Permissões granulares do sistema.
 * Formato: {módulo}.{ação}
 * Ações: read, write, delete, approve, export
 */
public final class Permissions {
    private Permissions() {}

    // Obras
    public static final String PROJECT_READ = "project.read";
    public static final String PROJECT_WRITE = "project.write";
    public static final String PROJECT_DELETE = "project.delete";

    // Orçamento
    public static final String BUDGET_READ = "budget.read";
    public static final String BUDGET_WRITE = "budget.write";
    public static final String BUDGET_EFFECTUATE = "budget.effectuate";
    public static final String BUDGET_EXPORT = "budget.export";

    // Medição
    public static final String MEASUREMENT_READ = "measurement.read";
    public static final String MEASUREMENT_WRITE = "measurement.write";
    public static final String MEASUREMENT_APPROVE = "measurement.approve";
    public static final String MEASUREMENT_REJECT = "measurement.reject";

    // Suprimentos
    public static final String PROCUREMENT_READ = "procurement.read";
    public static final String PROCUREMENT_WRITE = "procurement.write";
    public static final String PROCUREMENT_APPROVE = "procurement.approve";

    // Financeiro
    public static final String FINANCE_READ = "finance.read";
    public static final String FINANCE_WRITE = "finance.write";
    public static final String FINANCE_PAY = "finance.pay";
    public static final String FINANCE_RECEIVE = "finance.receive";
    public static final String FINANCE_RECONCILE = "finance.reconcile";

    // Comercial
    public static final String COMMERCIAL_READ = "commercial.read";
    public static final String COMMERCIAL_WRITE = "commercial.write";
    public static final String COMMERCIAL_CANCEL = "commercial.cancel";

    // Mão de Obra
    public static final String LABOR_READ = "labor.read";
    public static final String LABOR_WRITE = "labor.write";
    public static final String LABOR_CLOSE_PERIOD = "labor.close_period";

    // Cadastros
    public static final String REGISTRY_READ = "registry.read";
    public static final String REGISTRY_WRITE = "registry.write";

    // Relatórios
    public static final String REPORT_READ = "report.read";
    public static final String REPORT_EXPORT = "report.export";

    // Configurações
    public static final String SETTINGS_READ = "settings.read";
    public static final String SETTINGS_WRITE = "settings.write";
    public static final String SETTINGS_MANAGE_USERS = "settings.manage_users";

    // Admin
    public static final String ADMIN_FULL = "admin.full";
    public static final String SINAPI_IMPORT = "sinapi.import";
}
