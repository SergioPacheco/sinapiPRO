-- Submittals (document approval workflow)
create table submittal (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id),
    number integer not null,
    title varchar(300) not null,
    spec_section varchar(60),
    type varchar(40) not null,
    submitted_by varchar(140),
    assigned_to varchar(140),
    status varchar(20) not null default 'DRAFT',
    due_date date,
    submitted_at timestamptz,
    reviewed_at timestamptz,
    reviewer_notes text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique(budget_id, number)
);
create index idx_submittal_budget on submittal(budget_id);

-- Weather Delay Tracking
create table weather_delay (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id),
    delay_date date not null,
    weather_condition varchar(60) not null,
    hours_lost numeric(4,2) not null,
    full_day_lost boolean not null default false,
    impact_description varchar(500),
    reported_by varchar(140),
    created_at timestamptz not null default now(),
    unique(budget_id, delay_date)
);
create index idx_weather_delay_budget on weather_delay(budget_id);

-- Time Tracking (timesheets)
create table timesheet_entry (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid not null references budget(id),
    cost_code_id uuid references cost_code(id),
    worker_name varchar(140) not null,
    role varchar(80) not null,
    work_date date not null,
    regular_hours numeric(4,2) not null default 0,
    overtime_hours numeric(4,2) not null default 0,
    hourly_rate numeric(10,2) not null default 0,
    units_produced numeric(10,2),
    unit_type varchar(30),
    notes varchar(300),
    created_at timestamptz not null default now()
);
create index idx_timesheet_budget on timesheet_entry(budget_id);
create index idx_timesheet_date on timesheet_entry(work_date);

-- Notifications
create table notification (
    id uuid primary key default gen_random_uuid(),
    budget_id uuid,
    type varchar(40) not null,
    severity varchar(20) not null default 'INFO',
    title varchar(300) not null,
    message text not null,
    entity_type varchar(40),
    entity_id uuid,
    recipient varchar(140),
    read boolean not null default false,
    created_at timestamptz not null default now()
);
create index idx_notification_recipient on notification(recipient, read);
create index idx_notification_budget on notification(budget_id);
