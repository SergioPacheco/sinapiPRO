-- Budget stages (etapas hierárquicas)
create table budget_stage (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id) on delete cascade,
    parent_id uuid references budget_stage(id) on delete cascade,
    name varchar(200) not null,
    sort_order integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_budget_stage_budget on budget_stage(budget_id);
create index idx_budget_stage_parent on budget_stage(parent_id);

-- Budget items (itens vinculados a composição SINAPI)
create table budget_item (
    id uuid primary key default gen_random_uuid(),
    stage_id uuid not null references budget_stage(id) on delete cascade,
    composition_id uuid not null references composition(id),
    quantity numeric(14,4) not null,
    unit_cost numeric(14,4) not null,
    bdi_pct numeric(6,4) not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_budget_item_stage on budget_item(stage_id);
create index idx_budget_item_composition on budget_item(composition_id);

-- BDI configuration per budget
create table bdi_config (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null unique references budget(id) on delete cascade,
    administration numeric(6,4) not null default 0,
    profit numeric(6,4) not null default 0,
    taxes numeric(6,4) not null default 0,
    social_charges numeric(6,4) not null default 0,
    financial_expenses numeric(6,4) not null default 0,
    risks numeric(6,4) not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
