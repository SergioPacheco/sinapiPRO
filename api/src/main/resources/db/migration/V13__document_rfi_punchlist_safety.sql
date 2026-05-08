-- Document Management
create table document (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid references budget(id),
    entity_type varchar(40),
    entity_id uuid,
    title varchar(300) not null,
    file_name varchar(260) not null,
    content_type varchar(100) not null,
    file_size bigint not null,
    storage_path varchar(500) not null,
    version integer not null default 1,
    uploaded_by varchar(140),
    created_at timestamptz not null default now()
);
create index idx_document_budget on document(budget_id);
create index idx_document_entity on document(entity_type, entity_id);

-- RFI (Request for Information)
create table rfi (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id),
    number integer not null,
    subject varchar(300) not null,
    question text not null,
    answer text,
    status varchar(20) not null default 'OPEN',
    priority varchar(20) not null default 'NORMAL',
    assigned_to varchar(140),
    created_by varchar(140),
    due_date date,
    answered_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique(budget_id, number)
);
create index idx_rfi_budget on rfi(budget_id);

-- Punch List
create table punch_list_item (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id),
    location varchar(200) not null,
    description text not null,
    category varchar(60),
    priority varchar(20) not null default 'NORMAL',
    status varchar(20) not null default 'OPEN',
    assigned_to varchar(140),
    due_date date,
    completed_at timestamptz,
    created_by varchar(140),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create index idx_punch_list_budget on punch_list_item(budget_id);

-- Safety Checklist Templates
create table safety_checklist_template (
    id uuid primary key default gen_random_uuid(),
    name varchar(200) not null,
    category varchar(60) not null,
    items jsonb not null default '[]',
    active boolean not null default true,
    created_at timestamptz not null default now()
);

-- Safety Inspections (filled checklists)
create table safety_inspection (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id),
    template_id uuid not null references safety_checklist_template(id),
    inspector varchar(140) not null,
    inspection_date date not null,
    status varchar(20) not null default 'PASS',
    results jsonb not null default '[]',
    notes text,
    created_at timestamptz not null default now()
);
create index idx_safety_inspection_budget on safety_inspection(budget_id);

-- Safety Incidents
create table safety_incident (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id),
    incident_date date not null,
    severity varchar(20) not null,
    description text not null,
    location varchar(200),
    injured_party varchar(140),
    corrective_action text,
    reported_by varchar(140),
    status varchar(20) not null default 'OPEN',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create index idx_safety_incident_budget on safety_incident(budget_id);
