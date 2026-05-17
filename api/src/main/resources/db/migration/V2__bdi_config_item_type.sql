ALTER TABLE bdi_config
    ADD COLUMN IF NOT EXISTS item_type varchar(20) NOT NULL DEFAULT 'ALL';

UPDATE bdi_config
SET item_type = 'ALL'
WHERE item_type IS NULL OR trim(item_type) = '';

ALTER TABLE bdi_config
    DROP CONSTRAINT IF EXISTS bdi_config_budget_id_key;

ALTER TABLE bdi_config
    ADD CONSTRAINT uk_bdi_config_budget_item_type UNIQUE (budget_id, item_type);
