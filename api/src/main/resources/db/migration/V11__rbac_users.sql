-- V11: RBAC — Users, user_role, user_project_access

CREATE TABLE IF NOT EXISTS app_user (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    email varchar(200) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    external_id varchar(100) UNIQUE,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS user_role (
    user_id uuid NOT NULL REFERENCES app_user(id),
    role_id uuid NOT NULL REFERENCES role(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS user_project_access (
    user_id uuid NOT NULL REFERENCES app_user(id),
    project_id uuid NOT NULL,
    PRIMARY KEY (user_id, project_id)
);

-- Seed: create admin user (will be linked to first JWT login)
INSERT INTO role (id, name, description, created_at, updated_at)
VALUES (gen_random_uuid(), 'ADMIN', 'Administrador - acesso total', now(), now())
ON CONFLICT (name) DO NOTHING;
