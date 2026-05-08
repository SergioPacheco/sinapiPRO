-- Cost codes (hierarchical: division → phase → cost type)
create table cost_code (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id) on delete cascade,
    parent_id uuid references cost_code(id) on delete cascade,
    code varchar(30) not null,
    name varchar(200) not null,
    budgeted_amount numeric(18,2) not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (budget_id, code)
);

create index idx_cost_code_budget on cost_code(budget_id);
create index idx_cost_code_parent on cost_code(parent_id);

-- Cost transactions (actual, committed, budget adjustment)
create table cost_transaction (
    id uuid primary key default gen_random_uuid(),
    cost_code_id uuid not null references cost_code(id) on delete cascade,
    type varchar(20) not null,
    amount numeric(18,2) not null,
    description varchar(300),
    reference_id uuid,
    transaction_date date not null,
    created_at timestamptz not null default now()
);

create index idx_cost_transaction_code on cost_transaction(cost_code_id);
create index idx_cost_transaction_type on cost_transaction(type);
create index idx_cost_transaction_date on cost_transaction(transaction_date);
