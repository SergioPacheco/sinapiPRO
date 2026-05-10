-- Broker commissions
CREATE TABLE broker_commission (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    proposal_id uuid NOT NULL REFERENCES sales_proposal(id),
    broker_name varchar(200) NOT NULL,
    percentage numeric(5,4) NOT NULL,
    amount numeric(18,2) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    paid_date date,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_commission_proposal ON broker_commission(proposal_id);

-- Equipment fueling
CREATE TABLE equipment_fueling (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id uuid NOT NULL REFERENCES equipment(id),
    budget_id uuid NOT NULL REFERENCES budget(id),
    fueling_date date NOT NULL,
    fuel_type varchar(30) NOT NULL,
    liters numeric(8,2) NOT NULL,
    cost_per_liter numeric(8,4) NOT NULL,
    total_cost numeric(14,2) NOT NULL,
    odometer numeric(10,2),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_fueling_equipment ON equipment_fueling(equipment_id);

-- Document versions
CREATE TABLE document_version (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id uuid NOT NULL REFERENCES document(id) ON DELETE CASCADE,
    version_number integer NOT NULL,
    file_path varchar(500) NOT NULL,
    uploaded_by varchar(140),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now(),
    UNIQUE(document_id, version_number)
);

-- Inventory / Stock
CREATE TABLE stock_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    description varchar(300) NOT NULL,
    unit varchar(20) NOT NULL,
    current_quantity numeric(14,4) NOT NULL DEFAULT 0,
    min_quantity numeric(14,4) NOT NULL DEFAULT 0,
    location varchar(100),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_stock_item_budget ON stock_item(budget_id);

CREATE TABLE stock_movement (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_item_id uuid NOT NULL REFERENCES stock_item(id),
    type varchar(20) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    reference_id uuid,
    reference_type varchar(40),
    notes varchar(300),
    moved_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_stock_movement_item ON stock_movement(stock_item_id);

CREATE TABLE stock_requisition (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id),
    requested_by varchar(140) NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE stock_requisition_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    requisition_id uuid NOT NULL REFERENCES stock_requisition(id) ON DELETE CASCADE,
    stock_item_id uuid NOT NULL REFERENCES stock_item(id),
    quantity numeric(14,4) NOT NULL,
    delivered_quantity numeric(14,4) NOT NULL DEFAULT 0
);

-- Purchase order: cost code distribution
CREATE TABLE purchase_order_cost_distribution (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id uuid NOT NULL REFERENCES purchase_order(id) ON DELETE CASCADE,
    cost_code_id uuid NOT NULL REFERENCES cost_code(id),
    percentage numeric(5,4) NOT NULL,
    amount numeric(18,2) NOT NULL
);

CREATE INDEX idx_po_cost_dist ON purchase_order_cost_distribution(purchase_order_id);
