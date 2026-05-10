-- Create project table and migrate sub-resources from budget_id to project_id

-- Add project_id to tables that were previously linked to budget
-- For now, budget remains as a sub-resource of project (orçamento da obra)
ALTER TABLE budget ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE contract ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE measurement ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE daily_log ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE schedule_activity ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE cost_code ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE quotation ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE purchase_order ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE safety_inspection ADD COLUMN IF NOT EXISTS project_id uuid;
ALTER TABLE safety_incident ADD COLUMN IF NOT EXISTS project_id uuid;

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_budget_project ON budget(project_id);
CREATE INDEX IF NOT EXISTS idx_contract_project ON contract(project_id);
CREATE INDEX IF NOT EXISTS idx_measurement_project ON measurement(project_id);
CREATE INDEX IF NOT EXISTS idx_daily_log_project ON daily_log(project_id);
CREATE INDEX IF NOT EXISTS idx_schedule_activity_project ON schedule_activity(project_id);
CREATE INDEX IF NOT EXISTS idx_cost_code_project ON cost_code(project_id);
