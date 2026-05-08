-- Measurements (medições periódicas)
create table measurement (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id) on delete cascade,
    number integer not null,
    period_start date not null,
    period_end date not null,
    status varchar(20) not null default 'DRAFT',
    retention_pct numeric(5,4) not null default 0,
    notes text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (budget_id, number)
);

create index idx_measurement_budget on measurement(budget_id);

-- Measurement items
create table measurement_item (
    id uuid primary key default gen_random_uuid(),
    measurement_id uuid not null references measurement(id) on delete cascade,
    cost_code_id uuid references cost_code(id),
    description varchar(300) not null,
    quantity numeric(14,4) not null,
    unit_price numeric(14,4) not null,
    created_at timestamptz not null default now()
);

create index idx_measurement_item_measurement on measurement_item(measurement_id);
