-- V8: Sprint 8 — Controle de faltas/atestados/afastamentos

CREATE TABLE IF NOT EXISTS employee_absence (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    employee_id uuid NOT NULL,
    type varchar(30) NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    reason varchar(300),
    document_number varchar(50),
    justified boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_employee_absence_employee ON employee_absence(employee_id);
