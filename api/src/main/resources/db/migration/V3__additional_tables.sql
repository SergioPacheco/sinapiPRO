-- V3: Tabelas adicionais — módulos que evoluíram após V1
-- Estratégia: ADD COLUMN ... DEFAULT NULL (safe, no lock)
-- Todas as tabelas usam UUID PK e timestamps de auditoria

-- ============================================================
-- BUDGET (extensões)
-- ============================================================

CREATE TABLE IF NOT EXISTS bdi_config (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    item_type varchar(20) NOT NULL DEFAULT 'ALL',
    administration numeric(6,4) NOT NULL DEFAULT 0,
    profit numeric(6,4) NOT NULL DEFAULT 0,
    taxes numeric(6,4) NOT NULL DEFAULT 0,
    social_charges numeric(6,4) NOT NULL DEFAULT 0,
    financial_expenses numeric(6,4) NOT NULL DEFAULT 0,
    risks numeric(6,4) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS budget_item_memo (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_item_id uuid NOT NULL REFERENCES budget_item(id),
    lines jsonb NOT NULL DEFAULT '[]',
    result numeric(14,4),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS budget_item_tag (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_item_id uuid NOT NULL REFERENCES budget_item(id),
    tag varchar(50) NOT NULL,
    color varchar(7),
    UNIQUE(budget_item_id, tag)
);

CREATE TABLE IF NOT EXISTS budget_proposal (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    version integer NOT NULL DEFAULT 1,
    status varchar(20) NOT NULL DEFAULT 'DRAFT',
    total_amount numeric(18,2),
    discount_percent numeric(5,2) DEFAULT 0,
    validity_days integer DEFAULT 30,
    notes varchar(2000),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS social_charges_config (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    group_name varchar(60) NOT NULL,
    inss numeric(6,4) NOT NULL DEFAULT 0,
    fgts numeric(6,4) NOT NULL DEFAULT 0,
    thirteenth numeric(6,4) NOT NULL DEFAULT 0,
    vacation numeric(6,4) NOT NULL DEFAULT 0,
    dismissal numeric(6,4) NOT NULL DEFAULT 0,
    other numeric(6,4) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- MEASUREMENT (extensões)
-- ============================================================

CREATE TABLE IF NOT EXISTS measurement_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    measurement_id uuid NOT NULL REFERENCES measurement(id),
    budget_item_id uuid REFERENCES budget_item(id),
    description varchar(300) NOT NULL,
    unit varchar(10),
    contracted_qty numeric(14,4) NOT NULL DEFAULT 0,
    previous_qty numeric(14,4) NOT NULL DEFAULT 0,
    current_qty numeric(14,4) NOT NULL DEFAULT 0,
    unit_price numeric(14,4) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS measurement_item_memo (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    measurement_item_id uuid NOT NULL REFERENCES measurement_item(id),
    lines jsonb NOT NULL DEFAULT '[]',
    result numeric(14,4),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS measurement_approver (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    measurement_id uuid NOT NULL REFERENCES measurement(id),
    approver_name varchar(200) NOT NULL,
    role varchar(80),
    approved_at timestamptz,
    signature_url varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS measurement_history (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    measurement_id uuid NOT NULL REFERENCES measurement(id),
    from_status varchar(20) NOT NULL,
    to_status varchar(20) NOT NULL,
    changed_by varchar(200),
    reason varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- PROCUREMENT (extensões)
-- ============================================================

CREATE TABLE IF NOT EXISTS purchase_request (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES project(id),
    code varchar(30) NOT NULL,
    description varchar(500),
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    requester_name varchar(200),
    priority varchar(10) DEFAULT 'NORMAL',
    needed_by date,
    approved_by varchar(200),
    approved_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS purchase_order_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id uuid NOT NULL REFERENCES purchase_order(id),
    description varchar(300) NOT NULL,
    unit varchar(10),
    quantity numeric(14,4) NOT NULL,
    unit_price numeric(14,4) NOT NULL,
    total numeric(18,2),
    budget_item_id uuid,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS quotation (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_request_id uuid REFERENCES purchase_request(id),
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    deadline date,
    notes varchar(1000),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS quotation_response (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_id uuid NOT NULL REFERENCES quotation(id),
    supplier_id uuid REFERENCES supplier(id),
    total_amount numeric(18,2),
    delivery_days integer,
    payment_terms varchar(200),
    selected boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS quotation_email (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_id uuid NOT NULL REFERENCES quotation(id),
    supplier_id uuid REFERENCES supplier(id),
    email varchar(200) NOT NULL,
    sent_at timestamptz,
    token varchar(100),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS supplier_portal_token (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    token varchar(200) NOT NULL UNIQUE,
    quotation_id uuid REFERENCES quotation(id),
    expires_at timestamptz NOT NULL,
    used boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS procurement_schedule (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES project(id),
    budget_item_id uuid REFERENCES budget_item(id),
    description varchar(300),
    needed_date date NOT NULL,
    lead_time_days integer DEFAULT 7,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS purchase_budget_limit (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES project(id),
    category varchar(60),
    budget_amount numeric(18,2) NOT NULL,
    committed_amount numeric(18,2) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS purchase_order_cost_distribution (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id uuid NOT NULL REFERENCES purchase_order(id),
    cost_code varchar(30),
    percentage numeric(5,2) NOT NULL,
    amount numeric(18,2),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS receiving (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id uuid NOT NULL REFERENCES purchase_order(id),
    received_date date NOT NULL,
    received_by varchar(200),
    notes varchar(500),
    invoice_number varchar(50),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- FINANCE (extensões)
-- ============================================================

CREATE TABLE IF NOT EXISTS chart_of_accounts (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(20) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    parent_code varchar(20),
    type varchar(20) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS tax_retention (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    payable_id uuid REFERENCES payable(id),
    tax_type varchar(20) NOT NULL,
    rate numeric(6,4) NOT NULL,
    amount numeric(14,2) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS supplier_advance (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    project_id uuid REFERENCES project(id),
    amount numeric(18,2) NOT NULL,
    balance numeric(18,2) NOT NULL,
    description varchar(300),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS check_issuance (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_account_id uuid REFERENCES bank_account(id),
    check_number varchar(20) NOT NULL,
    amount numeric(18,2) NOT NULL,
    payee varchar(200),
    issue_date date NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'ISSUED',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- SCHEDULE
-- ============================================================

CREATE TABLE IF NOT EXISTS schedule_activity (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    code varchar(30) NOT NULL,
    name varchar(200) NOT NULL,
    parent_id uuid,
    start_date date,
    end_date date,
    duration_days integer,
    percent_complete numeric(5,2) NOT NULL DEFAULT 0,
    is_milestone boolean NOT NULL DEFAULT false,
    is_critical boolean NOT NULL DEFAULT false,
    wbs varchar(30),
    budget_item_id uuid,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS activity_dependency (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    predecessor_id uuid NOT NULL REFERENCES schedule_activity(id),
    successor_id uuid NOT NULL REFERENCES schedule_activity(id),
    type varchar(4) NOT NULL DEFAULT 'FS',
    lag_days integer DEFAULT 0
);

CREATE TABLE IF NOT EXISTS schedule_baseline (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    name varchar(100) NOT NULL,
    snapshot_date date NOT NULL,
    data jsonb NOT NULL DEFAULT '{}',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS schedule_holiday (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES project(id),
    date date NOT NULL,
    description varchar(100),
    recurring boolean NOT NULL DEFAULT false
);

-- ============================================================
-- JOB COSTING
-- ============================================================

CREATE TABLE IF NOT EXISTS cost_code (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    code varchar(30) NOT NULL,
    description varchar(200) NOT NULL,
    budget_amount numeric(18,2) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS cost_transaction (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    cost_code_id uuid NOT NULL REFERENCES cost_code(id),
    type varchar(20) NOT NULL,
    amount numeric(18,2) NOT NULL,
    description varchar(300),
    reference_date date NOT NULL,
    source_type varchar(30),
    source_id uuid,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- EQUIPMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS equipment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES project(id),
    code varchar(30) NOT NULL,
    name varchar(200) NOT NULL,
    type varchar(60),
    brand varchar(100),
    model varchar(100),
    serial_number varchar(100),
    plate varchar(20),
    year integer,
    status varchar(20) NOT NULL DEFAULT 'AVAILABLE',
    hourly_cost numeric(14,2),
    ownership varchar(20) NOT NULL DEFAULT 'OWNED',
    supplier_id uuid,
    rental_start date,
    rental_end date,
    rental_monthly_cost numeric(14,2),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS equipment_usage (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id uuid NOT NULL REFERENCES equipment(id),
    usage_date date NOT NULL,
    hours_used numeric(6,2) NOT NULL,
    operator_name varchar(200),
    activity varchar(300),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS equipment_fueling (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id uuid NOT NULL REFERENCES equipment(id),
    fueling_date date NOT NULL,
    fuel_type varchar(30) NOT NULL,
    liters numeric(10,2) NOT NULL,
    cost numeric(14,2) NOT NULL,
    odometer numeric(12,1),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- CONTRACT
-- ============================================================

CREATE TABLE IF NOT EXISTS contract (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES project(id),
    code varchar(30) NOT NULL,
    title varchar(200) NOT NULL,
    supplier_id uuid REFERENCES supplier(id),
    type varchar(30) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'ACTIVE',
    start_date date,
    end_date date,
    total_amount numeric(18,2) NOT NULL,
    retention_percent numeric(5,2) DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS change_order (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id uuid NOT NULL REFERENCES contract(id),
    code varchar(30) NOT NULL,
    description varchar(500),
    amount numeric(18,2) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    approved_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- SUPPLIER (extensões)
-- ============================================================

CREATE TABLE IF NOT EXISTS supplier_document (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    type varchar(40) NOT NULL,
    number varchar(60),
    expiry_date date,
    file_url varchar(500),
    status varchar(20) NOT NULL DEFAULT 'VALID',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS supplier_bank_account (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    bank_code varchar(10) NOT NULL,
    bank_name varchar(100),
    agency varchar(20) NOT NULL,
    account_number varchar(30) NOT NULL,
    pix_key varchar(100),
    is_primary boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS supplier_evaluation (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    project_id uuid REFERENCES project(id),
    evaluator_name varchar(200),
    quality_score integer NOT NULL,
    delivery_score integer NOT NULL,
    price_score integer NOT NULL,
    communication_score integer NOT NULL,
    notes varchar(500),
    evaluation_date date NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- DAILY LOG (extensões)
-- ============================================================

CREATE TABLE IF NOT EXISTS daily_log_task (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id),
    description varchar(500) NOT NULL,
    location varchar(200),
    percent_complete numeric(5,2) DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS daily_log_labor (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id),
    employee_id uuid REFERENCES employee(id),
    role varchar(80),
    hours_worked numeric(4,1) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS daily_log_equipment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id),
    equipment_id uuid REFERENCES equipment(id),
    hours_used numeric(4,1) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS daily_log_material (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id),
    material_name varchar(200) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    unit varchar(10),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS daily_log_occurrence (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id),
    type varchar(30) NOT NULL,
    description varchar(1000) NOT NULL,
    severity varchar(10) DEFAULT 'LOW',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS daily_log_photo (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id),
    url varchar(500) NOT NULL,
    caption varchar(200),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- SAFETY
-- ============================================================

CREATE TABLE IF NOT EXISTS safety_inspection (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    inspector_name varchar(200) NOT NULL,
    inspection_date date NOT NULL,
    type varchar(40) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    score numeric(5,2),
    notes varchar(2000),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS safety_incident (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    incident_date date NOT NULL,
    type varchar(40) NOT NULL,
    severity varchar(10) NOT NULL,
    description varchar(2000) NOT NULL,
    injured_person varchar(200),
    corrective_action varchar(1000),
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- DOCUMENT
-- ============================================================

CREATE TABLE IF NOT EXISTS document (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES project(id),
    title varchar(200) NOT NULL,
    category varchar(60),
    file_url varchar(500),
    file_size bigint,
    mime_type varchar(100),
    status varchar(20) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS document_version (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id uuid NOT NULL REFERENCES document(id),
    version integer NOT NULL,
    file_url varchar(500) NOT NULL,
    uploaded_by varchar(200),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- RFI / PUNCH LIST / SUBMITTAL
-- ============================================================

CREATE TABLE IF NOT EXISTS rfi (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    code varchar(30) NOT NULL,
    subject varchar(300) NOT NULL,
    question varchar(2000) NOT NULL,
    answer varchar(2000),
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    priority varchar(10) DEFAULT 'NORMAL',
    assigned_to varchar(200),
    due_date date,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS punch_list_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    location varchar(200) NOT NULL,
    description varchar(1000) NOT NULL,
    category varchar(60),
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    priority varchar(10) DEFAULT 'NORMAL',
    assigned_to varchar(200),
    due_date date,
    photo_url varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS submittal (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    code varchar(30) NOT NULL,
    title varchar(200) NOT NULL,
    type varchar(40) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    submitted_to varchar(200),
    due_date date,
    notes varchar(1000),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- SHARED
-- ============================================================

CREATE TABLE IF NOT EXISTS trash_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type varchar(60) NOT NULL,
    entity_id uuid NOT NULL,
    entity_data jsonb NOT NULL,
    deleted_by varchar(200),
    deleted_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS report_template (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(40) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    type varchar(20) NOT NULL,
    template_content text,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- WEATHER
-- ============================================================

CREATE TABLE IF NOT EXISTS weather_delay (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    delay_date date NOT NULL,
    hours_lost numeric(4,1) NOT NULL,
    weather_type varchar(30) NOT NULL,
    description varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- TEAM
-- ============================================================

CREATE TABLE IF NOT EXISTS team (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL REFERENCES project(id),
    name varchar(100) NOT NULL,
    leader_id uuid REFERENCES employee(id),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS team_member (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id uuid NOT NULL REFERENCES team(id),
    employee_id uuid NOT NULL REFERENCES employee(id),
    role varchar(80),
    joined_at date NOT NULL DEFAULT CURRENT_DATE
);

-- ============================================================
-- INDEXES (performance)
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_budget_item_stage ON budget_item(stage_id);
CREATE INDEX IF NOT EXISTS idx_measurement_item_measurement ON measurement_item(measurement_id);
CREATE INDEX IF NOT EXISTS idx_cost_transaction_code ON cost_transaction(cost_code_id);
CREATE INDEX IF NOT EXISTS idx_schedule_activity_project ON schedule_activity(project_id);
CREATE INDEX IF NOT EXISTS idx_daily_log_project_date ON daily_log(project_id, log_date);
