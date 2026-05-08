-- SINAPI Materials (insumos)
create table material (
    id uuid primary key default gen_random_uuid(),
    sinapi_code varchar(20) not null unique,
    description varchar(500) not null,
    unit varchar(20) not null,
    origin varchar(30) not null,
    search_vector tsvector generated always as (
        setweight(to_tsvector('portuguese', coalesce(sinapi_code, '')), 'A') ||
        setweight(to_tsvector('portuguese', coalesce(description, '')), 'B')
    ) stored,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_material_search on material using gin(search_vector);
create index idx_material_origin on material(origin);

-- Material prices by state and reference month
create table material_price (
    id uuid primary key default gen_random_uuid(),
    material_id uuid not null references material(id) on delete cascade,
    state char(2) not null,
    reference_month date not null,
    price numeric(14,4) not null,
    created_at timestamptz not null default now(),
    unique (material_id, state, reference_month)
);

create index idx_material_price_lookup on material_price(material_id, state, reference_month);

-- SINAPI Compositions (composições de serviço)
create table composition (
    id uuid primary key default gen_random_uuid(),
    sinapi_code varchar(20) not null unique,
    description varchar(500) not null,
    unit varchar(20) not null,
    group_name varchar(140),
    search_vector tsvector generated always as (
        setweight(to_tsvector('portuguese', coalesce(sinapi_code, '')), 'A') ||
        setweight(to_tsvector('portuguese', coalesce(description, '')), 'B')
    ) stored,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_composition_search on composition using gin(search_vector);
create index idx_composition_group on composition(group_name);

-- Composition items (insumos da composição com coeficiente)
create table composition_item (
    id uuid primary key default gen_random_uuid(),
    composition_id uuid not null references composition(id) on delete cascade,
    material_id uuid not null references material(id),
    coefficient numeric(14,6) not null,
    created_at timestamptz not null default now()
);

create index idx_composition_item_composition on composition_item(composition_id);
create index idx_composition_item_material on composition_item(material_id);
