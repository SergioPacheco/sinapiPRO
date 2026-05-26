-- V6: Sprint 10 — Cheques recebidos e custódia

CREATE TABLE IF NOT EXISTS check_received (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id uuid,
    bank_code varchar(10) NOT NULL,
    agency varchar(20) NOT NULL,
    account_number varchar(30) NOT NULL,
    check_number varchar(20) NOT NULL,
    amount numeric(18,2) NOT NULL,
    issue_date date NOT NULL,
    due_date date,
    issuer_name varchar(200) NOT NULL,
    issuer_document varchar(20),
    receivable_installment_id uuid,
    custody_bank_account_id uuid,
    custody_date date,
    cleared_date date,
    returned_date date,
    return_reason varchar(200),
    status varchar(20) NOT NULL DEFAULT 'RECEIVED',
    notes varchar(300),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_check_received_status ON check_received(status);
CREATE INDEX IF NOT EXISTS idx_check_received_custody ON check_received(custody_bank_account_id) WHERE status = 'IN_CUSTODY';
