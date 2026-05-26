-- V10: Gaps finais — autorização pagamento, fechamento período, dividendos, ajustes, agrupadores

-- Autorização de pagamento (workflow com alçadas)
CREATE TABLE IF NOT EXISTS payment_authority_level (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    approver_name varchar(200) NOT NULL,
    approver_email varchar(200) NOT NULL,
    max_amount numeric(18,2) NOT NULL,
    priority int NOT NULL,
    project_id uuid,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS payment_authorization (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    payable_id uuid,
    project_id uuid,
    description varchar(300) NOT NULL,
    amount numeric(18,2) NOT NULL,
    supplier_name varchar(200),
    due_date date,
    requested_by varchar(200) NOT NULL,
    requested_at timestamptz NOT NULL DEFAULT now(),
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    current_level int NOT NULL DEFAULT 1,
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS payment_authorization_history (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    authorization_id uuid NOT NULL,
    action varchar(20) NOT NULL,
    acted_by varchar(200) NOT NULL,
    acted_at timestamptz NOT NULL DEFAULT now(),
    level int NOT NULL,
    comments varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- Fechamento de período
CREATE TABLE IF NOT EXISTS period_closing (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    project_id uuid,
    reference_month date NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    closed_by varchar(200),
    closed_at timestamptz,
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(tenant_id, project_id, reference_month)
);

-- Distribuição de dividendos
CREATE TABLE IF NOT EXISTS dividend_distribution (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    reference_month date NOT NULL,
    total_profit numeric(18,2) NOT NULL,
    partner_name varchar(200) NOT NULL,
    partner_share_pct numeric(5,2) NOT NULL,
    amount numeric(18,2) NOT NULL,
    paid_date date,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- Ajuste de despesa (reclassificação)
CREATE TABLE IF NOT EXISTS expense_adjustment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    payable_id uuid NOT NULL,
    original_category varchar(60) NOT NULL,
    new_category varchar(60) NOT NULL,
    original_cost_center varchar(80),
    new_cost_center varchar(80),
    reason varchar(200) NOT NULL,
    adjusted_by varchar(200) NOT NULL,
    adjusted_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- Agrupadores financeiros (hierárquico)
CREATE TABLE IF NOT EXISTS financial_grouper (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    code varchar(20) NOT NULL,
    name varchar(200) NOT NULL,
    parent_id uuid REFERENCES financial_grouper(id),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_payment_auth_status ON payment_authorization(status);
CREATE INDEX IF NOT EXISTS idx_period_closing_project ON period_closing(project_id, reference_month);
