-- ============================================================
-- SinapiPRO — Schema consolidado + Seed data
-- Gerado em 2026-05-10 (pré-produção)
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- DDL: Tabelas independentes
-- ============================================================

CREATE TABLE project (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(30) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    description varchar(1000),
    customer_name varchar(200) NOT NULL,
    customer_document varchar(20),
    address varchar(300),
    city varchar(100),
    state varchar(2),
    responsible_engineer varchar(200),
    art_number varchar(50),
    start_date date,
    expected_end_date date,
    actual_end_date date,
    status varchar(20) NOT NULL DEFAULT 'PLANNING',
    total_area numeric(14,2),
    total_budget numeric(18,2),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE supplier (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(40) NOT NULL UNIQUE,
    name varchar(140) NOT NULL,
    trade_name varchar(140),
    tax_id varchar(30) NOT NULL UNIQUE,
    email varchar(140),
    phone varchar(40),
    rating integer NOT NULL,
    active boolean NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE client (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    document varchar(20),
    email varchar(200),
    phone varchar(30),
    address varchar(400),
    city varchar(100),
    state varchar(2),
    notes varchar(500),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE employee (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    document varchar(20),
    role varchar(80) NOT NULL,
    type varchar(20) NOT NULL DEFAULT 'EMPLOYEE',
    email varchar(200),
    phone varchar(30),
    hourly_rate numeric(14,4),
    admission_date date,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE unit_of_measure (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    symbol varchar(10) NOT NULL UNIQUE,
    description varchar(100) NOT NULL
);

CREATE TABLE payment_method (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL UNIQUE,
    installments integer NOT NULL DEFAULT 1,
    active boolean NOT NULL DEFAULT true
);

CREATE TABLE bank_account (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_code varchar(10) NOT NULL,
    bank_name varchar(100) NOT NULL,
    agency varchar(20) NOT NULL,
    account_number varchar(30) NOT NULL,
    account_type varchar(20) NOT NULL DEFAULT 'CHECKING',
    holder_name varchar(200),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE equipment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(30) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    type varchar(60) NOT NULL,
    brand varchar(100),
    model varchar(100),
    year integer,
    license_plate varchar(20),
    hourly_cost numeric(14,4) NOT NULL DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'AVAILABLE',
    current_hours numeric(10,2) NOT NULL DEFAULT 0,
    current_km numeric(10,2) NOT NULL DEFAULT 0,
    next_maintenance_hours numeric(10,2),
    next_maintenance_date date,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE material (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    sinapi_code varchar(20) NOT NULL UNIQUE,
    description varchar(500) NOT NULL,
    unit varchar(20) NOT NULL,
    origin varchar(30) NOT NULL,
    search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('portuguese', coalesce(sinapi_code, '')), 'A') ||
        setweight(to_tsvector('portuguese', coalesce(description, '')), 'B')
    ) STORED,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE composition (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    sinapi_code varchar(20) NOT NULL UNIQUE,
    description varchar(500) NOT NULL,
    unit varchar(20) NOT NULL,
    group_name varchar(140),
    origin varchar(20) NOT NULL DEFAULT 'SINAPI',
    search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('portuguese', coalesce(sinapi_code, '')), 'A') ||
        setweight(to_tsvector('portuguese', coalesce(description, '')), 'B')
    ) STORED,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE safety_checklist_template (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    category varchar(60) NOT NULL,
    items jsonb NOT NULL DEFAULT '[]',
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE development (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    address varchar(400),
    city varchar(100),
    state varchar(2),
    total_units integer NOT NULL DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'PLANNING',
    launch_date date,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================
-- DDL: Tabelas com FK para tabelas base
-- ============================================================

CREATE TABLE budget (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid REFERENCES project(id),
    code varchar(40) NOT NULL UNIQUE,
    title varchar(140) NOT NULL,
    customer_name varchar(140) NOT NULL,
    total_amount numeric(18,2) NOT NULL,
    status varchar(30) NOT NULL,
    active boolean NOT NULL DEFAULT false,
    start_date date NOT NULL,
    end_date date,
    metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE material_price (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    material_id uuid NOT NULL REFERENCES material(id) ON DELETE CASCADE,
    state varchar(2) NOT NULL,
    reference_month date NOT NULL,
    price numeric(14,4) NOT NULL,
    desonerated boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT material_price_unique UNIQUE (material_id, state, reference_month, desonerated)
);

CREATE TABLE composition_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    composition_id uuid NOT NULL REFERENCES composition(id) ON DELETE CASCADE,
    material_id uuid NOT NULL REFERENCES material(id),
    coefficient numeric(14,6) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE equipment_maintenance (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id uuid NOT NULL REFERENCES equipment(id),
    maintenance_date date NOT NULL,
    type varchar(40) NOT NULL,
    description varchar(500) NOT NULL,
    cost numeric(14,2) NOT NULL DEFAULT 0,
    hours_at_maintenance numeric(10,2),
    km_at_maintenance numeric(10,2),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE development_unit (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    development_id uuid NOT NULL REFERENCES development(id) ON DELETE CASCADE,
    code varchar(30) NOT NULL,
    type varchar(40) NOT NULL,
    area numeric(10,2),
    price numeric(18,2) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'AVAILABLE',
    floor integer,
    bedrooms integer,
    notes varchar(300),
    UNIQUE(development_id, code)
);

-- ============================================================
-- DDL: Tabelas dependentes de budget
-- ============================================================

CREATE TABLE invoice (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    number varchar(40) NOT NULL UNIQUE,
    budget_id uuid NOT NULL REFERENCES budget(id),
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    amount numeric(18,2) NOT NULL,
    issue_date date NOT NULL,
    due_date date NOT NULL,
    status varchar(30) NOT NULL,
    notes text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE budget_stage (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    parent_id uuid REFERENCES budget_stage(id) ON DELETE CASCADE,
    name varchar(200) NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE budget_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    stage_id uuid NOT NULL REFERENCES budget_stage(id) ON DELETE CASCADE,
    composition_id uuid NOT NULL REFERENCES composition(id),
    quantity numeric(14,4) NOT NULL,
    unit_cost numeric(14,4) NOT NULL,
    bdi_pct numeric(6,4) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE bdi_config (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL UNIQUE REFERENCES budget(id) ON DELETE CASCADE,
    administration numeric(6,4) NOT NULL DEFAULT 0,
    profit numeric(6,4) NOT NULL DEFAULT 0,
    taxes numeric(6,4) NOT NULL DEFAULT 0,
    social_charges numeric(6,4) NOT NULL DEFAULT 0,
    financial_expenses numeric(6,4) NOT NULL DEFAULT 0,
    risks numeric(6,4) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE cost_code (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    project_id uuid,
    parent_id uuid REFERENCES cost_code(id) ON DELETE CASCADE,
    code varchar(30) NOT NULL,
    name varchar(200) NOT NULL,
    budgeted_amount numeric(18,2) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (budget_id, code)
);

CREATE TABLE cost_transaction (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    cost_code_id uuid NOT NULL REFERENCES cost_code(id) ON DELETE CASCADE,
    type varchar(20) NOT NULL,
    amount numeric(18,2) NOT NULL,
    description varchar(300),
    reference_id uuid,
    transaction_date date NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE schedule_activity (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    project_id uuid,
    name varchar(200) NOT NULL,
    planned_start date NOT NULL,
    planned_end date NOT NULL,
    actual_start date,
    actual_end date,
    weight numeric(6,4) NOT NULL DEFAULT 0,
    progress_pct numeric(5,2) NOT NULL DEFAULT 0,
    sort_order integer NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE activity_dependency (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    predecessor_id uuid NOT NULL REFERENCES schedule_activity(id) ON DELETE CASCADE,
    successor_id uuid NOT NULL REFERENCES schedule_activity(id) ON DELETE CASCADE,
    type varchar(10) NOT NULL DEFAULT 'FS',
    UNIQUE (predecessor_id, successor_id)
);

CREATE TABLE schedule_baseline (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id uuid NOT NULL,
    name varchar(100) NOT NULL,
    snapshot jsonb NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE measurement (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    project_id uuid,
    number integer NOT NULL,
    period_start date NOT NULL,
    period_end date NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'DRAFT',
    retention_pct numeric(5,4) NOT NULL DEFAULT 0,
    notes text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (budget_id, number)
);

CREATE TABLE measurement_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    measurement_id uuid NOT NULL REFERENCES measurement(id) ON DELETE CASCADE,
    cost_code_id uuid REFERENCES cost_code(id),
    budget_item_id uuid REFERENCES budget_item(id),
    description varchar(300) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    unit_price numeric(14,4) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE contract (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    project_id uuid,
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    number varchar(40) NOT NULL,
    description varchar(300) NOT NULL,
    original_value numeric(18,2) NOT NULL,
    retention_pct numeric(5,4) NOT NULL DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'DRAFT',
    start_date date NOT NULL,
    end_date date,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE contract_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id uuid NOT NULL REFERENCES contract(id) ON DELETE CASCADE,
    description varchar(300) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    unit_price numeric(14,4) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE change_order (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id uuid NOT NULL REFERENCES contract(id) ON DELETE CASCADE,
    number integer NOT NULL,
    description varchar(500) NOT NULL,
    amount numeric(18,2) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    justification text,
    approved_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (contract_id, number)
);

CREATE TABLE purchase_request (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    cost_code_id uuid REFERENCES cost_code(id),
    description varchar(300) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    unit varchar(20) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    requested_by varchar(140),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE quotation (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_request_id uuid NOT NULL REFERENCES purchase_request(id) ON DELETE CASCADE,
    project_id uuid,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    deadline date,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE quotation_response (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_id uuid NOT NULL REFERENCES quotation(id) ON DELETE CASCADE,
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    unit_price numeric(14,4) NOT NULL,
    delivery_days integer,
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE purchase_order (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    project_id uuid,
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    quotation_response_id uuid REFERENCES quotation_response(id),
    cost_code_id uuid REFERENCES cost_code(id),
    number varchar(40) NOT NULL UNIQUE,
    description varchar(300) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    unit_price numeric(14,4) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    expected_delivery_date date,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE receiving (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id uuid NOT NULL REFERENCES purchase_order(id),
    quantity_received numeric(14,4) NOT NULL,
    received_at date NOT NULL,
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE purchase_order_cost_distribution (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id uuid NOT NULL REFERENCES purchase_order(id) ON DELETE CASCADE,
    cost_code_id uuid NOT NULL REFERENCES cost_code(id),
    percentage numeric(5,4) NOT NULL,
    amount numeric(18,2) NOT NULL
);

CREATE TABLE daily_log (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    project_id uuid,
    log_date date NOT NULL,
    weather_morning varchar(30),
    weather_afternoon varchar(30),
    observations text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE (budget_id, log_date)
);

CREATE TABLE daily_log_labor (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id) ON DELETE CASCADE,
    worker_name varchar(140) NOT NULL,
    role varchar(80) NOT NULL,
    hours numeric(4,2) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE daily_log_equipment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id) ON DELETE CASCADE,
    equipment_name varchar(140) NOT NULL,
    hours_used numeric(4,2) NOT NULL,
    hours_idle numeric(4,2) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE daily_log_occurrence (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id) ON DELETE CASCADE,
    type varchar(40) NOT NULL,
    description text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE daily_log_photo (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id) ON DELETE CASCADE,
    file_path varchar(500) NOT NULL,
    caption varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE equipment_usage (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id uuid NOT NULL REFERENCES equipment(id),
    budget_id uuid NOT NULL REFERENCES budget(id),
    usage_date date NOT NULL,
    hours_used numeric(6,2) NOT NULL,
    km_used numeric(8,2) NOT NULL DEFAULT 0,
    operator varchar(140),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE equipment_fueling (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id uuid NOT NULL REFERENCES equipment(id),
    budget_id uuid NOT NULL REFERENCES budget(id),
    fueling_date date NOT NULL,
    fuel_type varchar(30) NOT NULL,
    liters numeric(8,2) NOT NULL,
    cost_per_liter numeric(8,4) NOT NULL,
    total_cost numeric(14,2) NOT NULL,
    odometer numeric(10,2),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE document (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid REFERENCES budget(id),
    entity_type varchar(40),
    entity_id uuid,
    title varchar(300) NOT NULL,
    file_name varchar(260) NOT NULL,
    content_type varchar(100) NOT NULL,
    file_size bigint NOT NULL,
    storage_path varchar(500) NOT NULL,
    version integer NOT NULL DEFAULT 1,
    uploaded_by varchar(140),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE document_version (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id uuid NOT NULL REFERENCES document(id) ON DELETE CASCADE,
    version_number integer NOT NULL,
    file_path varchar(500) NOT NULL,
    uploaded_by varchar(140),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(document_id, version_number)
);

CREATE TABLE rfi (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    number integer NOT NULL,
    subject varchar(300) NOT NULL,
    question text NOT NULL,
    answer text,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    priority varchar(20) NOT NULL DEFAULT 'NORMAL',
    assigned_to varchar(140),
    created_by varchar(140),
    due_date date,
    answered_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(budget_id, number)
);

CREATE TABLE punch_list_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    location varchar(200) NOT NULL,
    description text NOT NULL,
    category varchar(60),
    priority varchar(20) NOT NULL DEFAULT 'NORMAL',
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    assigned_to varchar(140),
    due_date date,
    completed_at timestamptz,
    created_by varchar(140),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE safety_inspection (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    project_id uuid,
    template_id uuid NOT NULL REFERENCES safety_checklist_template(id),
    inspector varchar(140) NOT NULL,
    inspection_date date NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PASS',
    results jsonb NOT NULL DEFAULT '[]',
    notes text,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE safety_incident (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    project_id uuid,
    incident_date date NOT NULL,
    severity varchar(20) NOT NULL,
    description text NOT NULL,
    location varchar(200),
    injured_party varchar(140),
    corrective_action text,
    reported_by varchar(140),
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE submittal (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    number integer NOT NULL,
    title varchar(300) NOT NULL,
    spec_section varchar(60),
    type varchar(40) NOT NULL,
    submitted_by varchar(140),
    assigned_to varchar(140),
    status varchar(20) NOT NULL DEFAULT 'DRAFT',
    due_date date,
    submitted_at timestamptz,
    reviewed_at timestamptz,
    reviewer_notes text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(budget_id, number)
);

CREATE TABLE weather_delay (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    delay_date date NOT NULL,
    weather_condition varchar(60) NOT NULL,
    hours_lost numeric(4,2) NOT NULL,
    full_day_lost boolean NOT NULL DEFAULT false,
    impact_description varchar(500),
    reported_by varchar(140),
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(budget_id, delay_date)
);

CREATE TABLE timesheet_entry (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    cost_code_id uuid REFERENCES cost_code(id),
    worker_name varchar(140) NOT NULL,
    role varchar(80) NOT NULL,
    work_date date NOT NULL,
    regular_hours numeric(4,2) NOT NULL DEFAULT 0,
    overtime_hours numeric(4,2) NOT NULL DEFAULT 0,
    hourly_rate numeric(10,2) NOT NULL DEFAULT 0,
    units_produced numeric(10,2),
    unit_type varchar(30),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE notification (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid,
    type varchar(40) NOT NULL,
    severity varchar(20) NOT NULL DEFAULT 'INFO',
    title varchar(300) NOT NULL,
    message text NOT NULL,
    entity_type varchar(40),
    entity_id uuid,
    recipient varchar(140),
    read boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE payable (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    supplier_id uuid REFERENCES supplier(id),
    purchase_order_id uuid REFERENCES purchase_order(id),
    measurement_id uuid REFERENCES measurement(id),
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
    budget_id uuid NOT NULL REFERENCES budget(id),
    measurement_id uuid REFERENCES measurement(id),
    invoice_id uuid REFERENCES invoice(id),
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

CREATE TABLE stock_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    description varchar(300) NOT NULL,
    unit varchar(20) NOT NULL,
    current_quantity numeric(14,4) NOT NULL DEFAULT 0,
    min_quantity numeric(14,4) NOT NULL DEFAULT 0,
    location varchar(100),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE stock_movement (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_item_id uuid NOT NULL REFERENCES stock_item(id),
    type varchar(20) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    reference_id uuid,
    reference_type varchar(40),
    notes varchar(300),
    moved_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE stock_requisition (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    requested_by varchar(140) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE stock_requisition_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    requisition_id uuid NOT NULL REFERENCES stock_requisition(id) ON DELETE CASCADE,
    stock_item_id uuid NOT NULL REFERENCES stock_item(id),
    quantity numeric(14,4) NOT NULL,
    delivered_quantity numeric(14,4) NOT NULL DEFAULT 0
);

CREATE TABLE sales_proposal (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id uuid NOT NULL REFERENCES development_unit(id),
    client_id uuid REFERENCES client(id),
    client_name varchar(200) NOT NULL,
    proposal_date date NOT NULL,
    proposed_price numeric(18,2) NOT NULL,
    down_payment numeric(18,2),
    installments integer DEFAULT 1,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE broker_commission (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    proposal_id uuid NOT NULL REFERENCES sales_proposal(id),
    broker_name varchar(200) NOT NULL,
    percentage numeric(5,4) NOT NULL,
    amount numeric(18,2) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    paid_date date,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE service_ticket (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id uuid REFERENCES development_unit(id),
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
    closed_at timestamptz
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_budget_status ON budget(status);
CREATE INDEX idx_budget_customer_name ON budget(customer_name);
CREATE INDEX idx_budget_project ON budget(project_id);
CREATE INDEX idx_supplier_active ON supplier(active);
CREATE INDEX idx_invoice_status ON invoice(status);
CREATE INDEX idx_invoice_due_date ON invoice(due_date);
CREATE INDEX idx_invoice_budget ON invoice(budget_id);
CREATE INDEX idx_invoice_supplier ON invoice(supplier_id);
CREATE INDEX idx_material_search ON material USING gin(search_vector);
CREATE INDEX idx_material_origin ON material(origin);
CREATE INDEX idx_material_price_lookup ON material_price(material_id, state, reference_month, desonerated);
CREATE INDEX idx_composition_search ON composition USING gin(search_vector);
CREATE INDEX idx_composition_group ON composition(group_name);
CREATE INDEX idx_composition_origin ON composition(origin);
CREATE INDEX idx_composition_item_composition ON composition_item(composition_id);
CREATE INDEX idx_composition_item_material ON composition_item(material_id);
CREATE INDEX idx_budget_stage_budget ON budget_stage(budget_id);
CREATE INDEX idx_budget_stage_parent ON budget_stage(parent_id);
CREATE INDEX idx_budget_item_stage ON budget_item(stage_id);
CREATE INDEX idx_budget_item_composition ON budget_item(composition_id);
CREATE INDEX idx_cost_code_budget ON cost_code(budget_id);
CREATE INDEX idx_cost_code_parent ON cost_code(parent_id);
CREATE INDEX idx_cost_code_project ON cost_code(project_id);
CREATE INDEX idx_cost_transaction_code ON cost_transaction(cost_code_id);
CREATE INDEX idx_cost_transaction_type ON cost_transaction(type);
CREATE INDEX idx_cost_transaction_date ON cost_transaction(transaction_date);
CREATE INDEX idx_schedule_activity_budget ON schedule_activity(budget_id);
CREATE INDEX idx_schedule_activity_project ON schedule_activity(project_id);
CREATE INDEX idx_schedule_baseline_project ON schedule_baseline(project_id);
CREATE INDEX idx_measurement_budget ON measurement(budget_id);
CREATE INDEX idx_measurement_project ON measurement(project_id);
CREATE INDEX idx_measurement_item_measurement ON measurement_item(measurement_id);
CREATE INDEX idx_measurement_item_budget_item ON measurement_item(budget_item_id);
CREATE INDEX idx_contract_budget ON contract(budget_id);
CREATE INDEX idx_contract_supplier ON contract(supplier_id);
CREATE INDEX idx_contract_project ON contract(project_id);
CREATE INDEX idx_contract_item_contract ON contract_item(contract_id);
CREATE INDEX idx_change_order_contract ON change_order(contract_id);
CREATE INDEX idx_purchase_request_budget ON purchase_request(budget_id);
CREATE INDEX idx_quotation_response_quotation ON quotation_response(quotation_id);
CREATE INDEX idx_purchase_order_budget ON purchase_order(budget_id);
CREATE INDEX idx_purchase_order_delivery ON purchase_order(expected_delivery_date) WHERE status IN ('PENDING', 'APPROVED', 'PARTIAL');
CREATE INDEX idx_receiving_po ON receiving(purchase_order_id);
CREATE INDEX idx_po_cost_dist ON purchase_order_cost_distribution(purchase_order_id);
CREATE INDEX idx_daily_log_budget ON daily_log(budget_id);
CREATE INDEX idx_daily_log_project ON daily_log(project_id);
CREATE INDEX idx_daily_log_photo_log ON daily_log_photo(daily_log_id);
CREATE INDEX idx_equipment_usage_equipment ON equipment_usage(equipment_id);
CREATE INDEX idx_equipment_usage_budget ON equipment_usage(budget_id);
CREATE INDEX idx_equipment_maintenance_equipment ON equipment_maintenance(equipment_id);
CREATE INDEX idx_fueling_equipment ON equipment_fueling(equipment_id);
CREATE INDEX idx_document_budget ON document(budget_id);
CREATE INDEX idx_document_entity ON document(entity_type, entity_id);
CREATE INDEX idx_rfi_budget ON rfi(budget_id);
CREATE INDEX idx_punch_list_budget ON punch_list_item(budget_id);
CREATE INDEX idx_safety_inspection_budget ON safety_inspection(budget_id);
CREATE INDEX idx_safety_incident_budget ON safety_incident(budget_id);
CREATE INDEX idx_submittal_budget ON submittal(budget_id);
CREATE INDEX idx_weather_delay_budget ON weather_delay(budget_id);
CREATE INDEX idx_timesheet_budget ON timesheet_entry(budget_id);
CREATE INDEX idx_timesheet_date ON timesheet_entry(work_date);
CREATE INDEX idx_notification_recipient ON notification(recipient, read);
CREATE INDEX idx_notification_budget ON notification(budget_id);
CREATE INDEX idx_payable_budget ON payable(budget_id);
CREATE INDEX idx_payable_due_date ON payable(due_date) WHERE status = 'PENDING';
CREATE INDEX idx_receivable_budget ON receivable(budget_id);
CREATE INDEX idx_receivable_due_date ON receivable(due_date) WHERE status = 'PENDING';
CREATE INDEX idx_stock_item_budget ON stock_item(budget_id);
CREATE INDEX idx_stock_movement_item ON stock_movement(stock_item_id);
CREATE INDEX idx_dev_unit_development ON development_unit(development_id);
CREATE INDEX idx_dev_unit_status ON development_unit(status);
CREATE INDEX idx_proposal_unit ON sales_proposal(unit_id);
CREATE INDEX idx_commission_proposal ON broker_commission(proposal_id);
CREATE INDEX idx_ticket_status ON service_ticket(status);
CREATE INDEX idx_ticket_unit ON service_ticket(unit_id);

-- ============================================================
-- VIEW
-- ============================================================

CREATE VIEW showcase_portfolio_summary AS
SELECT
    (SELECT count(*) FROM budget) AS total_budgets,
    (SELECT coalesce(sum(total_amount), 0) FROM budget) AS total_budget_amount,
    (SELECT count(*) FROM supplier WHERE active = true) AS active_suppliers,
    (SELECT count(*) FROM invoice WHERE status IN ('PENDING', 'OVERDUE')) AS open_invoices,
    (SELECT coalesce(sum(amount), 0) FROM invoice WHERE status = 'OVERDUE') AS overdue_invoice_amount;

-- ============================================================
-- SEED DATA
-- ============================================================

-- === Unidades de medida ===
INSERT INTO unit_of_measure (id, symbol, description) VALUES
    (gen_random_uuid(), 'm', 'Metro'),
    (gen_random_uuid(), 'm2', 'Metro quadrado'),
    (gen_random_uuid(), 'm3', 'Metro cúbico'),
    (gen_random_uuid(), 'kg', 'Quilograma'),
    (gen_random_uuid(), 'un', 'Unidade'),
    (gen_random_uuid(), 'vb', 'Verba'),
    (gen_random_uuid(), 'h', 'Hora'),
    (gen_random_uuid(), 'l', 'Litro'),
    (gen_random_uuid(), 't', 'Tonelada'),
    (gen_random_uuid(), 'mês', 'Mês');

-- === Projetos ===
INSERT INTO project (id, code, name, description, customer_name, customer_document, address, city, state,
    responsible_engineer, art_number, start_date, expected_end_date, status, total_area, total_budget) VALUES
    ('a0000001-0000-0000-0000-000000000001', 'OBR-2026-001', 'Residencial Parque das Flores',
     'Condomínio residencial com 4 blocos, 64 unidades, área de lazer completa',
     'Construtora Horizonte Ltda', '12.345.678/0001-90',
     'Rua das Acácias, 500 - Lagoa Nova', 'Natal', 'RN',
     'Eng. Carlos Alberto Silva', 'ART-2026-001234',
     '2026-01-15', '2027-06-30', 'IN_PROGRESS', 8500.00, 12500000.00),
    ('a0000001-0000-0000-0000-000000000002', 'OBR-2026-002', 'Reforma Hospital Regional',
     'Reforma e ampliação do bloco cirúrgico',
     'Governo do Estado RN', '08.241.739/0001-05',
     'Av. Senador Salgado Filho, 1800', 'Natal', 'RN',
     'Eng. Maria Fernanda Costa', 'ART-2026-005678',
     '2026-03-01', '2026-12-20', 'IN_PROGRESS', 2200.00, 4800000.00),
    ('a0000001-0000-0000-0000-000000000003', 'OBR-2026-003', 'Galpão Industrial TechPark',
     'Galpão logístico com escritórios e docas',
     'TechPark Logística S.A.', '45.678.901/0001-23',
     'Distrito Industrial, Lote 45', 'Parnamirim', 'RN',
     'Eng. Roberto Mendes', null,
     '2026-05-01', '2026-11-30', 'PLANNING', 5000.00, 3200000.00);

-- === Budgets ===
INSERT INTO budget (id, project_id, code, title, customer_name, total_amount, status, active, start_date, end_date, metadata) VALUES
    ('11111111-1111-1111-1111-111111111111', 'a0000001-0000-0000-0000-000000000001', 'BUD-2026-001', 'Condominio Atlass', 'Grupo Atlass', 850000.00, 'APPROVED', true, '2026-01-10', '2026-12-18', '{"segment":"residential","city":"Natal"}'),
    ('22222222-2222-2222-2222-222222222222', 'a0000001-0000-0000-0000-000000000002', 'BUD-2026-002', 'Hospital Horizonte', 'Rede Horizonte', 2450000.00, 'IN_EXECUTION', true, '2026-02-03', '2027-06-28', '{"segment":"healthcare","city":"Recife"}');

-- === Suppliers ===
INSERT INTO supplier (id, code, name, trade_name, tax_id, email, phone, rating, active) VALUES
    ('33333333-3333-3333-3333-333333333333', 'SUP-2026-001', 'Aco Forte Industrial', 'Aco Forte', '12.345.678/0001-99', 'contato@acoforte.dev', '+55 84 4000-1000', 5, true),
    ('44444444-4444-4444-4444-444444444444', 'SUP-2026-002', 'Concreto Norte Engenharia', 'Concreto Norte', '98.765.432/0001-44', 'fornecedores@concretonorte.dev', '+55 81 4000-2000', 4, true),
    ('50000001-0000-0000-0000-000000000001', 'SUP-003', 'Cimento Nassau S.A.', 'Nassau', '11.222.333/0001-44', 'vendas@nassau.com.br', '(81) 3400-1000', 5, true),
    ('50000001-0000-0000-0000-000000000002', 'SUP-004', 'Gerdau Aços Longos', 'Gerdau', '22.333.444/0001-55', 'comercial@gerdau.com.br', '(51) 3400-2000', 5, true),
    ('50000001-0000-0000-0000-000000000003', 'SUP-005', 'Madeireira Tropical', 'Tropical', '33.444.555/0001-66', 'vendas@tropical.com.br', '(84) 3300-3000', 4, true),
    ('50000001-0000-0000-0000-000000000004', 'SUP-006', 'Hidráulica Total Ltda', 'Hidráulica Total', '44.555.666/0001-77', 'orcamento@hidraulica.com.br', '(84) 3300-4000', 4, true);

-- === Invoices ===
INSERT INTO invoice (id, number, budget_id, supplier_id, amount, issue_date, due_date, status, notes) VALUES
    ('55555555-5555-5555-5555-555555555555', 'INV-2026-001', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 120000.00, '2026-03-04', '2026-03-29', 'PAID', 'Lote estrutural'),
    ('66666666-6666-6666-6666-666666666666', 'INV-2026-002', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', 320000.00, '2026-04-09', '2026-04-30', 'OVERDUE', 'Concretagem bloco B');

-- === Clients ===
INSERT INTO client (id, name, document, email, phone, address, city, state, notes) VALUES
    ('c0000001-0000-0000-0000-000000000001', 'Construtora Horizonte Ltda', '12.345.678/0001-90', 'contato@horizonte.com.br', '(84) 3211-5000', 'Av. Prudente de Morais, 1200', 'Natal', 'RN', 'Cliente desde 2020'),
    ('c0000001-0000-0000-0000-000000000002', 'Governo do Estado RN', '08.241.739/0001-05', 'licitacoes@rn.gov.br', '(84) 3232-1000', 'Centro Administrativo', 'Natal', 'RN', 'Contrato via licitação'),
    ('c0000001-0000-0000-0000-000000000003', 'TechPark Logística S.A.', '45.678.901/0001-23', 'obras@techpark.com.br', '(84) 3344-2000', 'Distrito Industrial', 'Parnamirim', 'RN', null);

-- === Employees ===
INSERT INTO employee (id, name, document, role, type, email, phone, hourly_rate, admission_date) VALUES
    ('e0000001-0000-0000-0000-000000000001', 'João Carlos Pereira', '123.456.789-00', 'Mestre de Obras', 'EMPLOYEE', 'joao@obra.com', '(84) 99900-1001', 45.00, '2024-03-15'),
    ('e0000001-0000-0000-0000-000000000002', 'Maria Silva Santos', '234.567.890-11', 'Engenheira Civil', 'EMPLOYEE', 'maria@obra.com', '(84) 99900-1002', 120.00, '2023-08-01'),
    ('e0000001-0000-0000-0000-000000000003', 'Pedro Oliveira', '345.678.901-22', 'Pedreiro', 'EMPLOYEE', null, '(84) 99900-1003', 28.00, '2025-01-10'),
    ('e0000001-0000-0000-0000-000000000004', 'Terraplanagem Norte Ltda', '56.789.012/0001-34', 'Terraplanagem', 'CONTRACTOR', 'terra@norte.com', '(84) 3300-4000', null, '2026-01-20'),
    ('e0000001-0000-0000-0000-000000000005', 'Elétrica Potiguar ME', '67.890.123/0001-45', 'Instalações Elétricas', 'CONTRACTOR', 'eletrica@potiguar.com', '(84) 3300-5000', null, '2026-02-01');

-- === Payment Methods ===
INSERT INTO payment_method (id, name, installments) VALUES
    ('00000001-0000-0000-0000-000000000001', 'À Vista', 1),
    ('00000001-0000-0000-0000-000000000002', '30/60/90 dias', 3),
    ('00000001-0000-0000-0000-000000000003', 'Boleto 28 dias', 1),
    ('00000001-0000-0000-0000-000000000004', '30/60 dias', 2);

-- === Bank Accounts ===
INSERT INTO bank_account (id, bank_code, bank_name, agency, account_number, account_type, holder_name) VALUES
    ('ba000001-0000-0000-0000-000000000001', '001', 'Banco do Brasil', '3456-7', '12345-6', 'CHECKING', 'SinapiPRO Engenharia Ltda'),
    ('ba000001-0000-0000-0000-000000000002', '104', 'Caixa Econômica', '0891', '00012345-0', 'CHECKING', 'SinapiPRO Engenharia Ltda');

-- === Equipment ===
INSERT INTO equipment (id, code, name, type, brand, model, year, license_plate, hourly_cost, status, current_hours, current_km, next_maintenance_hours, next_maintenance_date) VALUES
    ('e1000001-0000-0000-0000-000000000001', 'EQ-001', 'Retroescavadeira CAT 416F2', 'RETROESCAVADEIRA', 'Caterpillar', '416F2', 2022, null, 180.00, 'AVAILABLE', 1250.00, 0, 1500.00, '2026-07-01'),
    ('e1000001-0000-0000-0000-000000000002', 'EQ-002', 'Betoneira 400L', 'BETONEIRA', 'CSM', 'CS 400', 2023, null, 35.00, 'AVAILABLE', 800.00, 0, 1000.00, null),
    ('e1000001-0000-0000-0000-000000000003', 'EQ-003', 'Caminhão Basculante', 'CAMINHAO', 'Mercedes-Benz', 'Atego 1719', 2021, 'RNX-4B56', 95.00, 'AVAILABLE', 0, 45000.00, null, '2026-08-15');

-- === SINAPI Materials ===
INSERT INTO material (id, sinapi_code, description, unit, origin) VALUES
    ('a0000001-0000-0000-0000-000000000001', '00000370', 'CIMENTO PORTLAND COMPOSTO CP II-32', 'KG', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000002', '00000367', 'AREIA MEDIA - POSTO JAZIDA/FORNECEDOR', 'M3', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000003', '00000368', 'PEDRA BRITADA N. 1 (9,5 A 19 MM) POSTO PEDREIRA', 'M3', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000004', '00000371', 'AGUA', 'M3', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000005', '00002692', 'SERVENTE COM ENCARGOS COMPLEMENTARES', 'H', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000006', '00002436', 'PEDREIRO COM ENCARGOS COMPLEMENTARES', 'H', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000007', '00004750', 'BETONEIRA CAPACIDADE NOMINAL 400 L, MOTOR ELETRICO', 'H', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000008', '00000553', 'ACO CA-50, 10,0 MM, VERGALHAO', 'KG', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000009', '00000554', 'ARAME RECOZIDO 18 BWG, D = 1,25 MM', 'KG', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000010', '00003777', 'ARMADOR COM ENCARGOS COMPLEMENTARES', 'H', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000011', '00000483', 'TIJOLO CERAMICO FURADO 9X19X19 CM', 'UN', 'SINAPI'),
    ('a0000001-0000-0000-0000-000000000012', '00001379', 'ARGAMASSA TRACO 1:2:8 (CIMENTO, CAL E AREIA)', 'M3', 'SINAPI');

-- === Material Prices (RN, Jan/2026) ===
INSERT INTO material_price (id, material_id, state, reference_month, price) VALUES
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001', 'RN', '2026-01-01', 0.7200),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000002', 'RN', '2026-01-01', 95.0000),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000003', 'RN', '2026-01-01', 110.0000),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000004', 'RN', '2026-01-01', 12.5000),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000005', 'RN', '2026-01-01', 22.4800),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000006', 'RN', '2026-01-01', 28.9200),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000007', 'RN', '2026-01-01', 1.8500),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000008', 'RN', '2026-01-01', 7.4500),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000009', 'RN', '2026-01-01', 12.8000),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000010', 'RN', '2026-01-01', 27.3500),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000011', 'RN', '2026-01-01', 0.5800),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000012', 'RN', '2026-01-01', 580.0000);

-- === Compositions ===
INSERT INTO composition (id, sinapi_code, description, unit, group_name, origin) VALUES
    ('c0000001-0000-0000-0000-000000000001', '87548', 'CONCRETO FCK=25MPA, VIRADO EM BETONEIRA', 'M3', 'ESTRUTURA', 'SINAPI'),
    ('c0000001-0000-0000-0000-000000000002', '92781', 'ARMACAO DE ESTRUTURAS - ACO CA-50 DE 10,0 MM', 'KG', 'ESTRUTURA', 'SINAPI'),
    ('c0000001-0000-0000-0000-000000000003', '87529', 'ALVENARIA DE VEDACAO COM TIJOLO CERAMICO FURADO', 'M2', 'ALVENARIA', 'SINAPI');

-- === Composition Items ===
INSERT INTO composition_item (id, composition_id, material_id, coefficient) VALUES
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001', 320.000000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000002', 0.660000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000003', 0.880000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000004', 0.200000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000005', 6.000000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000007', 1.000000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000008', 1.100000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000009', 0.020000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000010', 0.100000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000011', 25.000000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000012', 0.020000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000006', 0.900000),
    (gen_random_uuid(), 'c0000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000005', 0.500000);

-- === BDI Config ===
INSERT INTO bdi_config (id, budget_id, administration, profit, taxes, social_charges, financial_expenses, risks) VALUES
    ('b0000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 0.0400, 0.0800, 0.0650, 0.0000, 0.0120, 0.0100);

-- === Budget Stages ===
INSERT INTO budget_stage (id, budget_id, parent_id, name, sort_order) VALUES
    ('b1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', null, 'Infraestrutura', 1),
    ('b1000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', null, 'Superestrutura', 2),
    ('b1000001-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', null, 'Alvenaria e Vedação', 3),
    ('b1000001-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 'b1000001-0000-0000-0000-000000000001', 'Fundações', 1),
    ('b1000001-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111', 'b1000001-0000-0000-0000-000000000002', 'Pilares e Vigas', 1);

-- === Budget Items ===
INSERT INTO budget_item (id, stage_id, composition_id, quantity, unit_cost, bdi_pct) VALUES
    ('b2000001-0000-0000-0000-000000000001', 'b1000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000001', 120.0000, 450.5000, 0.2270),
    ('b2000001-0000-0000-0000-000000000002', 'b1000001-0000-0000-0000-000000000005', 'c0000001-0000-0000-0000-000000000002', 85.0000, 320.0000, 0.2270),
    ('b2000001-0000-0000-0000-000000000003', 'b1000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000003', 1200.0000, 28.5000, 0.2270);

-- === Cost Codes ===
INSERT INTO cost_code (id, budget_id, parent_id, code, name, budgeted_amount) VALUES
    ('cc000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', null, '01', 'Serviços Preliminares', 150000.00),
    ('cc000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', null, '02', 'Infraestrutura', 450000.00),
    ('cc000001-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', null, '03', 'Superestrutura', 680000.00),
    ('cc000001-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 'cc000001-0000-0000-0000-000000000002', '02.01', 'Fundações Profundas', 280000.00),
    ('cc000001-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111', 'cc000001-0000-0000-0000-000000000003', '03.01', 'Formas e Escoramentos', 320000.00);

-- === Cost Transactions ===
INSERT INTO cost_transaction (id, cost_code_id, type, amount, description, transaction_date) VALUES
    (gen_random_uuid(), 'cc000001-0000-0000-0000-000000000004', 'ACTUAL', 95000.00, 'Estacas hélice contínua - Bloco A', '2026-02-15'),
    (gen_random_uuid(), 'cc000001-0000-0000-0000-000000000004', 'ACTUAL', 88000.00, 'Estacas hélice contínua - Bloco B', '2026-03-10'),
    (gen_random_uuid(), 'cc000001-0000-0000-0000-000000000005', 'COMMITTED', 150000.00, 'Contrato formas metálicas', '2026-04-01'),
    (gen_random_uuid(), 'cc000001-0000-0000-0000-000000000005', 'ACTUAL', 72000.00, 'Formas 1o e 2o pavimento', '2026-04-20');

-- === Schedule Activities ===
INSERT INTO schedule_activity (id, budget_id, name, planned_start, planned_end, weight, progress_pct, sort_order) VALUES
    ('5a000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Terraplanagem', '2026-01-15', '2026-02-15', 0.05, 100, 1),
    ('5a000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Fundações', '2026-02-01', '2026-04-15', 0.15, 100, 2),
    ('5a000001-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'Estrutura', '2026-03-15', '2026-08-30', 0.25, 45, 3),
    ('5a000001-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 'Alvenaria', '2026-06-01', '2026-10-30', 0.15, 10, 4),
    ('5a000001-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111', 'Instalações Elétricas', '2026-07-01', '2026-12-15', 0.12, 0, 5),
    ('5a000001-0000-0000-0000-000000000006', '11111111-1111-1111-1111-111111111111', 'Instalações Hidráulicas', '2026-07-15', '2026-12-30', 0.10, 0, 6),
    ('5a000001-0000-0000-0000-000000000007', '11111111-1111-1111-1111-111111111111', 'Revestimentos', '2026-09-01', '2027-03-30', 0.10, 0, 7),
    ('5a000001-0000-0000-0000-000000000008', '11111111-1111-1111-1111-111111111111', 'Pintura e Acabamentos', '2027-02-01', '2027-05-30', 0.05, 0, 8),
    ('5a000001-0000-0000-0000-000000000009', '11111111-1111-1111-1111-111111111111', 'Área de Lazer', '2027-03-01', '2027-06-15', 0.03, 0, 9);

-- === Activity Dependencies ===
INSERT INTO activity_dependency (id, predecessor_id, successor_id, type) VALUES
    (gen_random_uuid(), '5a000001-0000-0000-0000-000000000001', '5a000001-0000-0000-0000-000000000002', 'FS'),
    (gen_random_uuid(), '5a000001-0000-0000-0000-000000000002', '5a000001-0000-0000-0000-000000000003', 'FS'),
    (gen_random_uuid(), '5a000001-0000-0000-0000-000000000003', '5a000001-0000-0000-0000-000000000004', 'FS'),
    (gen_random_uuid(), '5a000001-0000-0000-0000-000000000004', '5a000001-0000-0000-0000-000000000005', 'FS');

-- === Schedule Baseline ===
INSERT INTO schedule_baseline (id, project_id, name, snapshot) VALUES
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001', 'Baseline Original (Jan/2026)',
     '{"activities":[{"name":"Terraplanagem","start":"2026-01-15","end":"2026-02-15","weight":0.05},{"name":"Fundações","start":"2026-02-01","end":"2026-04-15","weight":0.15},{"name":"Estrutura","start":"2026-03-15","end":"2026-08-30","weight":0.25}]}');

-- === Contract ===
INSERT INTO contract (id, budget_id, supplier_id, number, description, original_value, retention_pct, status, start_date, end_date) VALUES
    ('c1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000002',
     'CTR-2026-001', 'Fornecimento e montagem de estrutura metálica - Blocos A e B',
     1850000.00, 0.05, 'ACTIVE', '2026-02-01', '2026-10-30');

INSERT INTO contract_item (id, contract_id, description, quantity, unit_price) VALUES
    (gen_random_uuid(), 'c1000001-0000-0000-0000-000000000001', 'Pilar metálico W200x46 (6m)', 48.0000, 12500.0000),
    (gen_random_uuid(), 'c1000001-0000-0000-0000-000000000001', 'Viga metálica W310x52 (8m)', 96.0000, 8200.0000),
    (gen_random_uuid(), 'c1000001-0000-0000-0000-000000000001', 'Montagem e soldagem', 1.0000, 285000.0000);

INSERT INTO change_order (id, contract_id, number, description, amount, status, justification, approved_at) VALUES
    ('c0000011-0000-0000-0000-000000000001', 'c1000001-0000-0000-0000-000000000001', 1,
     'Reforço estrutural - mudança de projeto arquitetônico', 125000.00, 'APPROVED',
     'Alteração solicitada pelo cliente para suportar cobertura verde no terraço', now() - interval '15 days');

-- === Measurements ===
INSERT INTO measurement (id, budget_id, number, period_start, period_end, status, retention_pct) VALUES
    ('ae000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 1, '2026-02-01', '2026-02-28', 'PAID', 0.05),
    ('ae000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 2, '2026-03-01', '2026-03-31', 'APPROVED', 0.05),
    ('ae000001-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 3, '2026-04-01', '2026-04-30', 'SUBMITTED', 0.05),
    ('ae000001-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 4, '2026-05-01', '2026-05-31', 'DRAFT', 0.05);

INSERT INTO measurement_item (id, measurement_id, description, quantity, unit_price) VALUES
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000001', 'Fundação - Estacas', 120.00, 850.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000001', 'Fundação - Blocos', 45.00, 1200.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000002', 'Estrutura - Pilares Bloco A', 32.00, 2800.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000002', 'Estrutura - Vigas Bloco A', 64.00, 1500.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000003', 'Estrutura - Lajes Bloco A', 16.00, 4200.00),
    (gen_random_uuid(), 'ae000001-0000-0000-0000-000000000004', 'Alvenaria - Bloco A Térreo', 280.00, 95.00);

-- === Purchase Requests ===
INSERT INTO purchase_request (id, budget_id, description, quantity, unit, status, requested_by) VALUES
    ('00000002-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Cimento CP-II 50kg', 500.00, 'saco', 'CLOSED', 'João Carlos'),
    ('00000002-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Aço CA-50 10mm', 8000.00, 'kg', 'OPEN', 'Maria Silva');

-- === Quotation ===
INSERT INTO quotation (id, purchase_request_id, status, deadline) VALUES
    ('a0000001-0000-0000-0000-000000000001', '00000002-0000-0000-0000-000000000002', 'CLOSED', '2026-05-10');

INSERT INTO quotation_response (id, quotation_id, supplier_id, unit_price, delivery_days, notes) VALUES
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001', '50000001-0000-0000-0000-000000000002', 7.00, 5, 'Entrega imediata do estoque'),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001', '50000001-0000-0000-0000-000000000003', 7.45, 10, 'Prazo de 10 dias úteis'),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001', '33333333-3333-3333-3333-333333333333', 6.80, 7, 'Frete incluso acima de 5 ton');

-- === Purchase Orders ===
INSERT INTO purchase_order (id, budget_id, supplier_id, number, description, quantity, unit_price, status, expected_delivery_date) VALUES
    ('00000003-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000001', 'PED-2026-001', 'Cimento CP-II 50kg', 500.00, 45.00, 'RECEIVED', '2026-05-01'),
    ('00000003-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000002', 'PED-2026-002', 'Aço CA-50 10mm (8 ton)', 8000.00, 7.00, 'APPROVED', '2026-05-18'),
    ('00000003-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000004', 'PED-2026-003', 'Tubos PVC 100mm', 200.00, 32.50, 'PENDING', '2026-05-25');

INSERT INTO receiving (id, purchase_order_id, quantity_received, received_at, notes) VALUES
    (gen_random_uuid(), '00000003-0000-0000-0000-000000000001', 500.00, '2026-05-01', 'Recebido conforme NF 4521 - conferido OK');

INSERT INTO purchase_order_cost_distribution (id, purchase_order_id, cost_code_id, percentage, amount) VALUES
    (gen_random_uuid(), '00000003-0000-0000-0000-000000000001', 'cc000001-0000-0000-0000-000000000005', 0.7000, 15750.00),
    (gen_random_uuid(), '00000003-0000-0000-0000-000000000001', 'cc000001-0000-0000-0000-000000000004', 0.3000, 6750.00);

-- === Daily Log ===
INSERT INTO daily_log (id, budget_id, log_date, weather_morning, weather_afternoon, observations) VALUES
    ('d1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '2026-05-09', 'Ensolarado', 'Parcialmente nublado', 'Concretagem do 3o pavimento Bloco A concluída. Equipe de armação iniciou 4o pavimento.'),
    ('d1000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', '2026-05-08', 'Nublado', 'Chuvoso', 'Chuva no período da tarde paralisou concretagem por 2h. Equipe realocada para armação interna.');

INSERT INTO daily_log_labor (id, daily_log_id, worker_name, role, hours) VALUES
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'João Carlos Pereira', 'Mestre de Obras', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Pedro Oliveira', 'Pedreiro', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Antônio Souza', 'Armador', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Francisco Lima', 'Carpinteiro', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'José Ferreira', 'Servente', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', 'João Carlos Pereira', 'Mestre de Obras', 8.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', 'Pedro Oliveira', 'Pedreiro', 6.00);

INSERT INTO daily_log_equipment (id, daily_log_id, equipment_name, hours_used, hours_idle) VALUES
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Grua Torre 40m', 8.00, 0.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'Betoneira 400L', 6.00, 2.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', 'Grua Torre 40m', 6.00, 2.00);

INSERT INTO daily_log_occurrence (id, daily_log_id, type, description) VALUES
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', 'PARALISACAO', 'Chuva forte das 14h às 16h. Concretagem suspensa por segurança.');

INSERT INTO daily_log_photo (id, daily_log_id, file_path, caption) VALUES
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', '/photos/2026-05-09/concretagem_3pav_01.jpg', 'Concretagem 3o pavimento - Bloco A'),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', '/photos/2026-05-09/concretagem_3pav_02.jpg', 'Vibração do concreto'),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000002', '/photos/2026-05-08/chuva_canteiro.jpg', 'Chuva no canteiro - tarde');

-- === Equipment Usage ===
INSERT INTO equipment_usage (id, equipment_id, budget_id, usage_date, hours_used, km_used, operator, notes) VALUES
    (gen_random_uuid(), 'e1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '2026-05-08', 6.50, 0, 'Marcos Operador', 'Escavação bloco C'),
    (gen_random_uuid(), 'e1000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', '2026-05-09', 8.00, 0, 'Pedro Oliveira', 'Concretagem 3o pav'),
    (gen_random_uuid(), 'e1000001-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', '2026-05-09', 4.00, 85.00, 'José Motorista', 'Transporte de areia');

INSERT INTO equipment_maintenance (id, equipment_id, maintenance_date, type, description, cost, hours_at_maintenance) VALUES
    (gen_random_uuid(), 'e1000001-0000-0000-0000-000000000001', '2026-04-15', 'PREVENTIVA', 'Troca de óleo e filtros', 2800.00, 1200.00),
    (gen_random_uuid(), 'e1000001-0000-0000-0000-000000000002', '2026-03-20', 'CORRETIVA', 'Substituição correia do motor', 450.00, 780.00);

INSERT INTO equipment_fueling (id, equipment_id, budget_id, fueling_date, fuel_type, liters, cost_per_liter, total_cost, odometer) VALUES
    (gen_random_uuid(), 'e1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', '2026-05-08', 'DIESEL_S10', 120.00, 6.4500, 774.00, null),
    (gen_random_uuid(), 'e1000001-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', '2026-05-09', 'DIESEL_S10', 80.00, 6.4500, 516.00, 45085.00);

-- === Documents ===
INSERT INTO document (id, budget_id, entity_type, entity_id, title, file_name, content_type, file_size, storage_path, version, uploaded_by) VALUES
    ('d2000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'PROJECT', 'a0000001-0000-0000-0000-000000000001', 'Projeto Estrutural - Rev.03', 'projeto_estrutural_rev03.pdf', 'application/pdf', 15728640, '/docs/projects/OBR-2026-001/estrutural_rev03.pdf', 3, 'Eng. Maria Fernanda'),
    ('d2000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'CONTRACT', 'c1000001-0000-0000-0000-000000000001', 'Contrato Estrutura Metálica', 'contrato_ctr_2026_001.pdf', 'application/pdf', 2097152, '/docs/contracts/CTR-2026-001.pdf', 1, 'Admin');

INSERT INTO document_version (id, document_id, version_number, file_path, uploaded_by, notes) VALUES
    (gen_random_uuid(), 'd2000001-0000-0000-0000-000000000001', 1, '/docs/projects/OBR-2026-001/estrutural_rev01.pdf', 'Eng. Maria Fernanda', 'Versão inicial'),
    (gen_random_uuid(), 'd2000001-0000-0000-0000-000000000001', 2, '/docs/projects/OBR-2026-001/estrutural_rev02.pdf', 'Eng. Maria Fernanda', 'Correção de armaduras'),
    (gen_random_uuid(), 'd2000001-0000-0000-0000-000000000001', 3, '/docs/projects/OBR-2026-001/estrutural_rev03.pdf', 'Eng. Maria Fernanda', 'Inclusão reforço terraço verde');

-- === RFI ===
INSERT INTO rfi (id, budget_id, number, subject, question, answer, status, priority, assigned_to, created_by, due_date, answered_at) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 1, 'Especificação do concreto para lajes', 'Qual o fck mínimo para as lajes do 4o pavimento?', 'Utilizar fck 35 MPa conforme memória de cálculo revisada.', 'CLOSED', 'HIGH', 'Eng. Carlos Alberto', 'João Carlos Pereira', '2026-04-20', now() - interval '20 days'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 2, 'Passagem de tubulação no pilar P12', 'É possível fazer furo no pilar P12 para passagem de tubulação de 75mm?', null, 'OPEN', 'URGENT', 'Eng. Maria Fernanda', 'Pedro Oliveira', '2026-05-12', null);

-- === Punch List ===
INSERT INTO punch_list_item (id, budget_id, location, description, category, priority, status, assigned_to, due_date, created_by) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Bloco A - 2o Pav - Apto 201', 'Fissura na laje do banheiro', 'Estrutura', 'HIGH', 'OPEN', 'Pedro Oliveira', '2026-05-20', 'Eng. Carlos Alberto'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Bloco A - Térreo - Hall', 'Desnível no piso do hall de entrada (>3mm)', 'Acabamento', 'MEDIUM', 'IN_PROGRESS', 'Equipe Revestimento', '2026-05-25', 'João Carlos Pereira');

-- === Safety ===
INSERT INTO safety_checklist_template (id, name, category, items, active) VALUES
    ('50000002-0000-0000-0000-000000000001', 'Inspeção Diária de Canteiro', 'CANTEIRO',
     '[{"item":"EPIs em uso","required":true},{"item":"Sinalização adequada","required":true},{"item":"Proteção periférica","required":true},{"item":"Ordem e limpeza","required":true},{"item":"Extintores acessíveis","required":true}]', true);

INSERT INTO safety_inspection (id, budget_id, template_id, inspector, inspection_date, status, results, notes) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '50000002-0000-0000-0000-000000000001',
     'Técnico Marcos Almeida', '2026-05-09', 'PASS',
     '[{"item":"EPIs em uso","ok":true},{"item":"Sinalização adequada","ok":true},{"item":"Proteção periférica","ok":true},{"item":"Ordem e limpeza","ok":true},{"item":"Extintores acessíveis","ok":true}]',
     'Canteiro em conformidade');

INSERT INTO safety_incident (id, budget_id, incident_date, severity, description, location, corrective_action, reported_by, status) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '2026-04-22', 'MINOR', 'Queda de material (tijolo) do 3o pavimento. Ninguém atingido.', 'Bloco A - Fachada Norte', 'Reforço da tela de proteção periférica e DDS sobre amarração de materiais em altura.', 'Técnico Marcos Almeida', 'CLOSED');

-- === Submittals ===
INSERT INTO submittal (id, budget_id, number, title, spec_section, type, submitted_by, assigned_to, status, due_date, submitted_at, reviewed_at, reviewer_notes) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 1, 'Amostra de porcelanato - Hall Social', '09.30', 'SAMPLE', 'Fornecedor Cerâmicas BR', 'Eng. Carlos Alberto', 'APPROVED', '2026-04-15', now() - interval '30 days', now() - interval '25 days', 'Aprovado. Cor Bianco Carrara.'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 2, 'Shop Drawing - Instalações Elétricas Bloco B', '16.00', 'SHOP_DRAWING', 'Elétrica Potiguar ME', 'Eng. Maria Fernanda', 'UNDER_REVIEW', '2026-05-15', now() - interval '5 days', null, null);

-- === Weather Delay ===
INSERT INTO weather_delay (id, budget_id, delay_date, weather_condition, hours_lost, full_day_lost, impact_description, reported_by) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '2026-05-08', 'Chuva forte', 2.00, false, 'Paralisação da concretagem por 2h', 'João Carlos Pereira'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '2026-04-10', 'Temporal com ventos', 8.00, true, 'Dia inteiro perdido - ventos acima de 60km/h', 'João Carlos Pereira');

-- === Timesheet ===
INSERT INTO timesheet_entry (id, budget_id, cost_code_id, worker_name, role, work_date, regular_hours, overtime_hours, hourly_rate, units_produced, unit_type, notes) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'cc000001-0000-0000-0000-000000000005', 'Pedro Oliveira', 'Pedreiro', '2026-05-09', 8.00, 2.00, 28.92, 12.00, 'm2', 'Formas pilares 4o pav'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'cc000001-0000-0000-0000-000000000005', 'Antônio Souza', 'Armador', '2026-05-09', 8.00, 0.00, 27.35, 450.00, 'kg', 'Armação vigas 4o pav'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'cc000001-0000-0000-0000-000000000004', 'Francisco Lima', 'Carpinteiro', '2026-05-09', 8.00, 1.00, 26.50, 8.00, 'm2', 'Formas blocos fundação Bloco C');

-- === Financial: Payables ===
INSERT INTO payable (id, budget_id, supplier_id, description, amount, due_date, status, category) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000001', 'NF 4521 - Cimento CP-II (500 sacos)', 22500.00, '2026-05-15', 'PENDING', 'Material'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '50000001-0000-0000-0000-000000000002', 'NF 8901 - Aço CA-50 (8 ton)', 56000.00, '2026-05-20', 'PENDING', 'Material'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', null, 'Aluguel de grua - Maio/2026', 18000.00, '2026-05-10', 'PENDING', 'Equipamento'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', null, 'Folha de pagamento - Abril/2026', 85000.00, '2026-05-05', 'PAID', 'Mão de Obra');

-- === Financial: Receivables ===
INSERT INTO receivable (id, budget_id, description, amount, due_date, status, category) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Medição #1 - Fundações', 102000.00, '2026-03-30', 'PAID', 'Medição'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Medição #2 - Estrutura', 185600.00, '2026-04-30', 'PAID', 'Medição'),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Medição #3 - Estrutura (cont.)', 67200.00, '2026-05-30', 'PENDING', 'Medição');

-- === Stock ===
INSERT INTO stock_item (id, budget_id, description, unit, current_quantity, min_quantity, location) VALUES
    ('c1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Cimento CP-II 50kg', 'saco', 120.00, 50.00, 'Almoxarifado A'),
    ('c1000001-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Aço CA-50 10mm', 'kg', 2500.00, 1000.00, 'Pátio de Aço'),
    ('c1000001-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'Tijolo Cerâmico 9x14x19', 'un', 8000.00, 5000.00, 'Almoxarifado B'),
    ('c1000001-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 'Areia Média', 'm3', 15.00, 20.00, 'Pátio');

INSERT INTO stock_movement (id, stock_item_id, type, quantity, reference_type, notes) VALUES
    (gen_random_uuid(), 'c1000001-0000-0000-0000-000000000001', 'IN', 500.00, 'PURCHASE_ORDER', 'Recebimento PED-2026-001'),
    (gen_random_uuid(), 'c1000001-0000-0000-0000-000000000001', 'OUT', 380.00, 'REQUISITION', 'Consumo concretagem Blocos A e B'),
    (gen_random_uuid(), 'c1000001-0000-0000-0000-000000000002', 'OUT', 1200.00, 'REQUISITION', 'Armação 1o ao 3o pavimento');

INSERT INTO stock_requisition (id, budget_id, requested_by, status, notes) VALUES
    ('d1000001-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Pedro Oliveira', 'APPROVED', 'Material para alvenaria Bloco A térreo');

INSERT INTO stock_requisition_item (id, requisition_id, stock_item_id, quantity, delivered_quantity) VALUES
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'c1000001-0000-0000-0000-000000000003', 2000.00, 2000.00),
    (gen_random_uuid(), 'd1000001-0000-0000-0000-000000000001', 'c1000001-0000-0000-0000-000000000001', 50.00, 50.00);

-- === Commercial: Development + Units ===
INSERT INTO development (id, name, address, city, state, total_units, status, launch_date) VALUES
    ('d0000001-0000-0000-0000-000000000001', 'Residencial Parque das Flores', 'Rua das Acácias, 500', 'Natal', 'RN', 64, 'LAUNCHED', '2026-01-10');

INSERT INTO development_unit (id, development_id, code, type, area, price, status, floor, bedrooms) VALUES
    ('d3000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', 'A-101', 'Apartamento', 72.00, 380000.00, 'SOLD', 1, 2),
    ('d3000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000001', 'A-102', 'Apartamento', 72.00, 380000.00, 'SOLD', 1, 2),
    ('d3000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000001', 'A-201', 'Apartamento', 72.00, 395000.00, 'RESERVED', 2, 2),
    ('d3000001-0000-0000-0000-000000000004', 'd0000001-0000-0000-0000-000000000001', 'A-202', 'Apartamento', 72.00, 395000.00, 'AVAILABLE', 2, 2),
    ('d3000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000001', 'A-301', 'Apartamento', 85.00, 450000.00, 'AVAILABLE', 3, 3),
    ('d3000001-0000-0000-0000-000000000006', 'd0000001-0000-0000-0000-000000000001', 'B-101', 'Apartamento', 65.00, 340000.00, 'AVAILABLE', 1, 2),
    ('d3000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000001', 'B-102', 'Apartamento', 65.00, 340000.00, 'AVAILABLE', 1, 2);

-- === Sales Proposals ===
INSERT INTO sales_proposal (id, unit_id, client_id, client_name, proposal_date, proposed_price, down_payment, installments, status, notes) VALUES
    ('d2000001-0000-0000-0000-000000000001', 'd3000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', 'Carlos Mendes', '2026-02-15', 375000.00, 75000.00, 60, 'ACCEPTED', 'Financiamento Caixa'),
    ('d2000001-0000-0000-0000-000000000002', 'd3000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000002', 'Ana Paula Rodrigues', '2026-03-01', 370000.00, 50000.00, 48, 'ACCEPTED', 'Pagamento direto'),
    (gen_random_uuid(), 'd3000001-0000-0000-0000-000000000003', null, 'Roberto Silva', '2026-04-20', 385000.00, 80000.00, 36, 'PENDING', 'Aguardando aprovação de crédito');

INSERT INTO broker_commission (id, proposal_id, broker_name, percentage, amount, status, paid_date) VALUES
    (gen_random_uuid(), 'd2000001-0000-0000-0000-000000000001', 'Imobiliária Natal Prime', 0.0500, 18750.00, 'PAID', '2026-03-15');

-- === Service Tickets (pós-venda) ===
INSERT INTO service_ticket (id, client_name, category, description, priority, status, assigned_to, due_date, opened_at) VALUES
    (gen_random_uuid(), 'Carlos Mendes (A-101)', 'Hidráulica', 'Vazamento no registro do banheiro social', 'HIGH', 'IN_PROGRESS', 'Hidráulica Total', '2026-05-15', now() - interval '3 days'),
    (gen_random_uuid(), 'Ana Paula (A-102)', 'Elétrica', 'Tomada da cozinha sem funcionar', 'MEDIUM', 'OPEN', null, '2026-05-20', now() - interval '1 day'),
    (gen_random_uuid(), 'Roberto Silva (A-201)', 'Pintura', 'Mancha de umidade na parede do quarto', 'LOW', 'OPEN', null, '2026-05-25', now());

-- === Notifications ===
INSERT INTO notification (id, budget_id, type, severity, title, message, entity_type, entity_id, recipient, read) VALUES
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'MEASUREMENT_SUBMITTED', 'INFO', 'Medição #3 enviada para aprovação', 'A medição do período Abr/2026 foi submetida por João Carlos.', 'MEASUREMENT', 'ae000001-0000-0000-0000-000000000003', 'admin@sinapipro.dev', false),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'STOCK_LOW', 'WARNING', 'Estoque baixo: Areia Média', 'Quantidade atual (15 m3) abaixo do mínimo (20 m3).', 'STOCK_ITEM', null, 'admin@sinapipro.dev', false),
    (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'RFI_OVERDUE', 'CRITICAL', 'RFI #2 vencida', 'A RFI sobre passagem de tubulação no pilar P12 está sem resposta.', 'RFI', null, 'admin@sinapipro.dev', true),
    (gen_random_uuid(), null, 'SYSTEM', 'INFO', 'Backup realizado com sucesso', 'Backup automático do banco de dados concluído às 03:00.', null, null, 'admin@sinapipro.dev', true);
