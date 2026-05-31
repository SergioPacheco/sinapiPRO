-- V4: Full-text search indexes (antes criados pelo DemoDataLoader)
-- Movidos para migration para garantir idempotência e rastreabilidade

ALTER TABLE composition ADD COLUMN IF NOT EXISTS search_vector tsvector;
ALTER TABLE material ADD COLUMN IF NOT EXISTS search_vector tsvector;

CREATE INDEX IF NOT EXISTS idx_composition_search ON composition USING gin(search_vector);
CREATE INDEX IF NOT EXISTS idx_material_search ON material USING gin(search_vector);
