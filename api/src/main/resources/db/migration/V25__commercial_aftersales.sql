-- Commercial module: developments, units, proposals
CREATE TABLE development (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(200) NOT NULL,
    address varchar(400),
    city varchar(100),
    state varchar(2),
    total_units integer NOT NULL DEFAULT 0,
    status varchar(20) NOT NULL DEFAULT 'PLANNING',
    launch_date date,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE development_unit (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    development_id uuid NOT NULL REFERENCES development(id) ON DELETE CASCADE,
    code varchar(30) NOT NULL,
    type varchar(40) NOT NULL,
    area numeric(10,2),
    price numeric(18,2) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'AVAILABLE',
    floor integer,
    bedrooms integer,
    notes varchar(300),
    UNIQUE(development_id, code)
);

CREATE INDEX idx_dev_unit_development ON development_unit(development_id);
CREATE INDEX idx_dev_unit_status ON development_unit(status);

CREATE TABLE sales_proposal (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id uuid NOT NULL REFERENCES development_unit(id),
    client_id uuid REFERENCES client(id),
    client_name varchar(200) NOT NULL,
    proposal_date date NOT NULL,
    proposed_price numeric(18,2) NOT NULL,
    down_payment numeric(18,2),
    installments integer DEFAULT 1,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_proposal_unit ON sales_proposal(unit_id);

-- After-sales module: service tickets
CREATE TABLE service_ticket (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id uuid REFERENCES development_unit(id),
    client_name varchar(200) NOT NULL,
    category varchar(60) NOT NULL,
    description text NOT NULL,
    priority varchar(20) NOT NULL DEFAULT 'MEDIUM',
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    assigned_to varchar(140),
    resolution text,
    opened_at timestamptz NOT NULL DEFAULT now(),
    due_date date,
    resolved_at timestamptz,
    closed_at timestamptz
);

CREATE INDEX idx_ticket_status ON service_ticket(status);
CREATE INDEX idx_ticket_unit ON service_ticket(unit_id);
