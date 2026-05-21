-- ============================================================
-- V6: Complete gaps from specs-and-tasks.md
-- ============================================================

-- 1.1 + 1.2: Budget reference_date, state, rounding_method, decimal_places
ALTER TABLE budget ADD COLUMN IF NOT EXISTS reference_date date;
ALTER TABLE budget ADD COLUMN IF NOT EXISTS state varchar(2);
ALTER TABLE budget ADD COLUMN IF NOT EXISTS rounding_method varchar(20) DEFAULT 'TRUNCATE';
ALTER TABLE budget ADD COLUMN IF NOT EXISTS decimal_places integer DEFAULT 4;

-- 1.4: Budget item memo (memória de cálculo)
CREATE TABLE IF NOT EXISTS budget_item_memo (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    budget_item_id uuid NOT NULL REFERENCES budget_item(id) ON DELETE CASCADE,
    lines jsonb NOT NULL DEFAULT '[]'::jsonb,
    result numeric(14,4) NOT NULL DEFAULT 0,
    notes text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT budget_item_memo_unique UNIQUE (budget_item_id)
);

-- 2.6: Measurement → change_order link
ALTER TABLE measurement ADD COLUMN IF NOT EXISTS change_order_id uuid REFERENCES change_order(id);

-- 3.2: Budget item custom_code (already in entity, ensure DDL)
ALTER TABLE budget_item ADD COLUMN IF NOT EXISTS custom_code varchar(30);
ALTER TABLE budget_item ADD COLUMN IF NOT EXISTS price_source varchar(20) DEFAULT 'SINAPI';

-- 4.5: Daily log tasks
CREATE TABLE IF NOT EXISTS daily_log_task (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id) ON DELETE CASCADE,
    activity_id uuid REFERENCES schedule_activity(id),
    description varchar(300) NOT NULL,
    progress_pct numeric(5,2),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- 4.6: Daily log materials (entrada/saída)
CREATE TABLE IF NOT EXISTS daily_log_material (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    daily_log_id uuid NOT NULL REFERENCES daily_log(id) ON DELETE CASCADE,
    type varchar(10) NOT NULL CHECK (type IN ('IN', 'OUT')),
    description varchar(200) NOT NULL,
    quantity numeric(14,4) NOT NULL,
    unit varchar(20),
    invoice_number varchar(40),
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- 5.1: Quotation email tracking
CREATE TABLE IF NOT EXISTS quotation_email (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_id uuid NOT NULL REFERENCES quotation(id) ON DELETE CASCADE,
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    recipient_email varchar(200) NOT NULL,
    subject varchar(300),
    status varchar(20) NOT NULL DEFAULT 'PENDING',
    sent_at timestamptz,
    error_message text,
    created_at timestamptz NOT NULL DEFAULT now()
);

-- 5.5: Measurement import tracking
ALTER TABLE measurement ADD COLUMN IF NOT EXISTS imported_from varchar(100);

-- 6.7: Digital signature on daily log
ALTER TABLE daily_log ADD COLUMN IF NOT EXISTS signed_by varchar(200);
ALTER TABLE daily_log ADD COLUMN IF NOT EXISTS signed_at timestamptz;
ALTER TABLE daily_log ADD COLUMN IF NOT EXISTS signature_hash varchar(128);

-- 6.6: Supplier portal tokens
CREATE TABLE IF NOT EXISTS supplier_portal_token (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    token varchar(64) NOT NULL UNIQUE,
    quotation_id uuid NOT NULL REFERENCES quotation(id) ON DELETE CASCADE,
    supplier_id uuid NOT NULL REFERENCES supplier(id),
    expires_at timestamptz NOT NULL,
    used_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_supplier_portal_token_token ON supplier_portal_token(token);
