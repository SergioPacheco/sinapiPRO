-- Purchase order: add expected delivery date and cost_code_id
ALTER TABLE purchase_order ADD COLUMN IF NOT EXISTS cost_code_id uuid REFERENCES cost_code(id);
ALTER TABLE purchase_order ADD COLUMN IF NOT EXISTS expected_delivery_date date;
CREATE INDEX IF NOT EXISTS idx_purchase_order_delivery ON purchase_order(expected_delivery_date) WHERE status IN ('PENDING', 'APPROVED', 'PARTIAL');
