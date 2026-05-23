-- ============================================================
-- V8: Deep registries — sub-registros + campos adicionais
-- ============================================================

-- ============================================================
-- 1. Evolução da tabela CLIENT
-- ============================================================
ALTER TABLE client ADD COLUMN IF NOT EXISTS person_type varchar(2) DEFAULT 'PF';
ALTER TABLE client ADD COLUMN IF NOT EXISTS state_registration varchar(30);
ALTER TABLE client ADD COLUMN IF NOT EXISTS municipal_registration varchar(30);
ALTER TABLE client ADD COLUMN IF NOT EXISTS classification varchar(30) DEFAULT 'PESSOA_FISICA';
ALTER TABLE client ADD COLUMN IF NOT EXISTS postal_code varchar(10);

CREATE TABLE client_contact (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id uuid NOT NULL REFERENCES client(id) ON DELETE CASCADE,
    name varchar(200) NOT NULL,
    role varchar(100),
    email varchar(200),
    phone varchar(30),
    department varchar(30) NOT NULL,
    is_primary boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_client_contact_client ON client_contact(client_id);
CREATE INDEX idx_client_contact_dept ON client_contact(client_id, department);

-- ============================================================
-- 2. Evolução da tabela SUPPLIER
-- ============================================================
ALTER TABLE supplier ADD COLUMN IF NOT EXISTS categories varchar(100)[] DEFAULT '{}';

CREATE TABLE supplier_document (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id) ON DELETE CASCADE,
    document_type varchar(30) NOT NULL,
    number varchar(60),
    issue_date date,
    expiry_date date,
    file_path varchar(500),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_supplier_document_supplier ON supplier_document(supplier_id);
CREATE INDEX idx_supplier_document_expiry ON supplier_document(expiry_date)
    WHERE expiry_date IS NOT NULL;

CREATE TABLE supplier_evaluation (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id) ON DELETE CASCADE,
    evaluation_date date NOT NULL,
    criterion varchar(30) NOT NULL,
    score integer NOT NULL CHECK (score BETWEEN 1 AND 5),
    evaluator varchar(140),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_supplier_evaluation_supplier ON supplier_evaluation(supplier_id);

CREATE TABLE supplier_bank_account (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id uuid NOT NULL REFERENCES supplier(id) ON DELETE CASCADE,
    bank_code varchar(10) NOT NULL,
    bank_name varchar(100) NOT NULL,
    agency varchar(20) NOT NULL,
    account_number varchar(30) NOT NULL,
    account_type varchar(20) NOT NULL DEFAULT 'CORRENTE',
    holder_name varchar(200),
    holder_document varchar(20),
    pix_key varchar(100),
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_supplier_bank_account_supplier ON supplier_bank_account(supplier_id);

-- ============================================================
-- 3. Evolução da tabela EMPLOYEE
-- ============================================================
ALTER TABLE employee ADD COLUMN IF NOT EXISTS pis_pasep varchar(20);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS ctps_number varchar(20);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS ctps_series varchar(10);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS ctps_state varchar(2);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS cbo_code varchar(10);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS cbo_description varchar(200);
ALTER TABLE employee ADD COLUMN IF NOT EXISTS category varchar(20) DEFAULT 'MENSALISTA';

CREATE TABLE employee_training (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id uuid NOT NULL REFERENCES employee(id) ON DELETE CASCADE,
    training_name varchar(200) NOT NULL,
    regulatory_standard varchar(20),
    completion_date date NOT NULL,
    expiry_date date,
    hours integer,
    institution varchar(200),
    certificate_path varchar(500),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_employee_training_employee ON employee_training(employee_id);
CREATE INDEX idx_employee_training_expiry ON employee_training(expiry_date)
    WHERE expiry_date IS NOT NULL;

CREATE TABLE employee_epi_delivery (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id uuid NOT NULL REFERENCES employee(id) ON DELETE CASCADE,
    epi_description varchar(200) NOT NULL,
    ca_number varchar(30),
    delivery_date date NOT NULL,
    expiry_date date,
    quantity integer NOT NULL DEFAULT 1,
    signature_path varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_employee_epi_employee ON employee_epi_delivery(employee_id);
CREATE INDEX idx_employee_epi_expiry ON employee_epi_delivery(expiry_date)
    WHERE expiry_date IS NOT NULL;

CREATE TABLE employee_medical_exam (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id uuid NOT NULL REFERENCES employee(id) ON DELETE CASCADE,
    exam_type varchar(30) NOT NULL,
    exam_date date NOT NULL,
    expiry_date date,
    physician varchar(200),
    crm varchar(20),
    result varchar(30) NOT NULL DEFAULT 'APTO',
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_employee_medical_employee ON employee_medical_exam(employee_id);
CREATE INDEX idx_employee_medical_expiry ON employee_medical_exam(expiry_date)
    WHERE expiry_date IS NOT NULL;

-- ============================================================
-- 4. Evolução da tabela PROJECT (obra)
-- ============================================================
ALTER TABLE project ADD COLUMN IF NOT EXISTS client_id uuid REFERENCES client(id);
ALTER TABLE project ADD COLUMN IF NOT EXISTS employee_id uuid REFERENCES employee(id);
ALTER TABLE project ADD COLUMN IF NOT EXISTS project_type varchar(30);
ALTER TABLE project ADD COLUMN IF NOT EXISTS contract_regime varchar(30);
ALTER TABLE project ADD COLUMN IF NOT EXISTS permit_number varchar(50);
ALTER TABLE project ADD COLUMN IF NOT EXISTS permit_expiry date;
ALTER TABLE project ADD COLUMN IF NOT EXISTS cei_cno varchar(30);
ALTER TABLE project ADD COLUMN IF NOT EXISTS postal_code varchar(10);

CREATE INDEX idx_project_client ON project(client_id);
CREATE INDEX idx_project_employee ON project(employee_id);

-- ============================================================
-- 5. Data migration: customer_name → client_id
-- ============================================================
INSERT INTO client (name, document, active)
SELECT DISTINCT customer_name, customer_document, true
FROM project
WHERE customer_name IS NOT NULL
  AND client_id IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM client c WHERE c.name = project.customer_name
  );

UPDATE project p
SET client_id = c.id
FROM client c
WHERE p.customer_name = c.name
  AND p.client_id IS NULL;

-- ============================================================
-- 6. Data migration: responsible_engineer → employee_id
-- ============================================================
UPDATE project p
SET employee_id = e.id
FROM employee e
WHERE p.responsible_engineer = e.name
  AND p.employee_id IS NULL;
