-- V7: Sprint 12 — Multas/sinistros e locação de equipamentos

CREATE TABLE IF NOT EXISTS vehicle_incident (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    vehicle_id uuid NOT NULL,
    type varchar(20) NOT NULL,
    incident_date date NOT NULL,
    description varchar(500) NOT NULL,
    cost numeric(12,2),
    driver_id uuid,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS equipment_rental (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    equipment_id uuid NOT NULL,
    project_id uuid NOT NULL,
    start_date date NOT NULL,
    expected_end_date date,
    actual_end_date date,
    daily_rate numeric(12,2) NOT NULL,
    total_cost numeric(12,2),
    status varchar(20) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_equipment_rental_status ON equipment_rental(status);
