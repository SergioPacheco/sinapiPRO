-- Schedule activities
create table schedule_activity (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id) on delete cascade,
    name varchar(200) not null,
    planned_start date not null,
    planned_end date not null,
    actual_start date,
    actual_end date,
    weight numeric(6,4) not null default 0,
    progress_pct numeric(5,2) not null default 0,
    sort_order integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_schedule_activity_budget on schedule_activity(budget_id);

-- Activity dependencies (predecessors)
create table activity_dependency (
    id uuid primary key default gen_random_uuid(),
    predecessor_id uuid not null references schedule_activity(id) on delete cascade,
    successor_id uuid not null references schedule_activity(id) on delete cascade,
    type varchar(10) not null default 'FS',
    unique (predecessor_id, successor_id)
);
