ALTER TABLE measurement_item ADD COLUMN IF NOT EXISTS budget_item_id uuid REFERENCES budget_item(id);
CREATE INDEX IF NOT EXISTS idx_measurement_item_budget_item ON measurement_item(budget_item_id);
