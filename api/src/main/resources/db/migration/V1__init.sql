-- V1: SinapiPRO — Schema completo + Dados de demonstração
-- Gerado: 2026-05-23 | Sistema novo, primeira instalação

-- ============================================================
-- EXTENSÕES
-- ============================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- TABELAS GLOBAIS (sem tenant)
-- ============================================================

CREATE TABLE tenant (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    slug varchar(50) NOT NULL UNIQUE,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE app_settings (
    key varchar(60) PRIMARY KEY,
    value varchar(500) NOT NULL
);

CREATE TABLE city (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL,
    state varchar(2) NOT NULL,
    ibge_code varchar(10),
    UNIQUE(name, state)
);

CREATE TABLE monetary_index (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(20) NOT NULL UNIQUE,
    name varchar(100) NOT NULL,
    source varchar(50),
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE monetary_index_value (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    index_id uuid NOT NULL REFERENCES monetary_index(id),
    reference_month date NOT NULL,
    value numeric(12,6) NOT NULL,
    accumulated numeric(14,6),
    UNIQUE(index_id, reference_month)
);

CREATE TABLE hour_type (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(10) NOT NULL UNIQUE,
    name varchar(60) NOT NULL,
    multiplier numeric(4,2) NOT NULL DEFAULT 1.0,
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE unit_of_measure (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    symbol varchar(10) NOT NULL UNIQUE,
    description varchar(100) NOT NULL
);

-- ============================================================
-- TABELAS COM TENANT (dados por empresa)
-- ============================================================

CREATE TABLE project (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    code varchar(30) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    description varchar(1000),
    customer_name varchar(200) NOT NULL,
    customer_document varchar(20),
    address varchar(300),
    city varchar(100),
    state varchar(2),
    neighborhood varchar(100),
    address_number varchar(20),
    postal_code varchar(10),
    phone varchar(30),
    responsible_engineer varchar(200),
    art_number varchar(50),
    start_date date,
    expected_end_date date,
    actual_end_date date,
    status varchar(20) NOT NULL DEFAULT 'PLANNING',
    total_area numeric(14,2),
    total_built_area numeric(14,2),
    total_budget numeric(18,2),
    client_id uuid,
    employee_id uuid,
    project_type varchar(30),
    contract_regime varchar(30),
    permit_number varchar(50),
    permit_expiry date,
    cei_cno varchar(30),
    development_id uuid,
    branch_id uuid,
    accounting_code varchar(30),
    financial_control_enabled boolean NOT NULL DEFAULT false,
    stock_control_enabled boolean NOT NULL DEFAULT false,
    budget_control_enabled boolean NOT NULL DEFAULT false,
    cost_apportionment_enabled boolean NOT NULL DEFAULT false,
    apportionment_rate numeric(5,2) DEFAULT 0,
    purchase_limit_no_auth numeric(18,2),
    billing_to_client boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE client (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    name varchar(200) NOT NULL,
    document varchar(20),
    email varchar(200),
    phone varchar(30),
    address varchar(400),
    city varchar(100),
    state varchar(2),
    notes varchar(500),
    active boolean NOT NULL DEFAULT true,
    trade_name varchar(200),
    person_type varchar(2) NOT NULL DEFAULT 'PJ',
    cell_phone varchar(30),
    whatsapp varchar(30),
    website varchar(200),
    neighborhood varchar(100),
    postal_code varchar(10),
    billing_address varchar(400),
    gross_income numeric(18,2),
    preferred_due_day integer,
    billing_by_email boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE supplier (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    code varchar(40) NOT NULL UNIQUE,
    name varchar(140) NOT NULL,
    trade_name varchar(140),
    tax_id varchar(30) NOT NULL,
    email varchar(140),
    phone varchar(40),
    contact_name varchar(140),
    website varchar(200),
    category varchar(40) NOT NULL,
    qualification_status varchar(30) NOT NULL,
    payment_term_days integer NOT NULL DEFAULT 30,
    lead_time_days integer NOT NULL DEFAULT 7,
    address varchar(300),
    city varchar(100),
    state varchar(2),
    postal_code varchar(20),
    notes varchar(1000),
    rating integer NOT NULL DEFAULT 5,
    active boolean NOT NULL DEFAULT true,
    cell_phone varchar(30),
    commercial_phone varchar(30),
    whatsapp varchar(30),
    payment_due_day integer,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE employee (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    employee_code varchar(30) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    document varchar(20),
    role varchar(80) NOT NULL,
    specialty varchar(100) NOT NULL,
    type varchar(20) NOT NULL DEFAULT 'EMPLOYEE',
    employment_status varchar(20) NOT NULL DEFAULT 'ACTIVE',
    email varchar(200),
    phone varchar(30),
    mobile_phone varchar(30),
    address varchar(300),
    city varchar(100),
    state varchar(2),
    postal_code varchar(20),
    cost_center varchar(80),
    company_name varchar(140),
    notes varchar(1000),
    hourly_rate numeric(14,4),
    admission_date date,
    termination_date date,
    active boolean NOT NULL DEFAULT true,
    birth_date date,
    gender varchar(1),
    salary numeric(14,2),
    department varchar(100),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE budget (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    code varchar(40) NOT NULL UNIQUE,
    title varchar(140) NOT NULL,
    customer_name varchar(140) NOT NULL,
    total_amount numeric(18,2) NOT NULL DEFAULT 0,
    status varchar(30) NOT NULL DEFAULT 'DRAFT',
    active boolean NOT NULL DEFAULT false,
    start_date date NOT NULL,
    end_date date,
    metadata jsonb NOT NULL DEFAULT '{}',
    project_id uuid REFERENCES project(id),
    reference_date date,
    state varchar(2),
    rounding_method varchar(20) DEFAULT 'TRUNCATE',
    decimal_places integer DEFAULT 4,
    item_mask varchar(30),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE composition (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    sinapi_code varchar(20),
    description varchar(300) NOT NULL,
    unit varchar(20),
    group_name varchar(100),
    origin varchar(20) DEFAULT 'SINAPI',
    version integer DEFAULT 1,
    parent_id uuid,
    is_current boolean DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE material (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    sinapi_code varchar(20),
    description varchar(300) NOT NULL,
    unit varchar(20),
    origin varchar(20) DEFAULT 'SINAPI',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE budget_stage (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    name varchar(200) NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    parent_id uuid REFERENCES budget_stage(id),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE budget_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    stage_id uuid NOT NULL REFERENCES budget_stage(id) ON DELETE CASCADE,
    composition_id uuid NOT NULL REFERENCES composition(id),
    quantity numeric(14,4) NOT NULL,
    unit_cost numeric(14,4) NOT NULL,
    bdi_pct numeric(6,4) NOT NULL DEFAULT 0.25,
    custom_code varchar(30),
    price_source varchar(20) DEFAULT 'SINAPI',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE measurement (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    budget_id uuid NOT NULL REFERENCES budget(id),
    project_id uuid REFERENCES project(id),
    number integer NOT NULL,
    period_start date NOT NULL,
    period_end date NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'DRAFT',
    total_amount numeric(18,2) DEFAULT 0,
    notes varchar(1000),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE purchase_order (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    budget_id uuid NOT NULL REFERENCES budget(id),
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    project_id uuid REFERENCES project(id),
    number varchar(40) NOT NULL UNIQUE,
    description varchar(300) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    unit_price numeric(14,4) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    expected_delivery_date date,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE payable (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    budget_id uuid NOT NULL REFERENCES budget(id),
    project_id uuid REFERENCES project(id),
    supplier_id uuid REFERENCES supplier(id),
    description varchar(300) NOT NULL,
    amount numeric(18,2) NOT NULL,
    due_date date NOT NULL,
    paid_date date,
    paid_amount numeric(18,2),
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    category varchar(60),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE receivable (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    budget_id uuid NOT NULL REFERENCES budget(id),
    project_id uuid REFERENCES project(id),
    description varchar(300) NOT NULL,
    amount numeric(18,2) NOT NULL,
    due_date date NOT NULL,
    received_date date,
    received_amount numeric(18,2),
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    category varchar(60),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE bank_account (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    bank_code varchar(10) NOT NULL,
    bank_name varchar(100) NOT NULL,
    agency varchar(20) NOT NULL,
    account_number varchar(30) NOT NULL,
    account_type varchar(20) NOT NULL DEFAULT 'CHECKING',
    holder_name varchar(200),
    active boolean NOT NULL DEFAULT true,
    initial_balance numeric(18,2) DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE bank_transaction (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    bank_account_id uuid NOT NULL REFERENCES bank_account(id),
    transaction_date date NOT NULL,
    type varchar(20) NOT NULL,
    amount numeric(18,2) NOT NULL,
    description varchar(300) NOT NULL,
    reconciled boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE payable_installment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    payable_id uuid NOT NULL REFERENCES payable(id) ON DELETE CASCADE,
    installment_number integer NOT NULL,
    due_date date NOT NULL,
    amount numeric(18,2) NOT NULL,
    paid_amount numeric(18,2) DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    payment_method varchar(30),
    bank_account_id uuid REFERENCES bank_account(id),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE receivable_installment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    receivable_id uuid NOT NULL REFERENCES receivable(id) ON DELETE CASCADE,
    installment_number integer NOT NULL,
    due_date date NOT NULL,
    amount numeric(18,2) NOT NULL,
    received_amount numeric(18,2) DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    boleto_number varchar(50),
    our_number varchar(30),
    bank_account_id uuid REFERENCES bank_account(id),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE notification (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    user_id varchar(200),
    title varchar(200) NOT NULL,
    message varchar(500),
    type varchar(30),
    read boolean NOT NULL DEFAULT false,
    entity_type varchar(60),
    entity_id uuid,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE daily_log (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    project_id uuid NOT NULL REFERENCES project(id),
    log_date date NOT NULL,
    weather varchar(30),
    temperature_min numeric(4,1),
    temperature_max numeric(4,1),
    notes text,
    signed boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE service_ticket (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    unit_id uuid,
    client_name varchar(200) NOT NULL,
    category varchar(60) NOT NULL,
    description text NOT NULL,
    priority varchar(20) NOT NULL DEFAULT 'MEDIUM',
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    assigned_to varchar(140),
    resolution text,
    opened_at timestamptz NOT NULL DEFAULT now(),
    due_date date,
    resolved_at timestamptz,
    closed_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE development (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    name varchar(200) NOT NULL,
    address varchar(300),
    city varchar(100),
    state varchar(2),
    total_units integer DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE development_unit (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    development_id uuid NOT NULL REFERENCES development(id),
    identifier varchar(50) NOT NULL,
    type varchar(30),
    private_area numeric(10,2),
    price numeric(18,2),
    status varchar(20) NOT NULL DEFAULT 'AVAILABLE',
    sold_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE sale_contract (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    development_id uuid NOT NULL REFERENCES development(id),
    contract_number varchar(30) NOT NULL UNIQUE,
    contract_date date NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PROPOSAL',
    total_amount numeric(18,2) NOT NULL,
    down_payment numeric(18,2) DEFAULT 0,
    installment_count integer NOT NULL DEFAULT 1,
    amortization_type varchar(10) DEFAULT 'PRICE',
    interest_rate numeric(8,4) DEFAULT 0,
    broker_id uuid,
    commission_rate numeric(5,2),
    commission_amount numeric(18,2),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE sale_installment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    contract_id uuid NOT NULL REFERENCES sale_contract(id) ON DELETE CASCADE,
    installment_number integer NOT NULL,
    type varchar(30) NOT NULL DEFAULT 'MONTHLY',
    original_due_date date NOT NULL,
    current_due_date date NOT NULL,
    original_amount numeric(18,2) NOT NULL,
    adjusted_amount numeric(18,2) NOT NULL,
    paid_amount numeric(18,2) DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'FUTURE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- ÍNDICES
-- ============================================================
CREATE INDEX idx_project_tenant ON project(tenant_id);
CREATE INDEX idx_budget_tenant ON budget(tenant_id);
CREATE INDEX idx_measurement_tenant ON measurement(tenant_id);
CREATE INDEX idx_payable_tenant ON payable(tenant_id);
CREATE INDEX idx_receivable_tenant ON receivable(tenant_id);
CREATE INDEX idx_bank_transaction_tenant ON bank_transaction(tenant_id);
CREATE INDEX idx_notification_tenant ON notification(tenant_id);
CREATE INDEX idx_sale_contract_tenant ON sale_contract(tenant_id);
