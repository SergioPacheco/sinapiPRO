-- Add desonerated flag to material_price to distinguish between
-- SINAPI "Desonerado" and "Não Desonerado" price tables.

ALTER TABLE material_price ADD COLUMN desonerated boolean NOT NULL DEFAULT false;

-- Drop old unique constraint and create new one including desonerated
ALTER TABLE material_price DROP CONSTRAINT IF EXISTS material_price_material_id_state_reference_month_key;
ALTER TABLE material_price DROP CONSTRAINT IF EXISTS uki1x06na1lrxesimyba0i31n72;

ALTER TABLE material_price ADD CONSTRAINT material_price_unique
    UNIQUE (material_id, state, reference_month, desonerated);

-- Recreate lookup index with desonerated
DROP INDEX IF EXISTS idx_material_price_lookup;
CREATE INDEX idx_material_price_lookup ON material_price (material_id, state, reference_month, desonerated);
