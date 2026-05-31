-- V13: Add missing columns for SINAPI import support

ALTER TABLE material ADD COLUMN IF NOT EXISTS source varchar(20) DEFAULT 'SINAPI';
ALTER TABLE material ADD COLUMN IF NOT EXISTS unit_cost numeric(18,4) DEFAULT 0;
ALTER TABLE material ADD COLUMN IF NOT EXISTS reference_date date;
ALTER TABLE composition ADD COLUMN IF NOT EXISTS unit_cost numeric(18,4) DEFAULT 0;
ALTER TABLE composition ADD COLUMN IF NOT EXISTS reference_date date;
ALTER TABLE material ADD COLUMN IF NOT EXISTS search_vector tsvector;
ALTER TABLE composition ADD COLUMN IF NOT EXISTS search_vector tsvector;

-- SINAPI data is loaded at runtime via DemoDataLoader (profile=dev) or via /sinapi import UI
