-- Contracts
create table contract (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id) on delete cascade,
    supplier_id uuid not null references supplier(id),
    number varchar(40) not null,
    description varchar(300) not null,
    original_value numeric(18,2) not null,
    retention_pct numeric(5,4) not null default 0,
    status varchar(20) not null default 'DRAFT',
    start_date date not null,
    end_date date,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_contract_budget on contract(budget_id);
create index idx_contract_supplier on contract(supplier_id);

-- Contract items
create table contract_item (
    id uuid primary key default gen_random_uuid(),
    contract_id uuid not null references contract(id) on delete cascade,
    description varchar(300) not null,
    quantity numeric(14,4) not null,
    unit_price numeric(14,4) not null,
    created_at timestamptz not null default now()
);

create index idx_contract_item_contract on contract_item(contract_id);

-- Change orders (aditivos)
create table change_order (
    id uuid primary key default gen_random_uuid(),
    contract_id uuid not null references contract(id) on delete cascade,
    number integer not null,
    description varchar(500) not null,
    amount numeric(18,2) not null,
    status varchar(20) not null default 'PENDING',
    justification text,
    approved_at timestamptz,
    created_at timestamptz not null default now(),
    unique (contract_id, number)
);

create index idx_change_order_contract on change_order(contract_id);
