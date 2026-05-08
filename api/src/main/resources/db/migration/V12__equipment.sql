-- Equipment management
create table equipment (
    id uuid primary key default gen_random_uuid(),
    code varchar(30) not null unique,
    name varchar(200) not null,
    type varchar(60) not null,
    brand varchar(100),
    model varchar(100),
    year integer,
    license_plate varchar(20),
    hourly_cost numeric(14,4) not null default 0,
    status varchar(20) not null default 'AVAILABLE',
    current_hours numeric(10,2) not null default 0,
    current_km numeric(10,2) not null default 0,
    next_maintenance_hours numeric(10,2),
    next_maintenance_date date,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table equipment_usage (
    id uuid primary key default gen_random_uuid(),
    equipment_id uuid not null references equipment(id),
    budget_id uuid not null references budget(id),
    usage_date date not null,
    hours_used numeric(6,2) not null,
    km_used numeric(8,2) not null default 0,
    operator varchar(140),
    notes varchar(300),
    created_at timestamptz not null default now()
);

create index idx_equipment_usage_equipment on equipment_usage(equipment_id);
create index idx_equipment_usage_budget on equipment_usage(budget_id);

create table equipment_maintenance (
    id uuid primary key default gen_random_uuid(),
    equipment_id uuid not null references equipment(id),
    maintenance_date date not null,
    type varchar(40) not null,
    description varchar(500) not null,
    cost numeric(14,2) not null default 0,
    hours_at_maintenance numeric(10,2),
    km_at_maintenance numeric(10,2),
    created_at timestamptz not null default now()
);

create index idx_equipment_maintenance_equipment on equipment_maintenance(equipment_id);
