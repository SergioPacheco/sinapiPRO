-- Add origin column to composition to distinguish SINAPI vs custom compositions
ALTER TABLE composition ADD COLUMN origin varchar(20) NOT NULL DEFAULT 'SINAPI';

CREATE INDEX idx_composition_origin ON composition (origin);
