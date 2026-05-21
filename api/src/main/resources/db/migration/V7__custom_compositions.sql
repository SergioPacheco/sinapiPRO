-- ============================================================
-- V7: Custom Compositions — Polimorfismo + Versionamento
-- ============================================================

-- ============================================================
-- PARTE 1: Campos de versionamento na tabela composition
-- ============================================================

ALTER TABLE composition ADD COLUMN version integer NOT NULL DEFAULT 1;
ALTER TABLE composition ADD COLUMN parent_id uuid REFERENCES composition(id);
ALTER TABLE composition ADD COLUMN is_current boolean NOT NULL DEFAULT true;

-- Remover constraint UNIQUE de sinapi_code (agora pode ter múltiplas versões)
ALTER TABLE composition DROP CONSTRAINT composition_sinapi_code_key;

-- Criar índice único parcial: sinapi_code único apenas entre versões correntes
CREATE UNIQUE INDEX idx_composition_sinapi_code_current
    ON composition(sinapi_code) WHERE is_current = true;

-- Índice para busca por cadeia de versões
CREATE INDEX idx_composition_parent ON composition(parent_id);
CREATE INDEX idx_composition_current ON composition(is_current) WHERE is_current = true;

-- ============================================================
-- PARTE 2: Polimorfismo em composition_item
-- ============================================================

-- Tornar material_id nullable (agora pode ser composição auxiliar)
ALTER TABLE composition_item ALTER COLUMN material_id DROP NOT NULL;

-- Adicionar referência para composição auxiliar
ALTER TABLE composition_item ADD COLUMN child_composition_id uuid REFERENCES composition(id);

-- Adicionar tipo de insumo (discriminador)
ALTER TABLE composition_item ADD COLUMN item_type varchar(20) NOT NULL DEFAULT 'MATERIAL';

-- Constraint de exclusividade mútua: material XOR composição auxiliar
ALTER TABLE composition_item ADD CONSTRAINT chk_item_reference_xor
    CHECK (
        (material_id IS NOT NULL AND child_composition_id IS NULL)
        OR (material_id IS NULL AND child_composition_id IS NOT NULL)
    );

-- Constraint de valores válidos para item_type
ALTER TABLE composition_item ADD CONSTRAINT chk_item_type_valid
    CHECK (item_type IN ('MATERIAL', 'LABOR', 'EQUIPMENT', 'COMPOSITION'));

-- Índice para child_composition_id
CREATE INDEX idx_composition_item_child ON composition_item(child_composition_id)
    WHERE child_composition_id IS NOT NULL;
