-- V12: Add missing columns to notification table (entity has more fields than V1 schema)
ALTER TABLE notification ADD COLUMN IF NOT EXISTS severity varchar(20) NOT NULL DEFAULT 'INFO';
ALTER TABLE notification ADD COLUMN IF NOT EXISTS recipient varchar(140);
ALTER TABLE notification ADD COLUMN IF NOT EXISTS budget_id uuid;

-- daily_log also missing budget_id (entity references budget, table references project)
ALTER TABLE daily_log ADD COLUMN IF NOT EXISTS budget_id uuid;
