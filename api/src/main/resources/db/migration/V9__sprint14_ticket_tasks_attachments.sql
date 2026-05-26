-- V9: Sprint 14 — Tarefas e anexos em OS

CREATE TABLE IF NOT EXISTS service_ticket_task (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    ticket_id uuid NOT NULL,
    description varchar(300) NOT NULL,
    sort_order int NOT NULL DEFAULT 0,
    completed boolean NOT NULL DEFAULT false,
    completed_at timestamptz,
    completed_by varchar(140),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS service_ticket_attachment (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    ticket_id uuid NOT NULL,
    file_name varchar(200) NOT NULL,
    file_path varchar(500) NOT NULL,
    content_type varchar(100),
    file_size bigint,
    uploaded_by varchar(140),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);
