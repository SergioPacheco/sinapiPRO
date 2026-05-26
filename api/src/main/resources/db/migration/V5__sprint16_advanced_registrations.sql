-- V5: Sprint 16 — Cadastros Avançados
-- Transportadores, representantes, parâmetros obra, divisão insumos, audit trail

-- 16.1 Transportadores
CREATE TABLE IF NOT EXISTS transporter (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    name varchar(200) NOT NULL,
    document varchar(20),
    vehicle_plate varchar(10),
    vehicle_type varchar(50),
    phone varchar(30),
    cell_phone varchar(30),
    whatsapp varchar(30),
    email varchar(200),
    address varchar(400),
    city varchar(100),
    state varchar(2),
    postal_code varchar(10),
    notes varchar(500),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- 16.2 Representantes comerciais
CREATE TABLE IF NOT EXISTS sales_representative (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    name varchar(200) NOT NULL,
    document varchar(20),
    phone varchar(30),
    cell_phone varchar(30),
    whatsapp varchar(30),
    email varchar(200),
    commission_rate numeric(5,2) DEFAULT 0,
    region varchar(200),
    notes varchar(500),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- 16.3 Plano de contas — já existe chart_of_accounts, adicionar campos faltantes
ALTER TABLE chart_of_accounts ADD COLUMN IF NOT EXISTS description varchar(500);
ALTER TABLE chart_of_accounts ADD COLUMN IF NOT EXISTS accepts_entries boolean NOT NULL DEFAULT false;

-- 16.4 Parâmetros por obra
CREATE TABLE IF NOT EXISTS project_settings (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    project_id uuid NOT NULL REFERENCES project(id),
    key varchar(100) NOT NULL,
    value varchar(500),
    description varchar(200),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(project_id, key)
);

-- 16.5 Divisão/subdivisão de insumos (hierárquica)
CREATE TABLE IF NOT EXISTS input_category (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    code varchar(20) NOT NULL,
    name varchar(200) NOT NULL,
    parent_id uuid REFERENCES input_category(id),
    level int NOT NULL DEFAULT 1,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

-- 16.6 Múltiplos telefones/endereços (genérico para qualquer entidade)
CREATE TABLE IF NOT EXISTS contact_info (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    entity_type varchar(30) NOT NULL,
    entity_id uuid NOT NULL,
    info_type varchar(20) NOT NULL,
    label varchar(30) NOT NULL DEFAULT 'MAIN',
    value varchar(400) NOT NULL,
    is_primary boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_contact_info_entity ON contact_info(entity_type, entity_id);

-- 16.8 Audit trail
CREATE TABLE IF NOT EXISTS audit_log (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    entity_type varchar(60) NOT NULL,
    entity_id uuid NOT NULL,
    action varchar(20) NOT NULL,
    changed_by varchar(200),
    changed_at timestamptz NOT NULL DEFAULT now(),
    changes jsonb
);

CREATE INDEX IF NOT EXISTS idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_changed_at ON audit_log(changed_at);
