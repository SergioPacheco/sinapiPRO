create extension if not exists "pgcrypto";

create table budget (
    id uuid primary key,
    code varchar(40) not null unique,
    title varchar(140) not null,
    customer_name varchar(140) not null,
    total_amount numeric(18,2) not null,
    status varchar(30) not null,
    start_date date not null,
    end_date date,
    metadata jsonb not null default '{}'::jsonb,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table supplier (
    id uuid primary key,
    code varchar(40) not null unique,
    name varchar(140) not null,
    trade_name varchar(140),
    tax_id varchar(30) not null unique,
    email varchar(140),
    phone varchar(40),
    rating integer not null,
    active boolean not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table invoice (
    id uuid primary key,
    number varchar(40) not null unique,
    budget_id uuid not null references budget(id),
    supplier_id uuid not null references supplier(id),
    amount numeric(18,2) not null,
    issue_date date not null,
    due_date date not null,
    status varchar(30) not null,
    notes text,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index idx_budget_status on budget(status);
create index idx_budget_customer_name on budget(customer_name);
create index idx_supplier_active on supplier(active);
create index idx_invoice_status on invoice(status);
create index idx_invoice_due_date on invoice(due_date);
create index idx_invoice_budget on invoice(budget_id);
create index idx_invoice_supplier on invoice(supplier_id);

create view showcase_portfolio_summary as
select
    (select count(*) from budget) as total_budgets,
    (select coalesce(sum(total_amount), 0) from budget) as total_budget_amount,
    (select count(*) from supplier where active = true) as active_suppliers,
    (select count(*) from invoice where status in ('PENDING', 'OVERDUE')) as open_invoices,
    (select coalesce(sum(amount), 0) from invoice where status = 'OVERDUE') as overdue_invoice_amount;
