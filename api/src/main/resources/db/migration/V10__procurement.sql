-- Purchase requests, quotations, purchase orders, receiving
create table purchase_request (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id) on delete cascade,
    cost_code_id uuid references cost_code(id),
    description varchar(300) not null,
    quantity numeric(14,4) not null,
    unit varchar(20) not null,
    status varchar(20) not null default 'OPEN',
    requested_by varchar(140),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_purchase_request_budget on purchase_request(budget_id);

create table quotation (
    id uuid primary key default gen_random_uuid(),
    purchase_request_id uuid not null references purchase_request(id) on delete cascade,
    status varchar(20) not null default 'OPEN',
    deadline date,
    created_at timestamptz not null default now()
);

create table quotation_response (
    id uuid primary key default gen_random_uuid(),
    quotation_id uuid not null references quotation(id) on delete cascade,
    supplier_id uuid not null references supplier(id),
    unit_price numeric(14,4) not null,
    delivery_days integer,
    notes varchar(500),
    created_at timestamptz not null default now()
);

create index idx_quotation_response_quotation on quotation_response(quotation_id);

create table purchase_order (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id),
    supplier_id uuid not null references supplier(id),
    quotation_response_id uuid references quotation_response(id),
    number varchar(40) not null unique,
    description varchar(300) not null,
    quantity numeric(14,4) not null,
    unit_price numeric(14,4) not null,
    status varchar(20) not null default 'PENDING',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_purchase_order_budget on purchase_order(budget_id);

create table receiving (
    id uuid primary key default gen_random_uuid(),
    purchase_order_id uuid not null references purchase_order(id),
    quantity_received numeric(14,4) not null,
    received_at date not null,
    notes varchar(300),
    created_at timestamptz not null default now()
);

create index idx_receiving_po on receiving(purchase_order_id);
