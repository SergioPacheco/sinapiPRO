-- ============================================================
-- V9: Sprint 6 — Polish (trash, proposals, tags, social charges, report templates)
-- ============================================================

-- 6.3: Lixeira genérica
CREATE TABLE trash_item (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type varchar(40) NOT NULL,
    entity_id uuid NOT NULL,
    entity_name varchar(200),
    deleted_by varchar(140),
    snapshot jsonb,
    deleted_at timestamptz NOT NULL DEFAULT now(),
    expires_at timestamptz
);

CREATE INDEX idx_trash_item_type ON trash_item(entity_type);
CREATE INDEX idx_trash_item_expires ON trash_item(expires_at) WHERE expires_at IS NOT NULL;

-- 6.1: Propostas para pregão
CREATE TABLE budget_proposal (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    description varchar(100) NOT NULL,
    discount_pct numeric(6,4) NOT NULL,
    original_value numeric(18,2) NOT NULL,
    proposed_value numeric(18,2) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_budget_proposal_budget ON budget_proposal(budget_id);

-- 6.2: Tags em itens do orçamento
CREATE TABLE budget_item_tag (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_item_id uuid NOT NULL REFERENCES budget_item(id) ON DELETE CASCADE,
    tag varchar(50) NOT NULL,
    color varchar(7),
    CONSTRAINT uq_budget_item_tag UNIQUE (budget_item_id, tag)
);

CREATE INDEX idx_budget_item_tag_item ON budget_item_tag(budget_item_id);
CREATE INDEX idx_budget_item_tag_tag ON budget_item_tag(tag);

-- 6.5: Encargos sociais configuráveis
CREATE TABLE social_charges_config (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_id uuid NOT NULL REFERENCES budget(id) ON DELETE CASCADE,
    worker_type varchar(30) NOT NULL,
    tax_regime varchar(30) NOT NULL DEFAULT 'NORMAL',
    inss_pct numeric(6,4) DEFAULT 0,
    fgts_pct numeric(6,4) DEFAULT 0,
    vacation_pct numeric(6,4) DEFAULT 0,
    thirteenth_pct numeric(6,4) DEFAULT 0,
    other_pct numeric(6,4) DEFAULT 0,
    CONSTRAINT uq_social_charges UNIQUE (budget_id, worker_type)
);

-- 6.4: Personalização de relatórios
CREATE TABLE IF NOT EXISTS report_template (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(100) NOT NULL,
    type varchar(30),
    description varchar(500),
    logo_path varchar(500),
    header_text varchar(500),
    footer_text varchar(500),
    primary_color varchar(7) DEFAULT '#1e3a5f',
    settings jsonb,
    created_at timestamptz NOT NULL DEFAULT now()
);
