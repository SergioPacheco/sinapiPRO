-- ============================================================
-- V5: Registry auxiliary tables
-- ============================================================

CREATE TABLE contractor (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    document varchar(20),
    specialty varchar(100),
    phone varchar(30),
    email varchar(200),
    city varchar(100),
    state varchar(2),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE inspector (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    document varchar(20),
    role varchar(100),
    organization varchar(200),
    phone varchar(30),
    email varchar(200),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE bdi_template (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL UNIQUE,
    administration numeric(8,4) NOT NULL DEFAULT 0,
    profit numeric(8,4) NOT NULL DEFAULT 0,
    financial_cost numeric(8,4) NOT NULL DEFAULT 0,
    taxes numeric(8,4) NOT NULL DEFAULT 0,
    total numeric(8,4) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE social_charge (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL,
    type varchar(20) NOT NULL DEFAULT 'HOURLY',
    percentage numeric(8,4) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE payment_condition (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL UNIQUE,
    installments integer NOT NULL DEFAULT 1,
    description varchar(500),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE cost_center (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(30) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    description varchar(500),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE finance_category (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    code varchar(30) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    type varchar(20) NOT NULL DEFAULT 'OTHER',
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE project_type (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL UNIQUE,
    description varchar(500),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE default_stage (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL,
    sort_order integer NOT NULL DEFAULT 0,
    description varchar(500),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE incident_type (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL UNIQUE,
    severity varchar(20) NOT NULL DEFAULT 'MEDIUM',
    description varchar(500),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE epi (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    ca_number varchar(30),
    validity_months integer,
    description varchar(500),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE report_template (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    type varchar(30) NOT NULL DEFAULT 'BUDGET',
    description varchar(500),
    created_at timestamptz NOT NULL DEFAULT now()
);
