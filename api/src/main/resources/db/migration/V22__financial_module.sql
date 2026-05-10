-- Financial module: accounts payable and accounts receivable
CREATE TABLE payable (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    supplier_id uuid REFERENCES supplier(id),
    purchase_order_id uuid REFERENCES purchase_order(id),
    measurement_id uuid REFERENCES measurement(id),
    description varchar(300) NOT NULL,
    amount numeric(18,2) NOT NULL,
    due_date date NOT NULL,
    paid_date date,
    paid_amount numeric(18,2),
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    category varchar(60),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_payable_budget ON payable(budget_id);
CREATE INDEX idx_payable_due_date ON payable(due_date) WHERE status = 'PENDING';

CREATE TABLE receivable (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    measurement_id uuid REFERENCES measurement(id),
    invoice_id uuid REFERENCES invoice(id),
    description varchar(300) NOT NULL,
    amount numeric(18,2) NOT NULL,
    due_date date NOT NULL,
    received_date date,
    received_amount numeric(18,2),
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    category varchar(60),
    notes varchar(500),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_receivable_budget ON receivable(budget_id);
CREATE INDEX idx_receivable_due_date ON receivable(due_date) WHERE status = 'PENDING';
