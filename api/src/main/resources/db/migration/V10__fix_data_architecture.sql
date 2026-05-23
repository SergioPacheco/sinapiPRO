-- ============================================================
-- V10: fix-data-architecture — project_id FK + backfill + indexes
-- ============================================================

-- PARTE 1: Adicionar project_id nas tabelas operacionais
ALTER TABLE daily_log ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE measurement ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE safety_inspection ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE equipment_usage ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE equipment_fueling ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE purchase_request ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE purchase_order ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE rfi ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE punch_list_item ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE submittal ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE weather_delay ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE timesheet_entry ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE stock_item ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE stock_requisition ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE payable ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);
ALTER TABLE receivable ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES project(id);

-- PARTE 2: Backfill project_id via budget.project_id
UPDATE daily_log SET project_id = b.project_id FROM budget b WHERE daily_log.budget_id = b.id AND daily_log.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE measurement SET project_id = b.project_id FROM budget b WHERE measurement.budget_id = b.id AND measurement.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE safety_inspection SET project_id = b.project_id FROM budget b WHERE safety_inspection.budget_id = b.id AND safety_inspection.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE equipment_usage SET project_id = b.project_id FROM budget b WHERE equipment_usage.budget_id = b.id AND equipment_usage.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE equipment_fueling SET project_id = b.project_id FROM budget b WHERE equipment_fueling.budget_id = b.id AND equipment_fueling.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE purchase_request SET project_id = b.project_id FROM budget b WHERE purchase_request.budget_id = b.id AND purchase_request.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE purchase_order SET project_id = b.project_id FROM budget b WHERE purchase_order.budget_id = b.id AND purchase_order.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE rfi SET project_id = b.project_id FROM budget b WHERE rfi.budget_id = b.id AND rfi.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE punch_list_item SET project_id = b.project_id FROM budget b WHERE punch_list_item.budget_id = b.id AND punch_list_item.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE submittal SET project_id = b.project_id FROM budget b WHERE submittal.budget_id = b.id AND submittal.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE weather_delay SET project_id = b.project_id FROM budget b WHERE weather_delay.budget_id = b.id AND weather_delay.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE timesheet_entry SET project_id = b.project_id FROM budget b WHERE timesheet_entry.budget_id = b.id AND timesheet_entry.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE stock_item SET project_id = b.project_id FROM budget b WHERE stock_item.budget_id = b.id AND stock_item.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE stock_requisition SET project_id = b.project_id FROM budget b WHERE stock_requisition.budget_id = b.id AND stock_requisition.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE payable SET project_id = b.project_id FROM budget b WHERE payable.budget_id = b.id AND payable.project_id IS NULL AND b.project_id IS NOT NULL;
UPDATE receivable SET project_id = b.project_id FROM budget b WHERE receivable.budget_id = b.id AND receivable.project_id IS NULL AND b.project_id IS NOT NULL;

-- PARTE 3: Indexes
CREATE INDEX IF NOT EXISTS idx_payable_project ON payable(project_id);
CREATE INDEX IF NOT EXISTS idx_receivable_project ON receivable(project_id);
CREATE INDEX IF NOT EXISTS idx_purchase_order_project ON purchase_order(project_id);
CREATE INDEX IF NOT EXISTS idx_measurement_project ON measurement(project_id);
CREATE INDEX IF NOT EXISTS idx_daily_log_project ON daily_log(project_id);
CREATE INDEX IF NOT EXISTS idx_safety_inspection_project ON safety_inspection(project_id);
CREATE INDEX IF NOT EXISTS idx_rfi_project ON rfi(project_id);
CREATE INDEX IF NOT EXISTS idx_punch_list_item_project ON punch_list_item(project_id);
