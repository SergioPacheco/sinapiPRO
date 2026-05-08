-- Daily construction log
create table daily_log (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id) on delete cascade,
    log_date date not null,
    weather_morning varchar(30),
    weather_afternoon varchar(30),
    observations text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (budget_id, log_date)
);

create index idx_daily_log_budget on daily_log(budget_id);

create table daily_log_labor (
    id uuid primary key default gen_random_uuid(),
    daily_log_id uuid not null references daily_log(id) on delete cascade,
    worker_name varchar(140) not null,
    role varchar(80) not null,
    hours numeric(4,2) not null,
    created_at timestamptz not null default now()
);

create table daily_log_equipment (
    id uuid primary key default gen_random_uuid(),
    daily_log_id uuid not null references daily_log(id) on delete cascade,
    equipment_name varchar(140) not null,
    hours_used numeric(4,2) not null,
    hours_idle numeric(4,2) not null default 0,
    created_at timestamptz not null default now()
);

create table daily_log_occurrence (
    id uuid primary key default gen_random_uuid(),
    daily_log_id uuid not null references daily_log(id) on delete cascade,
    type varchar(40) not null,
    description text not null,
    created_at timestamptz not null default now()
);
